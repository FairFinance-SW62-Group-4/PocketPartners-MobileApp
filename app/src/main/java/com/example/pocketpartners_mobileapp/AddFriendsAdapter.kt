package com.example.pocketpartners_mobileapp

import Beans.UsersInformation
import Interface.PlaceHolder
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class AddFriendsAdapter(val usersNames: List<UsersInformation>, serviceP:PlaceHolder, userId: Int, auth:String): RecyclerView.Adapter<AddFriendsViewHolder>() {
    val service = serviceP
    val u = userId
    val authentication = auth

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddFriendsViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return AddFriendsViewHolder(layoutInflater.inflate(R.layout.users_list, parent, false), service, u, authentication)
    }

    override fun getItemCount(): Int = usersNames.size

    override fun onBindViewHolder(holder: AddFriendsViewHolder, position: Int) {
        val item = usersNames[position]
        holder.render(item)
    }
}