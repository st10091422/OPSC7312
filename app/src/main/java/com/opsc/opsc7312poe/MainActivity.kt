package com.opsc.opsc7312poe

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.opsc.opsc7312poe.api.local.ConnectivityReceiver
import com.opsc.opsc7312poe.api.local.LocalUser
import com.opsc.opsc7312poe.api.local.db.sync.CategorySync
import com.opsc.opsc7312poe.api.local.db.sync.TransactionSync
import com.opsc.opsc7312poe.databinding.ActivityMainBinding
import com.opsc.opsc7312poe.ui.activity.LoginActivity
import com.opsc.opsc7312poe.ui.fragment.AnalyticsFragment
import com.opsc.opsc7312poe.ui.fragment.CategoriesFragment
import com.opsc.opsc7312poe.ui.fragment.HomeFragment
import com.opsc.opsc7312poe.ui.fragment.SettingsFragment
import com.opsc.opsc7312poe.ui.fragment.TransactionsFragment

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private lateinit var localUser: LocalUser
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        localUser = LocalUser.getInstance(this)

        setupBottomNavigation()  // Initialize bottom navigation

        val connectivityReceiver = ConnectivityReceiver {
            enqueueCategories()
            enqueueTransactions()
        }

        this.registerReceiver(connectivityReceiver, IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION))

    }

    private fun enqueueCategories() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val syncRequest = OneTimeWorkRequestBuilder<CategorySync>()
            .setConstraints(constraints)
            .build()

        WorkManager.getInstance(this).enqueue(syncRequest)
    }

    private fun enqueueTransactions() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val syncRequest = OneTimeWorkRequestBuilder<TransactionSync>()
            .setConstraints(constraints)
            .build()

        WorkManager.getInstance(this).enqueue(syncRequest)
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
                R.id.analysis -> changeCurrentFragment(AnalyticsFragment())
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


    override fun onBackPressed() {
        // Check if there are any fragments in the back stack
        if (supportFragmentManager.backStackEntryCount > 0) {
            supportFragmentManager.popBackStack() // Go back to the previous fragment
        } else {
            super.onBackPressed() // Default behavior to exit the app
        }
    }
}