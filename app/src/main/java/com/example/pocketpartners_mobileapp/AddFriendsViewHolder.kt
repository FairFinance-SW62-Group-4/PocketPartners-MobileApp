package com.example.pocketpartners_mobileapp

import Beans.AddFriend
import Beans.FriendsOfUser
import Beans.UsersInformation
import Interface.PlaceHolder
import android.util.Log
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AddFriendsViewHolder(view:View, serviceP:PlaceHolder, userid: Int, auth:String): RecyclerView.ViewHolder(view) {
    var userName = view.findViewById<TextView>(R.id.txtUserNameAddFriend)
    var addFriendButton = view.findViewById<ImageButton>(R.id.btnUserAddFriend)
    val service = serviceP
    val userId = userid
    val authentication = auth

    fun render(userModel:UsersInformation){
        userName.text = userModel.fullName
        var listId = 0

        addFriendButton.setOnClickListener(){

            service.getFriends(authentication, userId).enqueue(object : Callback<FriendsOfUser> {
                override fun onResponse(call: Call<FriendsOfUser>, response: Response<FriendsOfUser>) {
                    val lf = response.body()
                    if(lf != null) {listId = lf.id}

                    service.addFriends(authentication, AddFriend(listId, userModel.id))
                        .enqueue(object : Callback<FriendsOfUser>{
                            override fun onResponse(fou: Call<FriendsOfUser>, response: Response<FriendsOfUser>) {
                                Log.d("resultados2", "Lista: ${listId}, amigo: ${userModel.id}")
                            }

                            override fun onFailure(p0: Call<FriendsOfUser>, p1: Throwable) {
                                p1.printStackTrace()
                            }
                        })
                }

                override fun onFailure(p0: Call<FriendsOfUser>, p1: Throwable) {
                    p1.printStackTrace()
                }
            })
        }
    }
}