package com.example.pocketpartners_mobileapp

import Beans.Grupo
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class GroupAdapter(private val grupos: List<Grupo>) : RecyclerView.Adapter<GroupAdapter.GrupoViewHolder>() {

    // Definimos los colores de fondo que se van a intercalar
    private val colores = listOf(R.color.colorAmarillo, R.color.colorBlanco)  // Define tus colores aquí

    class GrupoViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvGroupName: TextView = view.findViewById(R.id.tvGroupName)
        val ivGroupImage: ImageView = view.findViewById(R.id.ivGroupImage)
        val cardView: CardView = view.findViewById(R.id.cardView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GrupoViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.group_list, parent, false)
        return GrupoViewHolder(view)
    }

    override fun onBindViewHolder(holder: GrupoViewHolder, position: Int) {
        val grupo = grupos[position]

        holder.tvGroupName.text = grupo.name

        Glide.with(holder.itemView.context)
            .load(grupo.groupPhoto)
            .placeholder(R.drawable.ic_launcher_foreground)
            .into(holder.ivGroupImage)

        val colorIndex = position % colores.size
        holder.cardView.setCardBackgroundColor(ContextCompat.getColor(holder.itemView.context, colores[colorIndex]))
    }

    override fun getItemCount(): Int {
        return grupos.size
    }
}