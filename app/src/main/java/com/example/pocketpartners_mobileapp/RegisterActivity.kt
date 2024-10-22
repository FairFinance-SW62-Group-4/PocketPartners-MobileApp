package com.example.pocketpartners_mobileapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import Beans.SignUpRequest
import Beans.User
import Beans.UserInformationRequest
import Beans.UsersInformation
import Interface.PlaceHolder
import android.content.SharedPreferences
import android.util.Log
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RegisterActivity : AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var service: PlaceHolder
    private lateinit var authHelper: AuthHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val btnRegister = findViewById<Button>(R.id.btnRegister)
        val edtUsername = findViewById<EditText>(R.id.edtUsername)
        val edtPassword = findViewById<EditText>(R.id.edtPassword)
        val edtFirstName = findViewById<EditText>(R.id.edtFirstName)
        val edtLastName = findViewById<EditText>(R.id.edtLastName)
        val edtPhoneNumber = findViewById<EditText>(R.id.edtPhoneNumber)
        val edtPhoto = findViewById<EditText>(R.id.edtPhoto)
        val edtEmail = findViewById<EditText>(R.id.edtEmail)

        sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE)

        // Inicializar Retrofit
        val retrofit = Retrofit.Builder()
            .baseUrl("https://pocket-partners-backend-production.up.railway.app/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        service = retrofit.create(PlaceHolder::class.java)
        authHelper = AuthHelper(service, sharedPreferences)

        // Manejar el registro
        btnRegister.setOnClickListener {
            val username = edtUsername.text.toString()
            val password = edtPassword.text.toString()
            val fName = edtFirstName.text.toString()
            val lName = edtLastName.text.toString()
            val pNumber = edtPhoneNumber.text.toString()
            val photo = edtPhoto.text.toString()
            val email = edtEmail.text.toString()

            if (username.isNotEmpty() && password.isNotEmpty()) {
                registerUser(username, password, fName, lName, pNumber, photo, email)
            } else {
                Toast.makeText(this, "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Método para registrar un nuevo usuario usando Retrofit
    private fun registerUser(username: String, password: String, fName: String, lName: String, pNumber: String, photo: String, email: String) {
        val signUpRequest = SignUpRequest(username, password, listOf("ROLE_USER"))

        service.signUp(signUpRequest).enqueue(object : Callback<User> {
            override fun onResponse(call: Call<User>, response: Response<User>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@RegisterActivity, "Registro exitoso", Toast.LENGTH_SHORT).show()

                    // Iniciar sesión automáticamente usando AuthHelper
                    authHelper.loginUser(username, password, this@RegisterActivity) { userId ->
                        // Crear UserInformation una vez que se haya iniciado sesión
                        val userInformationRequest = UserInformationRequest(fName, lName, pNumber, photo, email, userId.toInt())

                        // Obtener el token del SharedPreferences
                        val token = sharedPreferences.getString("auth_token", null)

                        if (token != null) {
                            createUserInformation(token, userInformationRequest)
                        } else {
                            Toast.makeText(this@RegisterActivity, "Token no disponible", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    val errorMessage = response.errorBody()?.string() ?: "Error desconocido"
                    Toast.makeText(this@RegisterActivity, "Error al registrar usuario: $errorMessage", Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<User>, t: Throwable) {
                Toast.makeText(this@RegisterActivity, "Error al registrar", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun createUserInformation(token: String, userInformationRequest: UserInformationRequest) {
        service.createUserInformation("Bearer $token", userInformationRequest).enqueue(object : Callback<UsersInformation> {
            override fun onResponse(call: Call<UsersInformation>, response: Response<UsersInformation>) {
                if (response.isSuccessful) {
                    runOnUiThread {
                        Toast.makeText(this@RegisterActivity, "Información del usuario creada con éxito", Toast.LENGTH_SHORT).show()

                        // Redirigir a LoginActivity solo después de crear UserInformation
                        val intent = Intent(this@RegisterActivity, MainActivity::class.java)
                        startActivity(intent)
                        finish() // Cierra la actividad después de crear UserInformation
                    }
                } else {
                    // Verificar el código de error y el cuerpo del error
                    val errorBody = response.errorBody()?.string()
                    val errorMessage = "Código de error: ${response.code()}, Cuerpo del error: $errorBody"
                    Log.e("RegisterActivity", "Error al crear UserInformation: $errorMessage")
                    runOnUiThread {
                        Toast.makeText(this@RegisterActivity, "Error al crear UserInformation: $errorMessage", Toast.LENGTH_LONG).show()
                    }
                }
            }

            override fun onFailure(call: Call<UsersInformation>, t: Throwable) {
                runOnUiThread {
                    Toast.makeText(this@RegisterActivity, "Error al crear información del usuario", Toast.LENGTH_SHORT).show()
                }
            }
        })
    }
}