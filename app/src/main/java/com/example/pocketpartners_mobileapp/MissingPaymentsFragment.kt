package com.example.pocketpartners_mobileapp

import Interface.PlaceHolder
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MissingPaymentsFragment : Fragment() {

    companion object {
        private const val USER_ID = "user_id"

        fun newInstance(userId: Int): MissingPaymentsFragment {
            val fragment = MissingPaymentsFragment()
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

        // Inflar el layout para este fragmento
        return inflater.inflate(R.layout.fragment_missing_payments, container, false)
    }

}
