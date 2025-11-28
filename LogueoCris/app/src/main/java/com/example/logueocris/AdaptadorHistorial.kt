package com.example.logueocris

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

// Clase de datos simple para guardar una sesión
data class ChatSession(
    val id: Long,
    val title: String,
    val messages: List<ChatMessage> // Guarda la lista completa de mensajes
)

class HistoryAdapter(
    private val historyList: List<ChatSession>,
    private val onSessionClick: (ChatSession) -> Unit
) : RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder>() {

    class HistoryViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView = view.findViewById(R.id.historyTitle)
        val subtitle: TextView = view.findViewById(R.id.historySubtitle)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_menu_history, parent, false)
        return HistoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        val session = historyList[position]
        holder.title.text = session.title
        holder.subtitle.text = "${session.messages.size} mensajes"

        holder.itemView.setOnClickListener {
            onSessionClick(session)
        }
    }

    override fun getItemCount() = historyList.size
}