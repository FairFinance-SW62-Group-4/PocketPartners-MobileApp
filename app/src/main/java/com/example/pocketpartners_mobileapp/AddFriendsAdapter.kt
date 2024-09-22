package com.example.pocketpartners_mobileapp

import Beans.UsersInformation
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class AddFriendsAdapter(val usersNames: List<UsersInformation>): RecyclerView.Adapter<AddFriendsViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddFriendsViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return AddFriendsViewHolder(layoutInflater.inflate(R.layout.users_list, parent, false))
    }

    override fun getItemCount(): Int = usersNames.size

    override fun onBindViewHolder(holder: AddFriendsViewHolder, position: Int) {
        val item = usersNames[position]
        holder.render(item)
    }
}