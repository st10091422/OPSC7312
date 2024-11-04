package com.opsc.opsc7312poe.ui.activity

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import com.opsc.opsc7312poe.R

class SplashScreenActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Display splash screen layout
        setContentView(R.layout.activity_splash_screen)

        // Navigate to LaunchScreen2Activity after 3 seconds
        Handler().postDelayed({
            val intent = Intent(this, LaunchScreen2Activity::class.java)
            startActivity(intent)
            finish() // Close this activity
        }, 3000)
    }
}
