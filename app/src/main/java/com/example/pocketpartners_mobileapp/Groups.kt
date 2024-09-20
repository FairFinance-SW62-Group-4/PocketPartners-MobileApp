package com.example.pocketpartners_mobileapp

import Beans.Grupo
import Interface.PlaceHolder
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Recycler
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class Groups : AppCompatActivity() {

    lateinit var service: PlaceHolder

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_groups)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val retrofit=Retrofit.Builder()
            .baseUrl("")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        service = retrofit.create<PlaceHolder>(PlaceHolder::class.java)
        getGroups()

    }

    private fun getGroups(){
        service.getListadoGroups().enqueue(object: Callback<List<Grupo>>{
            override fun onResponse(p0: Call<List<Grupo>>, p1: Response<List<Grupo>>) {
                val gr=p1?.body()

                val listaG= mutableListOf<Grupo>()

                if(gr!=null){
                    for(item in gr){
                        listaG.add(
                            Grupo(
                                item.id, item.name, item.currency, item.groupPhoto, item.createdAt, item.updatedAt
                            )
                        )
                    }
                    val recycler=findViewById<RecyclerView>(R.id.recyclerGroups)
                    recycler.layoutManager=LinearLayoutManager(applicationContext)
                    recycler.adapter=GroupAdapter(listaG)
                }
            }

            override fun onFailure(p0: Call<List<Grupo>>, p1: Throwable) {
                p1?.printStackTrace()
            }
        })
    }
}