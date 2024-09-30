package com.opsc.opsc7312

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.google.android.material.navigation.NavigationView
import com.opsc.opsc7312.api.local.LocalUser
import com.opsc.opsc7312.databinding.ActivityMainBinding
import com.opsc.opsc7312.ui.activity.LoginActivity
import com.opsc.opsc7312.ui.fragment.CategoriesFragment
import com.opsc.opsc7312.ui.fragment.HomeFragment
import com.opsc.opsc7312.ui.fragment.SettingsFragment
import com.opsc.opsc7312.ui.fragment.TransactionsFragment

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private lateinit var localUser: LocalUser
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        localUser = LocalUser.getInstance(this)

        if (isLoggedIn()) {
            setupBottomNavigation()  // Initialize bottom navigation
        } else {
            navigateToLogin()      // Redirect to welcome screen
        }
    }



    // Sets up bottom navigation for fragment switching
    private fun setupBottomNavigation() {
        // Start with the HomeFragment as the initial fragment
        changeCurrentFragment(HomeFragment())

        // Set the listener for bottom navigation item selections
        binding.bottomNavigation.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.home -> changeCurrentFragment(HomeFragment())
                R.id.transactions -> changeCurrentFragment(TransactionsFragment())
                R.id.categories -> changeCurrentFragment(CategoriesFragment())
                R.id.settings -> changeCurrentFragment(SettingsFragment())

            }
            true
        }
    }

    // Changes the current displayed fragment and updates the toolbar title
    private fun changeCurrentFragment(fragment: Fragment) {
        // This method was adapted from stackoverflow
        // https://stackoverflow.com/questions/52318195/how-to-change-fragment-kotlin
        // Marcos Maliki
        // https://stackoverflow.com/users/8108169/marcos-maliki
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.frame_layout, fragment) // Replace the current fragment
            commit() // Commit the transaction
        }
    }

    // Navigates to the authentication screens if the user is not logged in
    private fun navigateToLogin() {
        startActivity(Intent(this, LoginActivity::class.java)) // Start WelcomeActivity
        finish() // Finish the current activity
    }

    // Checks if the user is currently logged in
    private fun isLoggedIn(): Boolean {
        val user = localUser.getUser() // Retrieve the authentication token
        val expirationTime = localUser.getTokenExpirationTime() // Get the token expiration time
        return user != null && !localUser.isTokenExpired(expirationTime) // Check if the token is valid
    }
}