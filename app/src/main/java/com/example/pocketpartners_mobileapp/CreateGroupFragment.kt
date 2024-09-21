package com.example.pocketpartners_mobileapp

import GroupsFragment
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView

class CreateGroupFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view=inflater.inflate(R.layout.fragment_create_group, container, false)

        val btnBack:ImageView=view.findViewById(R.id.backButton)
        val txtNom:EditText=view.findViewById(R.id.txtNombreGrupo)
        val txtMon:EditText=view.findViewById(R.id.txtMoneda)
        val btnNext:Button=view.findViewById(R.id.btnNext)

        btnBack.setOnClickListener(){
            val fragment = GroupsFragment()
            val transaction = parentFragmentManager.beginTransaction()
            transaction.replace(R.id.fragment_container, fragment)
            transaction.addToBackStack(null)
            transaction.commit()
        }

        btnNext.setOnClickListener(){
            val fragment = AddParticipantFragment()
            val transaction = parentFragmentManager.beginTransaction()
            transaction.replace(R.id.fragment_container, fragment)
            transaction.addToBackStack(null)
            transaction.commit()

            txtNom.text.clear()
            txtMon.text.clear()
        }



        return view
    }

}