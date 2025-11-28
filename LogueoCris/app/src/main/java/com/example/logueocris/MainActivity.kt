package com.example.logueocris

import android.content.Intent
import android.os.Bundle
import com.google.gson.GsonBuilder
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {

    // 1. Configuración de Retrofit
    private val apiService: ApiService by lazy {
        val gson = GsonBuilder()
            .setLenient()
            .create()

        Retrofit.Builder()
            .baseUrl("http://10.114.10.44/s") // Tu IP actual
            .addConverterFactory(GsonConverterFactory.create(gson)) // <--- AQUÍ USAMOS EL GSON MODIFICADO
            .build()
            .create(ApiService::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Referencias a las vistas
        // NOTA: Si en tu layout de login NO tienes campo de usuario, comenta o borra la línea de 'edTUsuario'
        val edTUsuario: EditText? = findViewById(R.id.edTUsuario)
        val edTContra: EditText = findViewById(R.id.edTContra)
        val edTcorreo: EditText = findViewById(R.id.edtCorreo)
        val btnAcceso: Button = findViewById(R.id.btnAcceso)
        val btnRegistro: Button = findViewById(R.id.btnreg)

        // Lógica del botón ACCESO (Login)
        btnAcceso.setOnClickListener {
            val correo = edTcorreo.text.toString().trim()
            val contrasena = edTContra.text.toString().trim()

            // Validamos solo correo y contraseña para entrar
            if (correo.isEmpty() || contrasena.isEmpty()) {
                Toast.makeText(this, "Por favor, completa correo y contraseña", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Creamos el objeto con los datos
            val loginRequest = LoginRequest(correo, contrasena)

            // Llamada a la API (Solo Iniciar Sesión)
            apiService.iniciarSesion(loginRequest).enqueue(object : Callback<AuthResponse> {
                override fun onResponse(call: Call<AuthResponse>, response: Response<AuthResponse>) {
                    if (response.isSuccessful) {
                        val respuesta = response.body()

                        if (respuesta?.status == "success") {
                            // --- LOGIN EXITOSO ---
                            Toast.makeText(this@MainActivity, "¡Hola ${respuesta.message}!", Toast.LENGTH_SHORT).show()

                            // Navegar al Chat de la IA
                            val intent = Intent(this@MainActivity, ChatPrincipal::class.java)
                            startActivity(intent)
                            finish() // Cierra el login

                        } else {
                            // Error de credenciales
                            Toast.makeText(this@MainActivity, "Error: ${respuesta?.message}", Toast.LENGTH_LONG).show()
                        }
                    } else {
                        Toast.makeText(this@MainActivity, "Error del servidor: ${response.code()}", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<AuthResponse>, t: Throwable) {
                    // Este es el bloque que te faltaba
                    Toast.makeText(this@MainActivity, "Fallo de conexión: ${t.message}", Toast.LENGTH_LONG).show()
                    Log.e("LOGIN", "Error: ${t.message}")
                }
            })
        }


        // Botón para ir a la pantalla de Registro
        btnRegistro.setOnClickListener {
            // Asegúrate de que 'ZpantallaRegistro' es el nombre correcto de tu actividad
            val intent = Intent(this, ZpantallaRegistro::class.java)
            startActivity(intent)
        }
    }
}
