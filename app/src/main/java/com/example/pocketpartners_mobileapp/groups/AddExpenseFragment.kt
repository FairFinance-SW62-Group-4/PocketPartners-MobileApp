package com.example.pocketpartners_mobileapp.groups

import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import Interface.PlaceHolder
import Beans.AddExpense
import Beans.AddPayment
import Beans.ExpenseResponse
import Beans.FriendsList
import Beans.PaymentCompleted
import Beans.GroupOperationPost
import Beans.GroupOperationResponse
import Beans.Payment
import Beans.UsersInformation
import androidx.appcompat.app.AppCompatActivity
import com.example.pocketpartners_mobileapp.R
import com.example.pocketpartners_mobileapp.friends.MemberSelectionAdapter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class AddExpenseFragment : Fragment() {

    private lateinit var sharedPreferences: SharedPreferences
    private var groupId: Long = 0
    private var userId: Int = 0
    private lateinit var groupMembersRecyclerView: RecyclerView
    private lateinit var groupMembersAdapter: MemberSelectionAdapter // Cambiado a MemberSelectionAdapter
    private lateinit var expenseNameEditText: EditText
    private lateinit var expenseAmountEditText: EditText
    private lateinit var paidByUserCheckbox: CheckBox
    private lateinit var createExpenseButton: Button

    companion object {
        fun newInstance(groupId: Long, userId: Int): AddExpenseFragment {
            val fragment = AddExpenseFragment()
            val args = Bundle()
            args.putLong("groupId", groupId)
            args.putInt("userId", userId)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_add_expense, container, false)

        groupId = arguments?.getLong("groupId") ?: 0
        userId = arguments?.getInt("userId") ?: 0
        sharedPreferences = requireActivity().getSharedPreferences("user_prefs", AppCompatActivity.MODE_PRIVATE)

        expenseNameEditText = view.findViewById(R.id.etExpenseName)
        expenseAmountEditText = view.findViewById(R.id.etExpenseAmount)
        paidByUserCheckbox = view.findViewById(R.id.cbPaidByUser)
        createExpenseButton = view.findViewById(R.id.btnCreateExpense)
        val backBtn = view.findViewById<ImageView>(R.id.backButton)

        // Configuración del RecyclerView con el nuevo adaptador
        groupMembersRecyclerView = view.findViewById(R.id.rvGroupMembers)
        groupMembersRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        groupMembersAdapter = MemberSelectionAdapter(emptyList())
        groupMembersRecyclerView.adapter = groupMembersAdapter

        fetchGroupMembers()

        createExpenseButton.setOnClickListener {
            createExpense()
            parentFragmentManager.popBackStack()
        }
        backBtn.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
        return view
    }

    private fun fetchGroupMembers() {
        val authHeader = "Bearer ${sharedPreferences.getString("auth_token", null)}"
        val retrofit = getRetrofitInstance()
        val service = retrofit.create(PlaceHolder::class.java)

        // Llamada para obtener la lista de IDs de amigos
        service.getUserFriendsListById(authHeader, userId).enqueue(object : Callback<FriendsList> {
            override fun onResponse(call: Call<FriendsList>, response: Response<FriendsList>) {
                // Verificar si la respuesta es válida y la lista no está vacía
                response.body()?.let { friendsList ->
                    if (!friendsList.friendsIds.isNullOrEmpty()) {
                        fetchFriendDetails(friendsList.friendsIds) // Obtener detalles completos de cada amigo
                    } else {
                        Toast.makeText(requireContext(), "No tienes amigos en este grupo", Toast.LENGTH_SHORT).show()
                    }
                } ?: run {
                    Toast.makeText(requireContext(), "Error al obtener lista de amigos", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<FriendsList>, t: Throwable) {
                Toast.makeText(requireContext(), "Error al cargar miembros", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun fetchFriendDetails(friendIds: List<Int>) {
        val authHeader = "Bearer ${sharedPreferences.getString("auth_token", null)}"
        val retrofit = getRetrofitInstance()
        val service = retrofit.create(PlaceHolder::class.java)
        val friendsDetails = mutableListOf<UsersInformation>() // Lista para almacenar la información completa

        friendIds.forEach { friendId ->
            service.getUserInformation(authHeader, friendId).enqueue(object : Callback<UsersInformation> {
                override fun onResponse(call: Call<UsersInformation>, response: Response<UsersInformation>) {
                    response.body()?.let { userInfo ->
                        friendsDetails.add(userInfo)

                        // Solo actualizamos el adaptador cuando hemos recibido toda la información
                        if (friendsDetails.size == friendIds.size) {
                            groupMembersAdapter = MemberSelectionAdapter(friendsDetails)
                            groupMembersRecyclerView.adapter = groupMembersAdapter
                        }
                    }
                }

                override fun onFailure(call: Call<UsersInformation>, t: Throwable) {
                    Toast.makeText(requireContext(), "Error al cargar detalles de amigo", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }

    private fun createExpense() {
        val name = expenseNameEditText.text.toString()
        val amount = expenseAmountEditText.text.toString().toDoubleOrNull() ?: return
        val selectedMembers = groupMembersAdapter.getSelectedMembers()
        val authHeader = "Bearer ${sharedPreferences.getString("auth_token", null)}"
        val retrofit = getRetrofitInstance()
        val service = retrofit.create(PlaceHolder::class.java)

        val addExpense = AddExpense(name = name, amount = amount, userId = userId, groupId = groupId)
        service.postExpense(authHeader, addExpense).enqueue(object : Callback<ExpenseResponse> {
            override fun onResponse(call: Call<ExpenseResponse>, response: Response<ExpenseResponse>) {
                if (response.isSuccessful) {
                    response.body()?.let { expenseResponse ->
                        createGroupOperations(expenseResponse.id, amount, selectedMembers)
                    }
                } else {
                    Toast.makeText(requireContext(), "Error al crear gasto", Toast.LENGTH_SHORT).show()
                }
            }
            override fun onFailure(call: Call<ExpenseResponse>, t: Throwable) {
                Toast.makeText(requireContext(), "Error al conectar", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun createGroupOperations(expenseId: Long, totalAmount: Double, members: List<UsersInformation>) {
        val authHeader = "Bearer ${sharedPreferences.getString("auth_token", null)}"
        val retrofit = getRetrofitInstance()
        val service = retrofit.create(PlaceHolder::class.java)
        val splitAmount = totalAmount / members.size

        members.forEach { member ->
            val addPayment = AddPayment(
                description = "Payment for $expenseId",
                amount = splitAmount,
                userInformationId = member.id.toLong(),
                expenseId = expenseId
            )
            service.postPayment(authHeader, addPayment).enqueue(object : Callback<Payment> {
                override fun onResponse(call: Call<Payment>, response: Response<Payment>) {
                    if (response.isSuccessful) {
                        response.body()?.let { payment ->
                            val groupOperation = GroupOperationPost(
                                groupId = groupId.toInt(),
                                expenseId = expenseId.toInt(),
                                paymentId = payment.id.toInt()
                            )
                            service.postGroupOperation(authHeader, groupOperation).enqueue(object : Callback<GroupOperationResponse> {
                                override fun onResponse(call: Call<GroupOperationResponse>, response: Response<GroupOperationResponse>) {
                                    if (response.isSuccessful && member.id == userId && paidByUserCheckbox.isChecked) {
                                        service.paymentStatusCompleted(authHeader, payment.id).enqueue(object : Callback<PaymentCompleted> {
                                            override fun onResponse(call: Call<PaymentCompleted>, response: Response<PaymentCompleted>) {
                                                // Manejar el estado completado
                                            }
                                            override fun onFailure(call: Call<PaymentCompleted>, t: Throwable) {}
                                        })
                                    }
                                }
                                override fun onFailure(call: Call<GroupOperationResponse>, t: Throwable) {}
                            })
                        }
                    }
                }
                override fun onFailure(call: Call<Payment>, t: Throwable) {}
            })
        }
    }

    private fun getRetrofitInstance(): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://pocket-partners-backend-production.up.railway.app/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}