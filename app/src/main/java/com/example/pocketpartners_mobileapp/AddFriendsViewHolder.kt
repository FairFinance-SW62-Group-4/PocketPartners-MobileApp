package com.example.pocketpartners_mobileapp

import Beans.UsersInformation
import android.util.Log
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class AddFriendsViewHolder(view:View): RecyclerView.ViewHolder(view) {
    var userName = view.findViewById<TextView>(R.id.txtUserNameAddFriend)
    var addFriendButton = view.findViewById<ImageButton>(R.id.btnUserAddFriend)

    fun render(userModel:UsersInformation){
        userName.text = userModel.fullName

        addFriendButton.setOnClickListener(){
            Log.d("Boton de ${userModel.fullName}", "id: ${userModel.userId}")
        }
    }
}