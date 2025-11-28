package com.example.logueocris

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {
    @POST("api_logueo/registro.php")
    fun registrarUsuario(@Body request: RegistroRequest): Call<AuthResponse>

    @POST("api_logueo/perfil.php")
    fun guardarPerfil(@Body request: PerfilRequest): Call<AuthResponse>

    @POST("api_logueo/login.php")
    fun iniciarSesion(@Body request: LoginRequest): Call<AuthResponse>
}