package com.example.pocketpartners_mobileapp

import Beans.FriendsOfUser
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
import com.example.pocketpartners_mobileapp.FriendsFragment.Companion
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [AddFriendsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class AddFriendsFragment : Fragment() {

    lateinit var service: PlaceHolder
    private lateinit var sharedPreferences: SharedPreferences

    companion object {
        private const val USER_ID = "user_id"

        fun newInstance(userId: Int): AddFriendsFragment{
            val fragment = AddFriendsFragment()
            val args = Bundle()
            args.putInt(USER_ID, userId)
            fragment.arguments = args
            return fragment
        }
    }

    private var userId = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            userId = it.getInt(com.example.pocketpartners_mobileapp.AddFriendsFragment.USER_ID, 0)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_add_friends, container, false)
        sharedPreferences = requireActivity().getSharedPreferences("user_prefs", AppCompatActivity.MODE_PRIVATE)

        val btnAddFriend = view.findViewById<Button>(R.id.btnAddFriends)

        val retrofit = Retrofit.Builder()
            .baseUrl("https://pocket-partners-backend-production.up.railway.app/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        service = retrofit.create(PlaceHolder::class.java)

        btnAddFriend.setOnClickListener{
            val fragment = FriendsFragment.newInstance(userId)
            val transaction = parentFragmentManager.beginTransaction()
            transaction.replace(R.id.fragment_container, fragment)
            transaction.addToBackStack(null)
            transaction.commit()
        }

        getUsers(view)

        return view
    }

    private fun getUsers(view: View){
        val authHeader = "Bearer ${sharedPreferences.getString("auth_token", null)}"
        val friends = mutableListOf<Int>()
        Log.d("AddFriendsFragment", "Auth Header: $authHeader")

        //OBTIENE LA LISTA DE AMIGOS DEL USUARIO
        // (POR RAHORA SOLO UTILIZA AL USUARIO "a" DE CONTRASEÑA "a", CON ID "4")
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

        service.getAllUsersInformation(authHeader).enqueue(object : Callback<List<UsersInformation>>{
            override fun onResponse(call: Call<List<UsersInformation>>, response: Response<List<UsersInformation>>) {
                val u = response.body()
                val listaU = mutableListOf<UsersInformation>()

                if(u != null){
                    for (item in u){

                        if(item.id !in friends && item.id != userId){
                            listaU.add(
                                UsersInformation(item.id, item.fullName, item.phoneNumber,
                                    item.photo ,item.email, item.userId)
                            )
                        }
                    }

                    // Verificamos si el fragmento sigue adjunto a la actividad y si la vista está disponible
                    if (isAdded && view != null) {
                        // Verificamos si el contexto no es null antes de usarlo
                        val safeContext = context ?: return
                        val recycler = view?.findViewById<RecyclerView>(R.id.recyclerAddFriends)
                        recycler?.layoutManager = LinearLayoutManager(safeContext)
                        recycler?.adapter = AddFriendsAdapter(listaU)
                    } else {
                        // El fragmento ya no está adjunto, no intentar modificar la UI
                        Log.w("AddFriendsFragment", "Fragment no longer attached, skipping UI update.")
                    }
                }
            }

            override fun onFailure(call: Call<List<UsersInformation>>, t: Throwable) {
                t.printStackTrace()
            }
        })
    }
}