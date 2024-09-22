package com.example.pocketpartners_mobileapp

import Beans.UsersInformation
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso

class FriendsViewholder(view: View):RecyclerView.ViewHolder(view) {
    var friendName = view.findViewById<TextView>(R.id.txtFriendName)
    var friendAdress = view.findViewById<TextView>(R.id.txtFriendAdress)
    var friendImg = view.findViewById<ImageView>(R.id.imgFriend)

    fun render(friendModel:UsersInformation){
        friendName.text = friendModel.fullName
        friendAdress.text = friendModel.email
        Picasso.get().load(friendModel.photo)
            .resize(100,100)
            .centerCrop().into(friendImg)
    }
}