package com.example.pocketpartners_mobileapp

import Beans.Friend
import Interface.FriendsPlaceHolder
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create

class FriendsFragment : Fragment() {

    lateinit var service: FriendsPlaceHolder

    companion object {
        fun newInstance() = FriendsFragment()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // TODO: Use the ViewModel
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_friends, container, false)
        val btnAddFriend = view.findViewById<Button>(R.id.btnAddFriends)

        val retrofit =Retrofit.Builder()
            .baseUrl("https://pocket-partners-backend-production.up.railway.app/api/v1/") //Añadir URL luego cuando se prenda
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        service = retrofit.create(FriendsPlaceHolder::class.java)

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
        service.getListadoFriends().enqueue(object : Callback<List<Friend>>{
            override fun onResponse(call: Call<List<Friend>>, response: Response<List<Friend>>) {
                val fr = response.body()
                val listaF = mutableListOf<Friend>()

                if(fr != null){
                    for (item in fr){
                        listaF.add(
                            Friend(item.id, item.fullName, item.phoneNumber,
                            item.photo ,item.email, item.userId)
                        )
                    }

                    val recycler = view.findViewById<RecyclerView>(R.id.recyclerFriends)
                    recycler.layoutManager = LinearLayoutManager(requireContext())
                    recycler.adapter = FriendAdapter(listaF)
                }
            }

            override fun onFailure(p0: Call<List<Friend>>, p1: Throwable) {
                p1.printStackTrace()
            }
        })
    }
}