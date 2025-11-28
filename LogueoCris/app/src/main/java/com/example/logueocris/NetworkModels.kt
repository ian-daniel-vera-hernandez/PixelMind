package com.example.logueocris

import com.google.gson.annotations.SerializedName

// Clase para enviar los datos de registro al servidor
data class RegistroRequest(
    @SerializedName("usuario") val usuario: String,
    @SerializedName("correo") val correo: String,
    @SerializedName("password") val password: String
)

// Clase para recibir la respuesta del servidor
data class AuthResponse(
    @SerializedName("status") val status: String,
    @SerializedName("message") val message: String,
    @SerializedName("usuario_id") val usuarioId: Int? = null
)

data class PerfilRequest(
    @SerializedName("usuario_id") val usuarioId: Int,
    @SerializedName("nombre_completo") val nombreCompleto: String,
    @SerializedName("correo") val correo: String
)
