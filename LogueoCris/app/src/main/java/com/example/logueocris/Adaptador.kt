package com.example.logueocris

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

// Interfaz para manejar los clics de los botones desde la Activity
interface OnMessageActionListener {
    fun onCopy(text: String)
    fun onRetry(prompt: String) // Opcional: Lógica para reintentar
    fun onLike()
    fun onDislike()
}

class ChatAdapter(
    private val messageList: List<ChatMessage>,
    private val listener: OnMessageActionListener // Agregamos el listener
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val VIEW_TYPE_USER = 1
        private const val VIEW_TYPE_AI = 2
    }

    override fun getItemViewType(position: Int): Int {
        return if (messageList[position].isUser) VIEW_TYPE_USER else VIEW_TYPE_AI
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE_USER) {
            // Asegúrate de tener un layout simple para el usuario (ej. item_chat_user.xml)
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_message_user, parent, false)
            UserViewHolder(view)
        } else {
            // Aquí cargamos TU XML con los botones (asumiendo que se llama item_chat_ai.xml)
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_message_ai, parent, false)
            AiViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val message = messageList[position]

        if (holder is UserViewHolder) {
            holder.bind(message)
        } else if (holder is AiViewHolder) {
            holder.bind(message, listener)
        }
    }

    override fun getItemCount(): Int = messageList.size

    // ViewHolder para el Usuario (Texto simple)
    class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val textView: TextView = itemView.findViewById(R.id.user_message_text)
        // Nota: Asegúrate que en item_chat_user.xml el ID sea user_message_text

        fun bind(message: ChatMessage) {
            textView.text = message.text
        }
    }

    // ViewHolder para la IA (Tu diseño complejo)
    class AiViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val textView: TextView = itemView.findViewById(R.id.ai_message_text)
        private val btnCopy: ImageButton = itemView.findViewById(R.id.share_button) // Usamos share como copiar
        private val btnRetry: ImageButton = itemView.findViewById(R.id.retry_button)
        private val btnLike: ImageButton = itemView.findViewById(R.id.thumb_up_button)
        private val btnDislike: ImageButton = itemView.findViewById(R.id.thumb_down_button)

        fun bind(message: ChatMessage, listener: OnMessageActionListener) {
            textView.text = message.text
            // Configurar clics
            btnCopy.setOnClickListener { listener.onCopy(message.text) }
            btnRetry.setOnClickListener { listener.onRetry(message.text) }
            btnLike.setOnClickListener { listener.onLike() }
            btnDislike.setOnClickListener { listener.onDislike() }
        }
    }
}