package com.example.meteora

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.example.meteora.features.alarm.view.AlarmFragment
import com.example.meteora.features.favorites.view.FavoriteFragment
import com.example.meteora.features.homescreen.view.HomeFragment
import com.example.meteora.features.settings.view.SettingFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class AppNavigationActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_app_navigation)

        // Set the initial fragment to HomeFragment
        loadFragment(HomeFragment())

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNavigationView.setOnItemSelectedListener { menuItem ->
            var fragment: Fragment? = null
            when (menuItem.itemId) {
                R.id.page_1 -> fragment = HomeFragment()
                R.id.page_2 -> fragment = FavoriteFragment()
                R.id.page_3 -> fragment = AlarmFragment()
                R.id.page_4 -> fragment = SettingFragment()
            }
            if (fragment != null) {
                loadFragment(fragment)
            }
            true
        }
    }

    // Function to load a fragment into the fragment container
    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }
}
