package com.example.pocketpartners_mobileapp

import Beans.UsersInformation
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class AddFriendsViewHolder(view:View): RecyclerView.ViewHolder(view) {
    var userName = view.findViewById<TextView>(R.id.txtUserNameAddFriend)

    fun render(userModel:UsersInformation){
        userName.text = userModel.fullName
    }
}