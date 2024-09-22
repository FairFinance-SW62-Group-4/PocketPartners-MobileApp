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
            val groupName = txtNom.text.toString()
            val groupPhoto = "URL de la foto" // Puedes modificar esto para obtener la URL de la foto
            val currency = txtMon.text.toString().split(",") // Supón que la moneda se ingresa como "PEN,USD"

            // Crear el bundle para pasar los datos al siguiente fragmento
            val bundle = Bundle().apply {
                putString("groupName", groupName)
                putString("groupPhoto", groupPhoto)
                putStringArray("currency", currency.toTypedArray())
            }

            val fragment = AddParticipantFragment()
            fragment.arguments = bundle
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