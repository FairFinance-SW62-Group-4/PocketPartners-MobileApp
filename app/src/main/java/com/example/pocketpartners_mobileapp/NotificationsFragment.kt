package com.example.pocketpartners_mobileapp

import NotificationsViewModel
import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider

class NotificationsFragment : Fragment() {

    companion object {
        private const val USER_ID = "user_id"

        fun newInstance(userId: Int): NotificationsFragment {
            val fragment = NotificationsFragment()
            val args = Bundle()
            args.putInt(USER_ID, userId)
            fragment.arguments = args
            return fragment
        }
    }

    private var userId: Int = 0

    private lateinit var notificationList: LinearLayout
    private lateinit var viewModel: NotificationsViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        super.onCreate(savedInstanceState)
        arguments?.let {
            userId = it.getInt(USER_ID, 0)
        }

        val view = inflater.inflate(R.layout.fragment_notifications, container, false)

        notificationList = view.findViewById(R.id.notification_list)
        val addButton: Button = view.findViewById(R.id.btn_add)

        viewModel = ViewModelProvider(this).get(NotificationsViewModel::class.java)

        viewModel.notifications.observe(viewLifecycleOwner) { notifications ->
            updateNotificationList(notifications)
        }

        addButton.setOnClickListener {
            showAddNotificationDialog()
        }

        return view
    }

    private fun updateNotificationList(notifications: List<String>) {
        notificationList.removeAllViews()
        notifications.forEach { notificationText ->
            addNotification(notificationText)
        }
    }

    private fun showAddNotificationDialog() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Agregar Notificación")

        val input = EditText(requireContext())
        builder.setView(input)

        builder.setPositiveButton("Agregar") { _, _ ->
            val notificationText = input.text.toString()
            if (notificationText.isNotEmpty()) {
                viewModel.addNotification(notificationText)
            }
        }
        builder.setNegativeButton("Cancelar") { dialog, _ -> dialog.cancel() }

        builder.show()
    }

    private fun addNotification(notificationText: String) {
        val cardView = CardView(requireContext())
        cardView.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ).apply {
            setMargins(0, 16, 0, 0)
        }
        cardView.setBackgroundResource(R.drawable.notification_background)
        cardView.setCardElevation(4f)
        cardView.setPadding(16, 16, 16, 16)

        val textView = TextView(requireContext())
        textView.text = notificationText
        textView.setTextColor(resources.getColor(android.R.color.black))
        textView.textSize = 16f
        textView.setTypeface(null, android.graphics.Typeface.BOLD)
        textView.setPadding(16, 16, 16, 16)

        cardView.addView(textView)
        notificationList.addView(cardView)

        cardView.setOnClickListener {
            viewModel.removeNotification(notificationText)
            notificationList.removeView(cardView)
        }
    }
}
