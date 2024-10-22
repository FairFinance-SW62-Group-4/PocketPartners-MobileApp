import Beans.GroupJoin
import Beans.Grupo
import Interface.PlaceHolder
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
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

    companion object {
        private const val USER_ID = "user_id"

        fun newInstance(userId: Int): GroupsFragment {
            val fragment = GroupsFragment()
            val args = Bundle()
            args.putInt(USER_ID, userId)
            fragment.arguments = args
            return fragment
        }
    }

    private var userId: Int = 0
    lateinit var service: PlaceHolder
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        super.onCreate(savedInstanceState)
        arguments?.let {
            userId = it.getInt(USER_ID, 0)
        }

        // Inflar el layout para este fragmento
        val view = inflater.inflate(R.layout.fragment_groups, container, false)

        sharedPreferences = requireActivity().getSharedPreferences("user_prefs", AppCompatActivity.MODE_PRIVATE)

        val btnCrearGrupo = view.findViewById<Button>(R.id.btnCreateGroup)

        // Inicializar Retrofit
        val retrofit = Retrofit.Builder()
            .baseUrl("https://pocket-partners-backend-production.up.railway.app/")
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

        // Llamar a la función para obtener los grupos unidos
        getJoinedGroups(view)

        return view
    }

    private fun getJoinedGroups(view: View) {
        val authHeader = "Bearer ${sharedPreferences.getString("auth_token", null)}"
        Log.d("GroupsFragment", "Auth Header: $authHeader")

        service.getGruposUnidosPorUserId(authHeader, userId).enqueue(object : Callback<List<GroupJoin>> {
            override fun onResponse(call: Call<List<GroupJoin>>, response: Response<List<GroupJoin>>) {
                val groupJoins = response.body()
                val listaGrupos = mutableListOf<Grupo>()

                Log.d("GroupsFragment", "Response: $groupJoins")

                // Verifica que se hayan recibido datos
                if (groupJoins != null) {
                    val groupDetailsCalls = groupJoins.map { groupJoin ->
                        service.getGruposPorUserId(authHeader, groupJoin.groupId)
                    }

                    for (call in groupDetailsCalls) {
                        call.enqueue(object : Callback<Grupo> {
                            override fun onResponse(call: Call<Grupo>, response: Response<Grupo>) {
                                val grupo = response.body()
                                if (grupo != null) {
                                    listaGrupos.add(grupo)

                                    // Solo configuramos el RecyclerView si el fragmento aún está adjunto
                                    if (isAdded && listaGrupos.size == groupDetailsCalls.size) {
                                        setupRecyclerView(view, listaGrupos)
                                    }
                                }
                            }

                            override fun onFailure(call: Call<Grupo>, t: Throwable) {
                                t.printStackTrace()
                            }
                        })
                    }
                } else {
                    val errorMessage = "Código de error: ${response.code()}"
                    Log.e("GroupsFragment", "No group joins found: $errorMessage")
                }
            }

            override fun onFailure(call: Call<List<GroupJoin>>, t: Throwable) {
                t.printStackTrace()
                Log.e("GroupsFragment", "Error: ${t.message}")
            }
        })
    }

    private fun setupRecyclerView(view: View, grupos: List<Grupo>) {
        if (!isAdded) return  // Evitar la configuración si el fragmento no está adjunto a la actividad
        val recycler = view.findViewById<RecyclerView>(R.id.recyclerGroups)
        recycler.layoutManager = LinearLayoutManager(requireContext())
        recycler.adapter = GroupAdapter(grupos)
    }
}
