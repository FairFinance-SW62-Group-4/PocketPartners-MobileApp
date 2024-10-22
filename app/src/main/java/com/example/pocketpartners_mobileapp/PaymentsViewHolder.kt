package com.example.pocketpartners_mobileapp

import Beans.Payment
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class PaymentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val descriptionTextView: TextView = itemView.findViewById(R.id.payment_description)
    private val amountTextView: TextView = itemView.findViewById(R.id.payment_amount)
    private val idTextView: TextView = itemView.findViewById(R.id.payment_id)

    fun bind(payment: Payment) {
        // Muestra la descripción y el ID del pago
        descriptionTextView.text = payment.description
        amountTextView.text = payment.amount.toString()
        idTextView.text = payment.id.toString()
    }
}