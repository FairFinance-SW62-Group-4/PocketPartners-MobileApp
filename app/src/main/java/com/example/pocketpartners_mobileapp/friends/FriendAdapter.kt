package com.example.pocketpartners_mobileapp.friends

import Beans.UsersInformation
import Interface.PlaceHolder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.pocketpartners_mobileapp.R

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

class MemberSelectionAdapter(
    private val membersList: List<UsersInformation>
) : RecyclerView.Adapter<MemberSelectionAdapter.MemberViewHolder>() {

    // Almacena el estado de selección de cada miembro
    private val selectedMembers = BooleanArray(membersList.size)

    inner class MemberViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvMemberName: TextView = itemView.findViewById(R.id.tvMemberName)
        val cbSelectMember: CheckBox = itemView.findViewById(R.id.cbSelectMember)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MemberViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.member_item, parent, false)
        return MemberViewHolder(view)
    }

    override fun onBindViewHolder(holder: MemberViewHolder, position: Int) {
        val member = membersList[position]
        holder.tvMemberName.text = member.fullName

        // Mantén el estado de selección del `CheckBox`
        holder.cbSelectMember.isChecked = selectedMembers[position]

        // Actualiza el estado de selección cuando el usuario interactúa con el `CheckBox`
        holder.cbSelectMember.setOnCheckedChangeListener { _, isChecked ->
            selectedMembers[position] = isChecked
        }
    }

    override fun getItemCount(): Int = membersList.size

    // Método para obtener los miembros seleccionados
    fun getSelectedMembers(): List<UsersInformation> {
        return membersList.filterIndexed { index, _ -> selectedMembers[index] }
    }
}