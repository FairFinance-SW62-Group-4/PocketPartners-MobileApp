package com.example.pocketpartners_mobileapp

import Beans.Expense
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ExpensesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val descriptionTextView: TextView = itemView.findViewById(R.id.expense_description)
    private val idTextView: TextView = itemView.findViewById(R.id.expense_id)

    fun bind(expense: Expense) {
        // Muestra la descripción y el ID del gasto
        descriptionTextView.text = expense.name
        idTextView.text = expense.groupId.toString()
    }
}