package com.example.pocketpartners_mobileapp

import Beans.UsersInformation
import Interface.PlaceHolder
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class FriendAdapter(val friendsList:List<UsersInformation>, val userId:Int, val service:PlaceHolder, val auth: String)
    :RecyclerView.Adapter<FriendsViewholder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FriendsViewholder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return FriendsViewholder(layoutInflater.inflate(R.layout.friends_card, parent, false), userId, service, auth)
    }

    override fun getItemCount(): Int = friendsList.size

    override fun onBindViewHolder(holder: FriendsViewholder, position: Int) {
        val item = friendsList[position]
        holder.render(item)
    }

}