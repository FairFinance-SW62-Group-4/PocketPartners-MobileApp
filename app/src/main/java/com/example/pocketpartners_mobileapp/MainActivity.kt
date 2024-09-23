package com.example.pocketpartners_mobileapp

import GroupsFragment
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.fragment.app.Fragment

class MainActivity : AppCompatActivity() {

    private var selectedFragmentId: Int = R.id.nav_home // Valor por defecto

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        enableEdgeToEdge()

        window.navigationBarColor = ContextCompat.getColor(this, R.color.navigation_bar_color)

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)

        // Cargar siempre el HomeFragment
        if (savedInstanceState == null) {
            loadFragment(HomeFragment())
            bottomNavigationView.selectedItemId = R.id.nav_home // Asegurarse de que se muestre "Home"
        } else {
            selectedFragmentId = savedInstanceState.getInt("selectedFragmentId", R.id.nav_home)
            bottomNavigationView.selectedItemId = selectedFragmentId // Actualizar la selección en la barra
            loadFragment(getFragmentById(selectedFragmentId))
        }

        bottomNavigationView.setOnItemSelectedListener { item ->
            var selectedFragment: Fragment? = null
            when (item.itemId) {
                R.id.nav_home -> selectedFragment = HomeFragment()
                R.id.nav_groups -> selectedFragment = GroupsFragment()
                R.id.nav_add_person -> selectedFragment = FriendsFragment()
                R.id.nav_notifications -> selectedFragment = NotificationsFragment() // Nueva línea para NotificationsFragment
            }

            if (selectedFragment != null) {
                loadFragment(selectedFragment)
            }
            true
        }
    }

    private fun loadFragment(fragment: Fragment) {
        val fragmentManager = supportFragmentManager
        if (fragmentManager.backStackEntryCount > 0) {
            fragmentManager.popBackStack(null, androidx.fragment.app.FragmentManager.POP_BACK_STACK_INCLUSIVE)
        }

        fragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }

    private fun getFragmentById(itemId: Int): Fragment {
        return when (itemId) {
            R.id.nav_groups -> GroupsFragment()
            R.id.nav_notifications -> NotificationsFragment() // Añadido aquí
            else -> HomeFragment() // Siempre devuelve HomeFragment si no es groups
        }
    }
}
