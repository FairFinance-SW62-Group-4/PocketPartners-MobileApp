package com.example.pocketpartners_mobileapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.example.pocketpartners_mobileapp.R

class HomeFragment : Fragment() {

    companion object {
        private const val USER_ID = "user_id"

        fun newInstance(userId: Int): HomeFragment {
            val fragment = HomeFragment()
            val args = Bundle()
            args.putInt(USER_ID, userId)
            fragment.arguments = args
            return fragment
        }
    }

    private var userId: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        super.onCreate(savedInstanceState)
        arguments?.let {
            userId = it.getInt(USER_ID, 0)
        }

        // Inflamos el layout para este fragmento
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        val imgProfile = view.findViewById<ImageView>(R.id.profileImage)

        // Aplicamos padding para manejar las barras del sistema si es necesario
        ViewCompat.setOnApplyWindowInsetsListener(view.findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        imgProfile.setOnClickListener {
            (activity as? MainActivity)?.logout()  // Llama al método logout de MainActivity
        }

        return view
    }
}
