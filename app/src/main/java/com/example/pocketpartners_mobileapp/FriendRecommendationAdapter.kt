package com.example.pocketpartners_mobileapp

import Beans.UsersInformation
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class FriendRecommendationAdapter(private val amigos: List<UsersInformation>, private val onAgregarClick: (UsersInformation) -> Unit) :
    RecyclerView.Adapter<FriendRecommendationAdapter.AmigoViewHolder>() {

    class AmigoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvNombre: TextView = itemView.findViewById(R.id.tvNombreAmigo)
        val btnAgregar: ImageButton = itemView.findViewById(R.id.btnAgregarAmigo)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AmigoViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.friend_recommendation, parent, false)
        return AmigoViewHolder(view)
    }

    override fun onBindViewHolder(holder: AmigoViewHolder, position: Int) {
        val amigo = amigos[position]
        holder.tvNombre.text = amigo.fullName
        holder.btnAgregar.setOnClickListener {
            onAgregarClick(amigo)
        }
    }

    override fun getItemCount(): Int = amigos.size
}