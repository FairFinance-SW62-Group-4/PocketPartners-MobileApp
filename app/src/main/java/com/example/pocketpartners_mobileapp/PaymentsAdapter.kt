package com.example.pocketpartners_mobileapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import Beans.Payment

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