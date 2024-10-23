package com.example.pocketpartners_mobileapp

import Interface.PlaceHolder
import Beans.Payment
import Beans.Expense
import Beans.ExpenseResponse
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

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
    private lateinit var service: PlaceHolder
    private lateinit var tableLayoutResumen: TableLayout

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreate(savedInstanceState)
        arguments?.let {
            userId = it.getInt(USER_ID, 0)
        }

        val view = inflater.inflate(R.layout.fragment_home, container, false)
        tableLayoutResumen = view.findViewById(R.id.tableLayoutResumen)
        Log.d("TableLayout", "TableLayout initialized: $tableLayoutResumen")

        val imgProfile = view.findViewById<ImageView>(R.id.profileImage)

        // Configurar padding para manejar las barras del sistema
        ViewCompat.setOnApplyWindowInsetsListener(view.findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        imgProfile.setOnClickListener {
            (activity as? MainActivity)?.logout()  // Llama al método logout de MainActivity
        }

        // Inicializar Retrofit para realizar las llamadas a la API
        val retrofit = Retrofit.Builder()
            .baseUrl("https://pocket-partners-backend-production.up.railway.app/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        service = retrofit.create(PlaceHolder::class.java)

        // Obtener los últimos pagos y mostrarlos
        getRecentPayments()

        return view
    }

    private fun getRecentPayments() {
        val sharedPreferences = requireActivity().getSharedPreferences("user_prefs", AppCompatActivity.MODE_PRIVATE)
        val authToken = sharedPreferences.getString("auth_token", null)

        if (authToken.isNullOrEmpty()) {
            Log.e("Auth Error", "El token de autenticación es nulo o está vacío")
            return
        }

        val authHeader = "Bearer $authToken"
        Log.d("Auth Header", "AuthHeader: $authHeader")

        // Limpia las filas anteriores del TableLayout pero mantiene el encabezado
        val childCount = tableLayoutResumen.childCount
        if (childCount > 1) {
            tableLayoutResumen.removeViews(1, childCount - 1)
        }

        service.getPaymentsByUserId(authHeader, userId).enqueue(object : Callback<List<Payment>> {
            override fun onResponse(call: Call<List<Payment>>, response: Response<List<Payment>>) {
                if (response.isSuccessful) {
                    Log.d("API Response", "Payments: ${response.body()}")
                    val payments = response.body()?.take(5)

                    if (payments.isNullOrEmpty()) {
                        Log.d("API Response", "No payments found.")
                        return
                    }

                    payments.forEach { payment ->
                        Log.d("Auth Header", "AuthHeader: $authHeader")
                        service.getExpensesByExpenseId(authHeader, payment.expenseId).enqueue(object : Callback<ExpenseResponse> {
                            override fun onResponse(call: Call<ExpenseResponse>, response: Response<ExpenseResponse>) {
                                if (response.isSuccessful) {
                                    val expense = response.body()
                                    if (expense != null) {
                                        Log.d("API Response", "Expense found: ${expense.name}")
                                        addPaymentRow(expense.name, payment.amount.toString(), payment.status)
                                    } else {
                                        Log.d("API Response", "No expense found for payment id: ${payment.expenseId}")
                                    }
                                } else {
                                    Log.e("Expense Error", "Expense response code: ${response.code()}")
                                }
                            }

                            override fun onFailure(call: Call<ExpenseResponse>, t: Throwable) {
                                t.printStackTrace()
                            }
                        })
                    }
                } else {
                    Log.e("Payments Error", "Response code: ${response.code()}")
                    if (response.code() == 401) {
                        Log.e("Auth Error", "Error 401: Autenticación fallida, token no válido o caducado")
                    }
                }
            }

            override fun onFailure(call: Call<List<Payment>>, t: Throwable) {
                t.printStackTrace()
            }
        })
    }

    private fun addPaymentRow(expenseName: String, amount: String, status: String) {
        Log.d("TableRow", "Adding row: Expense: $expenseName, Amount: $amount, Status: $status")

        val row = TableRow(requireContext())

        val expenseTextView = TextView(context)
        expenseTextView.text = expenseName
        expenseTextView.textAlignment = View.TEXT_ALIGNMENT_CENTER

        val amountTextView = TextView(context)
        amountTextView.text = amount
        amountTextView.textAlignment = View.TEXT_ALIGNMENT_CENTER

        val statusTextView = TextView(context)
        statusTextView.text = status
        statusTextView.textAlignment = View.TEXT_ALIGNMENT_CENTER

        row.addView(expenseTextView)
        row.addView(amountTextView)
        row.addView(statusTextView)

        // Verificar si la fila tiene datos
        Log.d("TableRow", "Row views: ${row.childCount}")

        // Añadir la fila al tableLayoutResumen
        tableLayoutResumen.addView(row)
        tableLayoutResumen.invalidate()

        // Verificar si la tabla ha recibido la nueva fila
        Log.d("TableRow", "Table now has ${tableLayoutResumen.childCount} rows")
    }
}