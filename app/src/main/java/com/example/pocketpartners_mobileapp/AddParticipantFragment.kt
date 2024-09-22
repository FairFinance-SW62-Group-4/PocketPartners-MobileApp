package com.example.pocketpartners_mobileapp

import Beans.FriendsList
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
import android.os.Build
import android.widget.Button
import android.widget.Toast
import androidx.annotation.RequiresApi
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
    private var groupId: Int = -1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_add_participant, container, false)

        val groupName = arguments?.getString("groupName") ?: "Default Name"
        val groupPhoto = arguments?.getString("groupPhoto") ?: "Default Photo URL"
        val currency = arguments?.getStringArray("currency")?.toList() ?: listOf("PEN")

        // Inicializar el RecyclerView
        recyclerView = view.findViewById(R.id.rvRecomendados)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        val btnBack: ImageView = view.findViewById(R.id.backButton)
        val btnCreate: Button = view.findViewById(R.id.btnCreate)

        btnBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
        btnCreate.setOnClickListener {
            val transaction = parentFragmentManager.beginTransaction()
            transaction.replace(R.id.fragment_container, GroupsFragment())
            transaction.commit()
            parentFragmentManager.popBackStack(null, androidx.fragment.app.FragmentManager.POP_BACK_STACK_INCLUSIVE)
            crearGrupo(groupName, groupPhoto, currency)
        }

        // Obtener el userId del usuario actual (ajusta según tu lógica)
        val userId = 1  // Cambia esto por el ID del usuario actual

        // Obtener la lista de amigos con información
        obtenerAmigosConInformacionCompleta(userId)

        return view
    }

    private fun crearGrupo(groupName: String, groupPhoto: String, currency: List<String>) {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://pocket-partners-backend-production.up.railway.app/api/v1/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val placeHolder = retrofit.create(PlaceHolder::class.java)

        val groupRequest = GroupRequest(
            name = groupName,
            groupPhoto = groupPhoto,
            currency = currency
        )

        // Hacer la llamada para crear el grupo
        placeHolder.createGroup(groupRequest).enqueue(object : Callback<GroupResponse> {
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onResponse(call: Call<GroupResponse>, response: Response<GroupResponse>) {
                if (response.isSuccessful) {
                    groupId = response.body()?.id ?: -1
                    agregarMiembrosAlGrupo()
                } else {
                    // Manejar error al crear el grupo
                }
            }

            override fun onFailure(call: Call<GroupResponse>, t: Throwable) {
                // Manejar error
            }
        })
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun agregarMiembrosAlGrupo() {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://pocket-partners-backend-production.up.railway.app/api/v1/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val placeHolder = retrofit.create(PlaceHolder::class.java)
        val currentTime = LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME)

        for (amigo in amigosConInfo) {
            val memberJson = mapOf(
                "groupId" to groupId,
                "userId" to amigo.userId,
                "joinedAt" to currentTime
            )

            placeHolder.addMemberToGroup(groupId, amigo.userId, memberJson).enqueue(object : Callback<Void> {
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    if (response.isSuccessful) {
                        // Miembro agregado con éxito
                        Toast.makeText(requireContext(), "Miembro agregado al grupo.", Toast.LENGTH_SHORT).show()
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

                override fun onFailure(call: Call<Void>, t: Throwable) {
                    // Manejar error
                }
            })
        }
    }

    private fun obtenerAmigosConInformacionCompleta(userId: Int) {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://pocket-partners-backend-production.up.railway.app/api/v1/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val placeHolder = retrofit.create(PlaceHolder::class.java)

        // 1. Obtener la lista de amigos de un usuario
        placeHolder.getFriendsList(userId).enqueue(object : Callback<FriendsList> {
            override fun onResponse(call: Call<FriendsList>, response: Response<FriendsList>) {
                if (response.isSuccessful) {
                    val friendsList = response.body()?.friendsIds ?: emptyList()
                    // Realizar las llamadas individuales para obtener la información de cada amigo
                    for (friendId in friendsList) {
                        placeHolder.getUserInformation(friendId).enqueue(object : Callback<UsersInformation> {
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
}