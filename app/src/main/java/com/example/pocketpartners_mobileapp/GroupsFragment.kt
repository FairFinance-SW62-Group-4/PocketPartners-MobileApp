import Beans.Grupo
import Interface.PlaceHolder
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.pocketpartners_mobileapp.CreateGroupFragment
import com.example.pocketpartners_mobileapp.GroupAdapter
import com.example.pocketpartners_mobileapp.R
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class GroupsFragment : Fragment() {

    lateinit var service: PlaceHolder

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflar el layout para este fragmento
        val view = inflater.inflate(R.layout.fragment_groups, container, false)

        val btnCrearGrupo=view.findViewById<Button>(R.id.btnCreateGroup)

        // Configurar window insets si es necesario (esto aplica principalmente para temas que usen transparencia y manejo de "Edge to Edge")
        ViewCompat.setOnApplyWindowInsetsListener(view.findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Inicializar Retrofit
        val retrofit = Retrofit.Builder()
            .baseUrl("https://pocket-partners-backend-production.up.railway.app/api/v1/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        service = retrofit.create(PlaceHolder::class.java)

        btnCrearGrupo.setOnClickListener {
            val fragment = CreateGroupFragment()
            val transaction = parentFragmentManager.beginTransaction()
            transaction.replace(R.id.fragment_container, fragment)
            transaction.addToBackStack(null)
            transaction.commit()
        }

        // Llamar a la función para obtener los grupos
        getGroups(view)

        return view
    }

    private fun getGroups(view: View) {
        service.getListadoGroups().enqueue(object : Callback<List<Grupo>> {
            override fun onResponse(call: Call<List<Grupo>>, response: Response<List<Grupo>>) {
                val gr = response.body()

                val listaG = mutableListOf<Grupo>()

                if (gr != null) {
                    for (item in gr) {
                        listaG.add(
                            Grupo(
                                item.id, item.name, item.currency, item.groupPhoto, item.createdAt, item.updatedAt
                            )
                        )
                    }
                    // Configurar el RecyclerView
                    val recycler = view.findViewById<RecyclerView>(R.id.recyclerGroups)
                    recycler.layoutManager = LinearLayoutManager(requireContext())
                    recycler.adapter = GroupAdapter(listaG)
                }
            }

            override fun onFailure(call: Call<List<Grupo>>, t: Throwable) {
                t.printStackTrace()
            }
        })
    }
}
