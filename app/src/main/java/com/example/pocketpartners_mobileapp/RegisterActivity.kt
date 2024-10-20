package com.example.pocketpartners_mobileapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import Beans.SignUpRequest
import Beans.User
import Interface.PlaceHolder
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RegisterActivity : AppCompatActivity() {

    private lateinit var service: PlaceHolder

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val btnRegister = findViewById<Button>(R.id.btnRegister)
        val edtUsername = findViewById<EditText>(R.id.edtUsername)
        val edtPassword = findViewById<EditText>(R.id.edtPassword)

        // Inicializar Retrofit
        val retrofit = Retrofit.Builder()
            .baseUrl("http://localhost:8080/api/v1/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        service = retrofit.create(PlaceHolder::class.java)

        // Manejar el registro
        btnRegister.setOnClickListener {
            val username = edtUsername.text.toString()
            val password = edtPassword.text.toString()

            if (username.isNotEmpty() && password.isNotEmpty()) {
                registerUser(username, password)
            } else {
                Toast.makeText(this, "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Método para registrar un nuevo usuario usando Retrofit
    private fun registerUser(username: String, password: String) {
        val signUpRequest = SignUpRequest(username, password, listOf("ROLE_USER"))

        service.signUp(signUpRequest).enqueue(object : Callback<User> {
            override fun onResponse(call: Call<User>, response: Response<User>) {
                if (response.isSuccessful) {
                    runOnUiThread {
                        Toast.makeText(this@RegisterActivity, "Registro exitoso", Toast.LENGTH_SHORT).show()

                        // Redirigir a LoginActivity
                        val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                } else {
                    val errorMessage = response.errorBody()?.string() ?: "Error desconocido"
                    runOnUiThread {
                        Toast.makeText(this@RegisterActivity, "Error al registrar usuario: $errorMessage", Toast.LENGTH_LONG).show()
                    }
                }
            }

            override fun onFailure(call: Call<User>, t: Throwable) {
                runOnUiThread {
                    Toast.makeText(this@RegisterActivity, "Error al registrar", Toast.LENGTH_SHORT).show()

                }
            }
        })
    }
}