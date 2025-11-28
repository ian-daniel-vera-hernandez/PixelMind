package com.example.logueocris
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
class ZpantallaRegistro : AppCompatActivity() {
    private val apiService: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl("http://10.114.10.44/") // Asegúrate que esta IP sea correcta
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_pantalla_registro)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val btnIniciarSesion: Button = findViewById(R.id.btnini)
        btnIniciarSesion.setOnClickListener {
            navegarALogin() // Si el usuario ya tiene cuenta, va al Login
        }

        val edTUsuario: EditText = findViewById(R.id.edtxtus)
        val edTNombre: EditText = findViewById(R.id.edtxtnombre)
        val edTCorreo: EditText = findViewById(R.id.edtxtmail)
        val btnRegistrarme: Button = findViewById(R.id.btnreg)

        btnRegistrarme.setOnClickListener {
            val usuario = edTUsuario.text.toString().trim()
            val nombre = edTNombre.text.toString().trim()
            val correo = edTCorreo.text.toString().trim()
            val contrasenaPorDefecto = "password123"

            if (usuario.isEmpty() || nombre.isEmpty() || correo.isEmpty()) {
                Toast.makeText(this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // --- 4. PRIMERA LLAMADA: Registrar el usuario ---
            Log.d("REGISTRO", "Iniciando registro para: $usuario, $correo")
            val registroRequest = RegistroRequest(usuario, correo, contrasenaPorDefecto)

            apiService.registrarUsuario(registroRequest).enqueue(object : Callback<AuthResponse> {
                override fun onResponse(call: Call<AuthResponse>, response: Response<AuthResponse>) {

                    if (response.isSuccessful) {
                        val authResponse = response.body()
                        Log.d("REGISTRO", "Respuesta del servidor (registro): $authResponse")

                        if (authResponse?.status == "success" && authResponse.usuarioId != null) {
                            Toast.makeText(this@ZpantallaRegistro, "Usuario creado. Guardando perfil...", Toast.LENGTH_SHORT).show()

                            Log.d("REGISTRO", "Iniciando guardado de perfil para ID: ${authResponse.usuarioId}")
                            val perfilRequest = PerfilRequest(authResponse.usuarioId, nombre, correo)

                            // --- SEGUNDA LLAMADA: Guardar Perfil ---
                            apiService.guardarPerfil(perfilRequest).enqueue(object: Callback<AuthResponse> {
                                override fun onResponse(call: Call<AuthResponse>, response: Response<AuthResponse>) {
                                    Log.d("REGISTRO", "Respuesta del servidor (perfil): ${response.body()}")

                                    if (response.isSuccessful) {
                                        Toast.makeText(this@ZpantallaRegistro, "Registro Exitoso: " + response.body()?.message, Toast.LENGTH_SHORT).show()

                                        // ---> CAMBIO AQUÍ: Ahora vamos al Chat <---
                                        navegarAChatPrincipal()
                                    } else {
                                        Toast.makeText(this@ZpantallaRegistro, "Error al guardar perfil (HTTP ${response.code()})", Toast.LENGTH_SHORT).show()
                                        // Si falló el perfil pero el usuario se creó, igual podrías querer mandarlo al login
                                        navegarALogin()
                                    }
                                }
                                override fun onFailure(call: Call<AuthResponse>, t: Throwable) {
                                    Log.e("REGISTRO", "ERROR FATAL: ${t.message}", t)
                                    Toast.makeText(this@ZpantallaRegistro, "ERROR: ${t.message}", Toast.LENGTH_LONG).show()
                                }
                            })

                        } else {
                            Log.w("REGISTRO", "El registro del usuario falló. Razón: ${authResponse?.message}")
                            Toast.makeText(this@ZpantallaRegistro, "No se pudo registrar: ${authResponse?.message}", Toast.LENGTH_LONG).show()
                        }
                    } else {
                        Log.e("REGISTRO", "Error HTTP en el registro: ${response.code()}")
                        Toast.makeText(this@ZpantallaRegistro, "Error del servidor: ${response.code()}", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<AuthResponse>, t: Throwable) {
                    Log.e("REGISTRO", "FALLO en la llamada de registro: ", t)
                    Toast.makeText(this@ZpantallaRegistro, "Fallo de conexión: ${t.message}", Toast.LENGTH_LONG).show()
                }
            })
        }
    }

    // Navegación antigua (para el botón "Ya tengo cuenta")
    private fun navegarALogin() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    // ---> NUEVA FUNCIÓN <---
    // Esta función te lleva directamente al Chat Principal
    private fun navegarAChatPrincipal() {
        val intent = Intent(this, ChatPrincipal::class.java)
        // Si quisieras pasar el nombre del usuario al chat, descomenta la siguiente línea:
        // intent.putExtra("NOMBRE_USUARIO", "NombreAqui")
        startActivity(intent)
        finish() // Cierra el registro para que no puedan volver atrás
    }
}