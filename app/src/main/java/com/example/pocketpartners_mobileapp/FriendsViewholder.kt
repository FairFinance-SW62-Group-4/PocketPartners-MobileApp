package com.example.pocketpartners_mobileapp

import Beans.FriendsOfUser
import Beans.UpdatedFriendsList
import Beans.UsersInformation
import Interface.PlaceHolder
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FriendsViewholder(view: View, val userId:Int, val service:PlaceHolder, val auth:String):RecyclerView.ViewHolder(view) {
    var friendName = view.findViewById<TextView>(R.id.txtFriendName)
    var friendAdress = view.findViewById<TextView>(R.id.txtFriendAdress)
    var friendImg = view.findViewById<ImageView>(R.id.imgFriend)
    val friendDltBtn = view.findViewById<Button>(R.id.btnDeleteFriend)
    val newFriends = mutableListOf<Int>()

    fun render(friendModel:UsersInformation){
        friendName.text = friendModel.fullName
        friendAdress.text = friendModel.email
        Picasso.get().load(friendModel.photo)
            .resize(200,200)
            .centerCrop().into(friendImg)

        friendDltBtn.setOnClickListener(){
            Log.d("Eliminar amigo", "ID mia: ${userId} ID amigo: ${friendModel.id}")

            service.getFriends(auth, userId).enqueue(object : Callback<FriendsOfUser> {
                override fun onResponse(call: Call<FriendsOfUser>, response: Response<FriendsOfUser>) {
                    val lf = response.body()
                    if(lf?.friendIds?.size != null){
                        for(f in lf.friendIds){
                            if(f != friendModel.id){
                                newFriends.add(f)
                            }
                        }
                    }

                    service.updateFriendList(auth, userId, UpdatedFriendsList(newFriends)).enqueue(object:Callback<FriendsOfUser>{
                        override fun onResponse(call: Call<FriendsOfUser>, response: Response<FriendsOfUser>) {
                            val r = response.body()
                            Log.d("Nueva lista", "${r?.friendIds?.size}")
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