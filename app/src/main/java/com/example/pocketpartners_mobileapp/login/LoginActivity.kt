package com.example.pocketpartners_mobileapp.login

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import Interface.PlaceHolder
import android.widget.ImageView
import com.example.pocketpartners_mobileapp.MainMenu
import com.example.pocketpartners_mobileapp.R
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class LoginActivity : AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var service: PlaceHolder
    private lateinit var authHelper: AuthHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE)

        val btnLogin = findViewById<Button>(R.id.btnLogin)
        val btnRegister = findViewById<Button>(R.id.btnRegister)
        val edtUsername = findViewById<EditText>(R.id.edtUsername)
        val edtPassword = findViewById<EditText>(R.id.edtPassword)
        val backButton = findViewById<ImageView>(R.id.backButton)

        // Inicializar Retrofit
        val retrofit = Retrofit.Builder()
            .baseUrl("https://pocket-partners-backend-production.up.railway.app/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        service = retrofit.create(PlaceHolder::class.java)
        authHelper = AuthHelper(service, sharedPreferences)

        backButton.setOnClickListener {
            // Crear un Intent para abrir MainMenu
            val intent = Intent(this, MainMenu::class.java)
            startActivity(intent)
            finish() // Opcional: finaliza LoginActivity para evitar regresar con el botón "Atrás"
        }

        // Manejar inicio de sesión
        btnLogin.setOnClickListener {
            val username = edtUsername.text.toString()
            val password = edtPassword.text.toString()

            if (username.isNotEmpty() && password.isNotEmpty()) {
                authHelper.loginUser(username, password, this) {}
            } else {
                Toast.makeText(this, "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show()
            }
        }

        // Ir a la actividad de registro
        btnRegister.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

}