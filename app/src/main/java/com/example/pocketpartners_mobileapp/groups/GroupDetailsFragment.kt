package com.example.pocketpartners_mobileapp.groups

import Beans.Expense
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.util.Log
import Interface.PlaceHolder
import Beans.ExpenseResponse
import Beans.Grupo
import android.content.SharedPreferences
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.pocketpartners_mobileapp.payments.ExpenseAdapter
import com.example.pocketpartners_mobileapp.R
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class GroupDetailsFragment : Fragment() {

    private var groupId: Long? = null
    private lateinit var expensesAdapter: ExpenseAdapter
    private lateinit var expensesRecyclerView: RecyclerView
    private lateinit var expensesMessageTextView: TextView
    private lateinit var sharedPreferences: SharedPreferences

    companion object {
        private const val ARG_GROUP_ID = "group_id"
        private const val ARG_AUTH_TOKEN = "auth_token"

        fun newInstance(groupId: Long, authToken: String): GroupDetailsFragment {
            val fragment = GroupDetailsFragment()
            val args = Bundle()
            args.putLong(ARG_GROUP_ID, groupId)
            args.putString(ARG_AUTH_TOKEN, authToken)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_group_details, container, false)

        sharedPreferences = requireActivity().getSharedPreferences("user_prefs", AppCompatActivity.MODE_PRIVATE)

        // Obtener el groupId y el token de autorización pasados como argumento
        groupId = arguments?.getLong(ARG_GROUP_ID)
        val authToken = arguments?.getString(ARG_AUTH_TOKEN)
        val userId = sharedPreferences.getLong("user_id", -1L).toInt()

        val tvGroupName = view.findViewById<TextView>(R.id.tvGroupName)
        val backBtn = view.findViewById<ImageView>(R.id.backButton)
        //val btnMenu = view.findViewById<Button>(R.id.btnMenu)
        val btnAddExpense = view.findViewById<Button>(R.id.btnAddExpense)
        expensesRecyclerView = view.findViewById(R.id.rvExpenses)
        expensesMessageTextView = view.findViewById(R.id.message_text_view)

        // Configurar el RecyclerView para mostrar los gastos
        expensesRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        expensesAdapter = ExpenseAdapter()
        expensesRecyclerView.adapter = expensesAdapter

        // Cargar los gastos del grupo
        authToken?.let { token ->
            groupId?.let { id ->
                fetchGroupExpenses(token, id)
                fetchGroupName(token, id, tvGroupName) // Llamar a la función para obtener el nombre del grupo

            }
        } ?: run {
            expensesMessageTextView.text = "Error: Token de autorización o ID de grupo faltante"
            expensesMessageTextView.visibility = View.VISIBLE
        }

        backBtn.setOnClickListener(){
            parentFragmentManager.popBackStack()
        }

        // Acción al presionar el botón de añadir gastos
        btnAddExpense.setOnClickListener {
            // Verifica que los IDs no sean nulos o valores predeterminados
            if (groupId != null && userId != -1) {
                // Crea instancia de AddExpenseFragment con los argumentos necesarios
                val addExpenseFragment = AddExpenseFragment.newInstance(groupId!!, userId)

                // Navegar al AddExpenseFragment
                requireActivity().supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, addExpenseFragment)  // Reemplaza con el contenedor de tu fragmento
                    .addToBackStack(null)  // Permite regresar al fragmento anterior
                    .commit()
            } else {
                Toast.makeText(requireContext(), "Error: No se pudo obtener el ID del grupo o del usuario", Toast.LENGTH_SHORT).show()
            }
        }


        return view
    }

    private fun getRetrofitInstance(): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://pocket-partners-backend-production.up.railway.app/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
    private fun fetchGroupName(authToken: String, groupId: Long, tvGroupName: TextView) {
        val authHeader = "Bearer ${sharedPreferences.getString("auth_token", null)}"
        val service = getRetrofitInstance().create(PlaceHolder::class.java)
        val call = service.getGruposPorGroupId(authHeader, groupId.toInt())

        call.enqueue(object : Callback<Grupo> {
            override fun onResponse(call: Call<Grupo>, response: Response<Grupo>) {
                if (response.isSuccessful) {
                    val groupName = response.body()?.name
                    tvGroupName.text = groupName ?: "Nombre del grupo no disponible"
                } else {
                    tvGroupName.text = "Error al obtener el nombre del grupo"
                }
            }

            override fun onFailure(call: Call<Grupo>, t: Throwable) {
                tvGroupName.text = "Error de conexión"
            }
        })
    }

    private fun fetchGroupExpenses(authToken: String, groupId: Long) {
        val authHeader = "Bearer ${sharedPreferences.getString("auth_token", null)}"
        val service = getRetrofitInstance().create(PlaceHolder::class.java)
        Log.d("GroupDetailsFragment", "Auth Header: $authHeader")
        Log.d("GroupDetailsFragment", "Group ID: $groupId")
        val call = service.getExpensesByExpenseGroupId(authHeader, groupId)

        call.enqueue(object : Callback<List<ExpenseResponse>> {
            override fun onResponse(call: Call<List<ExpenseResponse>>, response: Response<List<ExpenseResponse>>) {
                if (response.isSuccessful) {
                    val expenseResponses = response.body()
                    if (expenseResponses.isNullOrEmpty()) {
                        expensesMessageTextView.text = "No hay gastos en este grupo"
                        expensesMessageTextView.visibility = View.VISIBLE
                        expensesRecyclerView.visibility = View.GONE
                    } else {
                        // Mapea la lista de ExpenseResponse a Expense
                        val expenses = expenseResponses.map { mapExpenseResponseToExpense(it) }
                        expensesAdapter.setExpenses(expenses) // Aquí ya es de tipo List<Expense>
                        expensesMessageTextView.visibility = View.GONE
                        expensesRecyclerView.visibility = View.VISIBLE
                    }
                } else {
                    if (response.code() == 400) {
                        expensesMessageTextView.text = "No se encuentran gastos"
                        expensesMessageTextView.visibility = View.VISIBLE
                        expensesRecyclerView.visibility = View.GONE
                    }
                }
            }

            override fun onFailure(call: Call<List<ExpenseResponse>>, t: Throwable) {
                expensesMessageTextView.text = "Error de conexión"
                expensesMessageTextView.visibility = View.VISIBLE
                expensesRecyclerView.visibility = View.GONE
            }
        })
    }

    private fun mapExpenseResponseToExpense(expenseResponse: ExpenseResponse): Expense {
        return Expense(
            id = expenseResponse.id,
            name = expenseResponse.name,
            amount = expenseResponse.amount,
            userId = expenseResponse.userId.toInt(), // Convertimos el Long a Int
            groupId = expenseResponse.groupId,
            groupName = null // El campo `groupName` no está en ExpenseResponse, así que lo dejamos como null
        )
    }
}