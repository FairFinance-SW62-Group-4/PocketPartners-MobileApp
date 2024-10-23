package com.example.pocketpartners_mobileapp

import Beans.FriendsList
import Beans.FriendsOfUser
import Beans.UsersInformation
import Interface.PlaceHolder
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create

class FriendsFragment : Fragment() {

    lateinit var service: PlaceHolder
    private lateinit var sharedPreferences: SharedPreferences

    companion object {
        private const val USER_ID = "user_id"

        fun newInstance(userId: Int): FriendsFragment {
            val fragment = FriendsFragment()
            val args = Bundle()
            args.putInt(USER_ID, userId)
            fragment.arguments = args
            return fragment
        }
    }

    private var userId: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        super.onCreate(savedInstanceState)
        arguments?.let {
            userId = it.getInt(USER_ID, 0)
        }

        val view = inflater.inflate(R.layout.fragment_friends, container, false)
        sharedPreferences = requireActivity().getSharedPreferences("user_prefs", AppCompatActivity.MODE_PRIVATE)

        val btnAddFriend = view.findViewById<Button>(R.id.btnAddFriends)

        val retrofit =Retrofit.Builder()
            .baseUrl("https://pocket-partners-backend-production.up.railway.app/") //Añadir URL luego cuando se prenda
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        service = retrofit.create(PlaceHolder::class.java)

        btnAddFriend.setOnClickListener{
            val fragment = AddFriendsFragment.newInstance(userId)
            val transaction = parentFragmentManager.beginTransaction()
            transaction.replace(R.id.fragment_container, fragment)
            transaction.addToBackStack(null)
            transaction.commit()
        }

        getFriends(view)

        return view
    }

    private fun getFriends(view: View){
        val authHeader = "Bearer ${sharedPreferences.getString("auth_token", null)}"
        val friends = mutableListOf<Int>()
        Log.d("FriendsFragment", "Auth Header: $authHeader")
        //OBTIENE LA LISTA DE AMIGOS DEL USUARIO
        service.getFriends(authHeader, userId).enqueue(object : Callback<FriendsOfUser>{
            override fun onResponse(call: Call<FriendsOfUser>, response: Response<FriendsOfUser>) {
                val lf = response.body()
                //Log.d("amigos", lf?.friendIds?.size.toString())
                if(lf?.friendIds?.size != null){
                    for(f in lf.friendIds){
                        friends.add(f)
                    }
                }
            }

            override fun onFailure(p0: Call<FriendsOfUser>, p1: Throwable) {
                p1.printStackTrace()
            }
        })

        //OBTIENE A TODOS LOS USUARIOS Y COMPARA CON LA LISTA DE AMIGOS
        service.getAllUsersInformation(authHeader).enqueue(object : Callback<List<UsersInformation>>{
            override fun onResponse(call: Call<List<UsersInformation>>, response: Response<List<UsersInformation>>) {
                val fr = response.body()
                val listaF = mutableListOf<UsersInformation>()

                //Log.d("amigosDefinitivo", friends.size.toString())
                if(fr != null){
                    for (item in fr){

                        if(item.id in friends){
                            //Log.d("resultado",item.fullName.toString())
                            listaF.add(
                                UsersInformation(item.id, item.fullName, item.phoneNumber,
                                    item.photo ,item.email, item.userId)
                            )
                        }
                    }

                    val recycler = view.findViewById<RecyclerView>(R.id.recyclerFriends)
                    recycler.layoutManager = LinearLayoutManager(requireContext())
                    recycler.adapter = FriendAdapter(listaF)
                }
            }

            override fun onFailure(p0: Call<List<UsersInformation>>, p1: Throwable) {
                p1.printStackTrace()
            }
        })
    }
}