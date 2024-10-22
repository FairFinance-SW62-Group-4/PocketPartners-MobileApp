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

    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
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
            val fragment = FriendsFragment()
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
        Log.d("AddFriendsFragment", "Auth Header: $authHeader")

        service.getAllUsersInformation(authHeader).enqueue(object : Callback<List<UsersInformation>>{
            override fun onResponse(call: Call<List<UsersInformation>>, response: Response<List<UsersInformation>>) {
                val u = response.body()
                val listaU = mutableListOf<UsersInformation>()

                if(u != null){
                    for (item in u){
                        listaU.add(
                            UsersInformation(item.id, item.fullName, item.phoneNumber,
                                item.photo ,item.email, item.userId)
                        )
                    }

                    val recycler = view.findViewById<RecyclerView>(R.id.recyclerAddFriends)
                    recycler.layoutManager = LinearLayoutManager(requireContext())
                    recycler.adapter = AddFriendsAdapter(listaU)
                }
            }

            override fun onFailure(p0: Call<List<UsersInformation>>, p1: Throwable) {
                p1.printStackTrace()
            }
        })
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment AddFriendsFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            AddFriendsFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}