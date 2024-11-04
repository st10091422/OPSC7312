package com.opsc.opsc7312

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI.setupWithNavController
import com.opsc.opsc7312.api.local.LocalUser
import com.opsc.opsc7312.databinding.ActivityMainBinding
import com.opsc.opsc7312.ui.activity.LoginActivity
import com.opsc.opsc7312.ui.helper.BiometricAuthHelper

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var localUser: LocalUser
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        localUser = LocalUser.getInstance(this)

        if (localUser.isTokenExpired(localUser.getTokenExpirationTime())) {
            val biometricAuthHelper = BiometricAuthHelper(this)
            biometricAuthHelper.authenticate { success ->
                if (success) {
                    // Token expired, but fingerprint authentication successful
                    // Extend token expiration or re-authenticate with backend
                    // For now, we'll just extend the token expiration
                    val newExpirationTime =
                        System.currentTimeMillis() + (24 * 60 * 60 * 1000) // Extend by 24 hours
                    val currentUser = localUser.getUser()
                    if (currentUser != null) {
                        localUser.saveUser(currentUser, newExpirationTime)
                    }
                    setupBottomNavigation()
                } else {
                    // Token expired and fingerprint authentication failed
                    navigateToLogin()
                }
            }
        } else {
            // Token is valid, proceed with normal activity setup
            setupBottomNavigation()
        }
    }

    private fun setupBottomNavigation() {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController
        setupWithNavController(binding.bottomNavigation, navController)
    }

    private fun navigateToLogin() {
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }

    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount > 0) {
            supportFragmentManager.popBackStack()
        } else {
            super.onBackPressed()
        }
    }


}