package com.example.pocketpartners_mobileapp

import Beans.UsersInformation
import Interface.FriendsPlaceHolder
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
            val fragment = AddFriendsFragment()
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
        Log.d("FriendsFragment", "Auth Header: $authHeader")

        service.getAllUsersInformation(authHeader).enqueue(object : Callback<List<UsersInformation>>{
            override fun onResponse(call: Call<List<UsersInformation>>, response: Response<List<UsersInformation>>) {
                val fr = response.body()
                val listaF = mutableListOf<UsersInformation>()

                if(fr != null){
                    for (item in fr){
                        listaF.add(
                            UsersInformation(item.id, item.fullName, item.phoneNumber,
                            item.photo ,item.email, item.userId)
                        )
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