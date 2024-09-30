package com.opsc.opsc7312.ui.activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.opsc.opsc7312.R
import com.opsc.opsc7312.databinding.ActivityLaunchScreen2Binding

class LaunchScreen2Activity : AppCompatActivity() {
    private lateinit var binding: ActivityLaunchScreen2Binding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLaunchScreen2Binding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set up button click listeners
        binding.btnLogIn.setOnClickListener {
            // Navigate to the Login Activity
            startActivity(Intent(this, LoginActivity::class.java))
            finish() // Close this activity if you don't want to return here
        }

        binding.btnSignUp.setOnClickListener {
            // Navigate to the Sign Up Activity
            startActivity(Intent(this, RegisterActivity::class.java)) // Create SignUpActivity if it doesn't exist
            finish() // Close this activity if you don't want to return here
        }
    }
}
