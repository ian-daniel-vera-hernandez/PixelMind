package com.example.logueocris
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import kotlinx.coroutines.launch

class ChatPrincipal : AppCompatActivity(), OnMessageActionListener {

    // --- ADAPTADORES ---
    private lateinit var chatAdapter: ChatAdapter
    private lateinit var historyAdapter: HistoryAdapter // Nuevo adaptador para el menú

    // --- LISTAS DE DATOS ---
    private val messageList = mutableListOf<ChatMessage>() // Mensajes en pantalla
    private val historyList = mutableListOf<ChatSession>() // Lista de conversaciones guardadas

    // --- GEMINI ---
    private lateinit var generativeModel: GenerativeModel

    // --- VISTAS ---
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var chatRecyclerView: RecyclerView
    private lateinit var menuRecyclerView: RecyclerView // Recycler del menú lateral

    // Variable para saber si estamos editando una sesión vieja o una nueva
    private var currentSessionId: Long? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_pantalla2)
        supportActionBar?.hide()

        // 1. Vinculación de Vistas
        drawerLayout = findViewById(R.id.drawer_layout)
        chatRecyclerView = findViewById(R.id.chat_recycler_view)
        menuRecyclerView = findViewById(R.id.menuRecyclerView) // ID del XML que proporcionaste
        val mainContent = findViewById<ConstraintLayout>(R.id.main_content)

        val menuButton = findViewById<ImageButton>(R.id.menu_button)
        val messageInput = findViewById<EditText>(R.id.message_edit_text)
        val sendButton = findViewById<ImageButton>(R.id.send_message_button)
        val btnCerrarSesion = findViewById<Button>(R.id.logoutButton)
        val btnNuevaConversacion = findViewById<Button>(R.id.newConversationButton)

        // 2. Configuración de Insets (Teclado)
        ViewCompat.setOnApplyWindowInsetsListener(mainContent) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            val imeInsets = insets.getInsets(WindowInsetsCompat.Type.ime())
            val bottomPadding = if (imeInsets.bottom > 0) imeInsets.bottom else systemBars.bottom
            view.setPadding(systemBars.left, systemBars.top, systemBars.right, bottomPadding)
            insets
        }

        // 3. Configuración de Gemini
        generativeModel = GenerativeModel(
            modelName = "gemini-2.5-flash",
            apiKey = BuildConfig.GEMINI_API_KEY,
            systemInstruction = content {
                text("""
            Tu nombre es PixelMind. Eres un asistente experto y fanático EXCLUSIVAMENTE de tres videojuegos:
            1. Brawl Stars
            2. My Hero Ultra Rumble
            3. Super Smash Bros. Ultimate
            
            REGLAS ESTRICTAS DE COMPORTAMIENTO:
            - Si el usuario te pregunta sobre estrategias, personajes, actualizaciones o dudas de ESTOS TRES JUEGOS, responde con entusiasmo, emojis y detalles técnicos.
            - Si el usuario te pregunta sobre CUALQUIER OTRO TEMA (matemáticas, historia, cocina, política, o incluso otros videojuegos como Fortnite, Minecraft, etc.), DEBES RECHAZAR LA RESPUESTA AMABLEMENTE.
            
            Ejemplo de rechazo: "Lo siento, mi sistema solo me permite analizar combates de Brawl Stars, My Hero Ultra Rumble y Smash Bros. ¿Hablamos de eso?"
            
            Nunca rompas el personaje. No des consejos de vida ni resuelvas tareas escolares. Solo gaming de esos 3 títulos.
            """.trimIndent())
            }
        )

        // 4. Configuración del CHAT PRINCIPAL (RecyclerView)
        chatAdapter = ChatAdapter(messageList, this)
        chatRecyclerView.layoutManager = LinearLayoutManager(this).apply { stackFromEnd = true }
        chatRecyclerView.adapter = chatAdapter

        // 5. Configuración del HISTORIAL (Menú Lateral)
        historyAdapter = HistoryAdapter(historyList) { sessionSeleccionada ->
            // --- LÓGICA AL TOCAR UNA CONVERSACIÓN ANTIGUA ---
            cargarConversacionAntigua(sessionSeleccionada)
        }
        menuRecyclerView.layoutManager = LinearLayoutManager(this)
        menuRecyclerView.adapter = historyAdapter

        // 6. Listeners de Botones

        menuButton.setOnClickListener { drawerLayout.openDrawer(GravityCompat.START) }

        sendButton.setOnClickListener {
            val prompt = messageInput.text.toString()
            if (prompt.isNotBlank()) {
                enviarMensajeAGemini(prompt)
                messageInput.setText("")
            } else {
                Toast.makeText(this, "Escribe un mensaje", Toast.LENGTH_SHORT).show()
            }
        }

        btnCerrarSesion.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }

        // --- BOTÓN NUEVA CONVERSACIÓN ---
        btnNuevaConversacion.setOnClickListener {
            // 1. Guardamos la conversación actual si no está vacía
            guardarConversacionActual()

            // 2. Limpiamos la pantalla para empezar de cero
            currentSessionId = null // Es una sesión nueva
            messageList.clear()
            chatAdapter.notifyDataSetChanged()

            // 3. Cerramos el menú
            drawerLayout.closeDrawer(GravityCompat.START)
            Toast.makeText(this, "Nueva conversación iniciada", Toast.LENGTH_SHORT).show()
        }

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    drawerLayout.closeDrawer(GravityCompat.START)
                } else {
                    finish()
                }
            }
        })
    }

    // ---------------------------------------------------------
    // LÓGICA DE GUARDADO Y CARGA DE HISTORIAL
    // ---------------------------------------------------------

    private fun guardarConversacionActual() {
        if (messageList.isEmpty()) return // No guardamos chats vacíos

        // Creamos una copia de la lista actual para guardarla
        val listaAGuardar = ArrayList(messageList)

        // Título: Usamos los primeros 25 caracteres del primer mensaje del usuario
        val primerMensaje = listaAGuardar.firstOrNull { it.isUser }?.text ?: "Conversación sin título"
        val titulo = if (primerMensaje.length > 25) primerMensaje.take(25) + "..." else primerMensaje

        if (currentSessionId == null) {
            // ES UNA CONVERSACIÓN NUEVA -> Creamos nuevo registro en historial
            val nuevaSesion = ChatSession(
                id = System.currentTimeMillis(),
                title = titulo,
                messages = listaAGuardar
            )
            // Agregamos al principio de la lista del menú (índice 0)
            historyList.add(0, nuevaSesion)
            historyAdapter.notifyItemInserted(0)

        } else {
            // ES UNA CONVERSACIÓN VIEJA -> Actualizamos la existente en el historial
            val index = historyList.indexOfFirst { it.id == currentSessionId }
            if (index != -1) {
                // Reemplazamos la sesión vieja con la versión actualizada
                historyList[index] = historyList[index].copy(messages = listaAGuardar, title = titulo)
                historyAdapter.notifyItemChanged(index)
            }
        }
    }

    private fun cargarConversacionAntigua(session: ChatSession) {
        // 1. Antes de cambiar, guardamos lo que tenemos en pantalla actualmente
        guardarConversacionActual()

        // 2. Cargamos los datos de la sesión seleccionada
        currentSessionId = session.id
        messageList.clear()
        messageList.addAll(session.messages)

        // 3. Refrescamos el chat
        chatAdapter.notifyDataSetChanged()

        // 4. Scrolleamos al final
        if (messageList.isNotEmpty()) {
            chatRecyclerView.scrollToPosition(messageList.size - 1)
        }

        // 5. Cerramos el menú
        drawerLayout.closeDrawer(GravityCompat.START)
        Toast.makeText(this, "Conversación cargada", Toast.LENGTH_SHORT).show()
    }

    // ---------------------------------------------------------
    // FUNCIONES AUXILIARES CHAT
    // ---------------------------------------------------------

    private fun enviarMensajeAGemini(prompt: String) {
        addMessageToChat(prompt, isUser = true)
        lifecycleScope.launch {
            try {
                val response = generativeModel.generateContent(prompt)
                val responseText = response.text ?: "..."
                addMessageToChat(responseText, isUser = false)
            } catch (e: Exception) {
                addMessageToChat("Error: ${e.message}", isUser = false)
            }
        }
    }

    private fun addMessageToChat(text: String, isUser: Boolean) {
        messageList.add(ChatMessage(text, isUser))
        chatAdapter.notifyItemInserted(messageList.size - 1)
        chatRecyclerView.scrollToPosition(messageList.size - 1)

        // OPCIONAL: Guardar automáticamente en cada mensaje para no perder nada si la app se cierra
        // guardarConversacionActual()
    }

    // Implementación de la interfaz
    override fun onCopy(text: String) {
        val clipboard = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("PixelMind", text)
        clipboard.setPrimaryClip(clip)
        Toast.makeText(this, "Copiado", Toast.LENGTH_SHORT).show()
    }

    override fun onRetry(prompt: String) {
        enviarMensajeAGemini(prompt)
    }

    override fun onLike() {}
    override fun onDislike() {}
}