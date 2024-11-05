package com.example.pocketpartners_mobileapp

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.widget.Toast
import Beans.SignInRequest
import Beans.AuthenticatedUserResource
import Interface.PlaceHolder
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AuthHelper(private val service: PlaceHolder, private val sharedPreferences: SharedPreferences) {

    fun loginUser(username: String, password: String, context: Context, onLoginSuccess: (userId: Long) -> Unit) {
        val signInRequest = SignInRequest(username, password)

        service.signIn(signInRequest).enqueue(object : Callback<AuthenticatedUserResource> {
            override fun onResponse(call: Call<AuthenticatedUserResource>, response: Response<AuthenticatedUserResource>) {
                if (response.isSuccessful) {
                    val authUser = response.body()

                    if (authUser != null) {
                        // Guardar el token y el userId en SharedPreferences
                        val editor = sharedPreferences.edit()
                        editor.putString("auth_token", authUser.token)
                        editor.putLong("user_id", authUser.id)
                        editor.apply()

                        // Ejecutar la función pasada para manejar el éxito del login
                        onLoginSuccess(authUser.id)

                        // Redirigir a MainActivity si se está en LoginActivity
                        if (context is LoginActivity) {
                            val intent = Intent(context, MainActivity::class.java)
                            context.startActivity(intent)
                            context.finish()
                        }
                    }
                } else {
                    Toast.makeText(context, "Credenciales incorrectas", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<AuthenticatedUserResource>, t: Throwable) {
                Toast.makeText(context, "Error al iniciar sesión", Toast.LENGTH_SHORT).show()
            }
        })
    }
}