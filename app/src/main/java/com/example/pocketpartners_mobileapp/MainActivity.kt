package com.example.pocketpartners_mobileapp

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.fragment.app.Fragment

class MainActivity : AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferences
    private var selectedFragmentId: Int = R.id.nav_home
    private var userId: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inicializar SharedPreferences
        sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE)

        // Verificar si el usuario está autenticado
        if (!isUserAuthenticated()) {
            // Si no está autenticado, redirigir a LoginActivity
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish() // Cierra la MainActivity para que el usuario no pueda volver con el botón de atrás
            return
        }

        // Si está autenticado, obtener el ID del usuario
        userId = sharedPreferences.getInt("user_id", 0)

        setContentView(R.layout.activity_main)
        window.navigationBarColor = ContextCompat.getColor(this, R.color.navigation_bar_color)

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)

        // Cargar siempre el HomeFragment
        if (savedInstanceState == null) {
            loadFragment(HomeFragment.newInstance(userId)) // Pasar userId a los Fragments
            bottomNavigationView.selectedItemId = R.id.nav_home
        } else {
            selectedFragmentId = savedInstanceState.getInt("selectedFragmentId", R.id.nav_home)
            bottomNavigationView.selectedItemId = selectedFragmentId
            loadFragment(getFragmentById(selectedFragmentId))
        }

        bottomNavigationView.setOnItemSelectedListener { item ->
            var selectedFragment: Fragment? = null
            when (item.itemId) {
                R.id.nav_home -> selectedFragment = HomeFragment.newInstance(userId)
                R.id.nav_groups -> selectedFragment = GroupsFragment.newInstance(userId)
                R.id.nav_add_person -> selectedFragment = FriendsFragment.newInstance(userId)
                R.id.nav_notifications -> selectedFragment = NotificationsFragment.newInstance(userId)
                R.id.nav_payment -> selectedFragment = MissingPaymentsFragment.newInstance(userId)
            }

            if (selectedFragment != null) {
                loadFragment(selectedFragment)
            }
            true
        }
    }

    private fun loadFragment(fragment: Fragment) {
        val fragmentManager = supportFragmentManager
        fragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }

    private fun getFragmentById(itemId: Int): Fragment {
        return when (itemId) {
            R.id.nav_groups -> GroupsFragment.newInstance(userId)
            R.id.nav_notifications -> NotificationsFragment.newInstance(userId)
            else -> HomeFragment.newInstance(userId)
        }
    }

    private fun isUserAuthenticated(): Boolean {
        val userId = sharedPreferences.getInt("user_id", 0)
        return userId != 0
    }
}