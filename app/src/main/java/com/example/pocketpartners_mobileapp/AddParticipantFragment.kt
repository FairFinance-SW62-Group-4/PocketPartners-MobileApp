package com.example.pocketpartners_mobileapp

import Beans.FriendsList
import Beans.GroupJoin
import Beans.GroupRequest
import Beans.GroupResponse
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import Beans.UsersInformation
import GroupsFragment
import Interface.PlaceHolder
import android.content.SharedPreferences
import android.os.Build
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class AddParticipantFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var amigoAdapter: FriendRecommendationAdapter
    private val amigosConInfo = mutableListOf<UsersInformation>()
    private lateinit var sharedPreferences: SharedPreferences
    private var userInformationIdValue: Int = -1
    private var groupId: Int = -1
    private var userId: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_add_participant, container, false)

        val groupName = arguments?.getString("groupName") ?: "Default Name"
        val groupPhoto = arguments?.getString("groupPhoto") ?: "Default Photo URL"
        val currency = arguments?.getStringArray("currency")?.toList() ?: listOf("PEN")

        // Inicializar el RecyclerView
        sharedPreferences = requireActivity().getSharedPreferences("user_prefs", AppCompatActivity.MODE_PRIVATE)
        recyclerView = view.findViewById(R.id.rvRecomendados)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        val btnBack: ImageView = view.findViewById(R.id.backButton)
        val btnCreate: Button = view.findViewById(R.id.btnCreate)

        btnBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
        btnCreate.setOnClickListener {
            crearGrupo(groupName, groupPhoto, currency)
        }

        userId=sharedPreferences.getLong("user_id", 0L).toInt()

        // Obtener la lista de amigos con información
        obtenerAmigosConInformacionCompleta(userId)

        return view
    }

    private fun crearGrupo(groupName: String, groupPhoto: String, currency: List<String>) {
        val authHeader = "Bearer ${sharedPreferences.getString("auth_token", null)}"
        val retrofit = Retrofit.Builder()
            .baseUrl("https://pocket-partners-backend-production.up.railway.app/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val placeHolder = retrofit.create(PlaceHolder::class.java)

        val groupRequest = GroupRequest(
            name = groupName,
            groupPhoto = groupPhoto,
            currency = currency
        )

        // Hacer la llamada para crear el grupo
        placeHolder.createGroup(authHeader, groupRequest).enqueue(object : Callback<GroupResponse> {
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onResponse(call: Call<GroupResponse>, response: Response<GroupResponse>) {
                if (response.isSuccessful) {
                    groupId = response.body()?.id ?: -1
                    Log.d("UserInformationId", "User ID: $userInformationIdValue")
                    Log.d("GroupId", "User ID: $groupId")
                    agregarUsuarioAlGrupo() // Agregar el usuario al grupo
                    agregarMiembrosAlGrupo() // Luego agregar otros miembros
                } else {
                    // Manejar error al crear el grupo
                    Toast.makeText(requireContext(), "Error al crear el grupo: ${response.message()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<GroupResponse>, t: Throwable) {
                // Manejar error
                Toast.makeText(requireContext(), "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun agregarUsuarioAlGrupo() {
        val authHeader = "Bearer ${sharedPreferences.getString("auth_token", null)}"
        val retrofit = Retrofit.Builder()
            .baseUrl("https://pocket-partners-backend-production.up.railway.app/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val placeHolder = retrofit.create(PlaceHolder::class.java)

        // Obtener el userId del SharedPreferences
        val userId = sharedPreferences.getLong("user_id", 0L).toInt()

        // Obtener el ID del usuario con éxito
        obtenerUserInformationId(userId, object : UserInformationCallback {
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onSuccess(userId: Int) {
                userInformationIdValue = userId // Aquí se actualiza el valor

                // Asegúrate de que el valor se actualiza correctamente
                Log.d("UserInformationId", "ID del usuario después de la actualización: $userInformationIdValue")

                // Hacer la llamada para agregar al usuario al grupo
                placeHolder.addMemberToGroup(authHeader, groupId, userInformationIdValue)
                    .enqueue(object : Callback<GroupJoin> {
                        override fun onResponse(call: Call<GroupJoin>, response: Response<GroupJoin>) {
                            if (response.isSuccessful) {
                                Toast.makeText(requireContext(), "Usuario agregado al grupo.", Toast.LENGTH_SHORT).show()
                            } else {
                                // Manejar errores de la respuesta
                                when (response.code()) {
                                    400 -> Toast.makeText(requireContext(), "Solicitud inválida.", Toast.LENGTH_SHORT).show()
                                    404 -> Toast.makeText(requireContext(), "Grupo o usuario no encontrado.", Toast.LENGTH_SHORT).show()
                                    500 -> Toast.makeText(requireContext(), "Error interno del servidor.", Toast.LENGTH_SHORT).show()
                                    else -> Toast.makeText(requireContext(), "Error desconocido: ${response.code()}", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }

                        override fun onFailure(call: Call<GroupJoin>, t: Throwable) {
                            Toast.makeText(requireContext(), "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                        }
                    })
            }

            override fun onError(errorMessage: String) {
                Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show()
            }
        })
    }

    interface UserInformationCallback {
        fun onSuccess(userId: Int)
        fun onError(errorMessage: String)
    }

    private fun obtenerUserInformationId(userId: Int, callback: UserInformationCallback) {
        val authHeader = "Bearer ${sharedPreferences.getString("auth_token", null)}"
        Log.d("UserInformationId", "Auth Header: $authHeader")
        val retrofit = Retrofit.Builder()
            .baseUrl("https://pocket-partners-backend-production.up.railway.app/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val placeHolder = retrofit.create(PlaceHolder::class.java)

        Log.d("UserInformationId", "User ID enviado: $userId")
        placeHolder.getUserInformation(authHeader, userId).enqueue(object : Callback<UsersInformation> {
            override fun onResponse(call: Call<UsersInformation>, response: Response<UsersInformation>) {
                if (response.isSuccessful && response.body() != null) {
                    userInformationIdValue = response.body()!!.id
                    Log.d("UserInformationId", "ID obtenido: $userInformationIdValue")
                    callback.onSuccess(userInformationIdValue)
                } else {
                    // Manejo de errores más detallado
                    val errorBody = response.errorBody()?.string() ?: "Cuerpo de error vacío"
                    Log.e("UserInformationId", "Error al obtener el ID: ${response.code()} - ${response.message()} - $errorBody")
                    callback.onError("Error al obtener el ID: ${response.code()} - ${response.message()} - $errorBody")
                }
            }

            override fun onFailure(call: Call<UsersInformation>, t: Throwable) {
                Log.e("UserInformationId", "Error de red: ${t.message}")
                callback.onError("Error de red: ${t.message}")
            }
        })
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun agregarMiembrosAlGrupo() {
        val authHeader = "Bearer ${sharedPreferences.getString("auth_token", null)}"
        val retrofit = Retrofit.Builder()
            .baseUrl("https://pocket-partners-backend-production.up.railway.app/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val placeHolder = retrofit.create(PlaceHolder::class.java)

        for (amigo in amigosConInfo) {
            placeHolder.addMemberToGroup(authHeader, groupId, amigo.userId).enqueue(object : Callback<GroupJoin> {
                override fun onResponse(call: Call<GroupJoin>, response: Response<GroupJoin>) {
                    if (response.isSuccessful) {
                        Toast.makeText(requireContext(), "Miembro agregado al grupo.", Toast.LENGTH_SHORT).show()
                        regresarAGroupsFragment()
                    } else {
                        // Manejar errores de la respuesta
                        when (response.code()) {
                            400 -> Toast.makeText(requireContext(), "Solicitud inválida.", Toast.LENGTH_SHORT).show()
                            404 -> Toast.makeText(requireContext(), "Grupo o usuario no encontrado.", Toast.LENGTH_SHORT).show()
                            500 -> Toast.makeText(requireContext(), "Error interno del servidor.", Toast.LENGTH_SHORT).show()
                            else -> Toast.makeText(requireContext(), "Error desconocido: ${response.code()}", Toast.LENGTH_SHORT).show()
                        }
                    }
                }

                override fun onFailure(call: Call<GroupJoin>, t: Throwable) {
                    // Manejar error
                    Toast.makeText(requireContext(), "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }

    private fun obtenerAmigosConInformacionCompleta(userId: Int) {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://pocket-partners-backend-production.up.railway.app/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val placeHolder = retrofit.create(PlaceHolder::class.java)

        val authHeader = sharedPreferences.getString("auth_token", null) ?: return

        // 1. Obtener la lista de amigos de un usuario
        placeHolder.getFriendsList(userId).enqueue(object : Callback<FriendsList> {
            override fun onResponse(call: Call<FriendsList>, response: Response<FriendsList>) {
                if (response.isSuccessful) {
                    val friendsList = response.body()?.friendsIds ?: emptyList()
                    // Realizar las llamadas individuales para obtener la información de cada amigo
                    for (friendId in friendsList) {
                        placeHolder.getUserInformation(authHeader, friendId).enqueue(object : Callback<UsersInformation> {
                            override fun onResponse(call: Call<UsersInformation>, response: Response<UsersInformation>) {
                                if (response.isSuccessful) {
                                    response.body()?.let { amigo ->
                                        amigosConInfo.add(amigo)

                                        // Solo actualiza el RecyclerView cuando todos los amigos han sido cargados
                                        if (amigosConInfo.size == friendsList.size) {
                                            // Mostrar solo 4 amigos aleatorios
                                            amigosConInfo.shuffle()
                                            val amigosSeleccionados = amigosConInfo.take(4)

                                            // Configurar el adaptador
                                            amigoAdapter = FriendRecommendationAdapter(amigosSeleccionados) { amigo ->
                                                agregarAmigo(amigo)
                                            }
                                            recyclerView.adapter = amigoAdapter
                                        }
                                    }
                                }
                            }

                            override fun onFailure(call: Call<UsersInformation>, t: Throwable) {
                                // Manejar error al obtener información del amigo
                            }
                        })
                    }
                }
            }

            override fun onFailure(call: Call<FriendsList>, t: Throwable) {
                // Manejar error al obtener la lista de amigos
            }
        })
    }

    private fun agregarAmigo(amigo: UsersInformation) {
        // Lógica para agregar el amigo (puedes implementar lo que necesites aquí)
    }

    private fun regresarAGroupsFragment() {
        parentFragmentManager.popBackStack() // Regresar al fragmento anterior
    }
}