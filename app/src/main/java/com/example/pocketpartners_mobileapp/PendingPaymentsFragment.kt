package com.example.pocketpartners_mobileapp

import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import Interface.PlaceHolder
import Beans.Payment
import Beans.Expense
import android.util.Log
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class PendingPaymentsFragment : Fragment() {

    companion object {
        private const val USER_ID = "user_id"

        fun newInstance(userId: Int): PendingPaymentsFragment {
            val fragment = PendingPaymentsFragment()
            val args = Bundle()
            args.putInt(USER_ID, userId)
            fragment.arguments = args
            return fragment
        }
    }

    private var userId: Int = 0
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var paymentsAdapter: PaymentsAdapter
    private lateinit var expensesAdapter: ExpenseAdapter
    private lateinit var paymentsRecyclerView: RecyclerView
    private lateinit var expensesRecyclerView: RecyclerView
    private lateinit var paymentsMessageTextView: TextView
    private lateinit var expensesMessageTextView: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreate(savedInstanceState)
        arguments?.let {
            userId = it.getInt(USER_ID, 0)
        }

        val view = inflater.inflate(R.layout.fragment_pending_payments, container, false)
        sharedPreferences = requireActivity().getSharedPreferences("user_prefs", AppCompatActivity.MODE_PRIVATE)

        paymentsRecyclerView = view.findViewById(R.id.recycler_view_payments)
        expensesRecyclerView = view.findViewById(R.id.recycler_view_expenses)
        paymentsMessageTextView = view.findViewById(R.id.message_text_view)
        expensesMessageTextView = view.findViewById(R.id.message_text_view_expenses)

        paymentsRecyclerView.layoutManager = LinearLayoutManager(context)
        expensesRecyclerView.layoutManager = LinearLayoutManager(context)

        paymentsAdapter = PaymentsAdapter()
        expensesAdapter = ExpenseAdapter()

        paymentsRecyclerView.adapter = paymentsAdapter
        expensesRecyclerView.adapter = expensesAdapter

        fetchPendingPayments()
        fetchExpenses()

        return view
    }
    private fun getRetrofitInstance(): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://pocket-partners-backend-production.up.railway.app/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    private fun fetchPendingPayments() {
        val authHeader = "Bearer ${sharedPreferences.getString("auth_token", null)}"
        val service = getRetrofitInstance().create(PlaceHolder::class.java)
        val call = service.getPaymentsByUserId(authHeader, userId)

        call.enqueue(object : Callback<List<Payment>> {
            override fun onResponse(call: Call<List<Payment>>, response: Response<List<Payment>>) {
                if (response.isSuccessful) {
                    val payments = response.body()
                    if (payments.isNullOrEmpty()) {
                        paymentsMessageTextView.text = "Aun no hay pagos entrantes"
                        paymentsMessageTextView.visibility = View.VISIBLE
                        paymentsRecyclerView.visibility = View.GONE
                    } else {
                        paymentsAdapter.setPayments(payments)
                        paymentsMessageTextView.visibility = View.GONE
                        paymentsRecyclerView.visibility = View.VISIBLE
                    }
                } else {
                    if (response.code() == 404) {
                        paymentsMessageTextView.text = "Aun no hay pagos entrantes"
                    } else {
                        paymentsMessageTextView.text = "Error: ${response.code()}"
                    }
                    paymentsMessageTextView.visibility = View.VISIBLE
                    paymentsRecyclerView.visibility = View.GONE
                }
            }

            override fun onFailure(call: Call<List<Payment>>, t: Throwable) {
                paymentsMessageTextView.text = "Error onFailure funcion"
                paymentsMessageTextView.visibility = View.VISIBLE
                paymentsRecyclerView.visibility = View.GONE
            }
        })
    }

    private fun fetchExpenses() {
        val authHeader = "Bearer ${sharedPreferences.getString("auth_token", null)}"
        val service = getRetrofitInstance().create(PlaceHolder::class.java)
        val call = service.getAllExpenses(authHeader)

        call.enqueue(object : Callback<List<Expense>> {
            override fun onResponse(call: Call<List<Expense>>, response: Response<List<Expense>>) {
                if (response.isSuccessful) {
                    // Filtrar los gastos para excluir los del usuario logueado
                    val expenses = response.body()?.filter { it.userId != userId }
                    Log.d("PendingPaymentsFragment", "Filtered Expenses: $expenses")
                    if (expenses.isNullOrEmpty()) {
                        expensesMessageTextView.text = "Aun no hay gastos salientes"
                        expensesMessageTextView.visibility = View.VISIBLE
                        expensesRecyclerView.visibility = View.GONE
                    } else {
                        expensesAdapter.setExpenses(expenses)
                        expensesMessageTextView.visibility = View.GONE
                        expensesRecyclerView.visibility = View.VISIBLE
                    }
                } else {
                    // Manejo de errores
                    if (response.code() == 404) {
                        expensesMessageTextView.text = "Aun no hay gastos salientes"
                    } else {
                        expensesMessageTextView.text = "Error: ${response.code()}"
                    }
                    expensesMessageTextView.visibility = View.VISIBLE
                    expensesRecyclerView.visibility = View.GONE
                }
            }

            override fun onFailure(call: Call<List<Expense>>, t: Throwable) {
                expensesMessageTextView.text = "Error onFailure funcion"
                expensesMessageTextView.visibility = View.VISIBLE
                expensesRecyclerView.visibility = View.GONE
            }
        })
    }


}