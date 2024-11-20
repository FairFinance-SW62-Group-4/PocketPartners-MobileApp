package com.example.pocketpartners_mobileapp.payments

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import Beans.Payment
import com.example.pocketpartners_mobileapp.R

class PaymentsAdapter : RecyclerView.Adapter<PaymentViewHolder>() {

    private var payments: List<Payment> = listOf()

    fun setPayments(payments: List<Payment>) {
        this.payments = payments
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PaymentViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.pending_payments_card, parent, false)
        return PaymentViewHolder(view)
    }

    override fun onBindViewHolder(holder: PaymentViewHolder, position: Int) {
        val payment = payments[position]
        holder.bind(payment)
    }

    override fun getItemCount(): Int {
        return payments.size
    }


}