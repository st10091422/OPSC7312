package com.opsc.opsc7312poe.ui.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricPrompt
import androidx.biometric.BiometricPrompt.PromptInfo
import androidx.core.content.ContextCompat
import com.opsc.opsc7312poe.MainActivity
import com.opsc.opsc7312poe.api.local.LocalUser
import com.opsc.opsc7312poe.databinding.ActivityLaunchScreen2Binding
import java.util.concurrent.Executor

class LaunchScreen2Activity : AppCompatActivity() {
    private lateinit var binding: ActivityLaunchScreen2Binding



    private lateinit var executor: Executor
    private lateinit var biometricPrompt: BiometricPrompt
    private lateinit var promptInfo: PromptInfo

    private lateinit var localUser: LocalUser


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLaunchScreen2Binding.inflate(layoutInflater)
        setContentView(binding.root)

        localUser = LocalUser.getInstance(this)

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

        // Check if the user is logged in and set up navigation accordingly
        // Check if user is logged in, then trigger biometric authentication if enabled
        if (isLoggedIn()) {
            fingerprintLogin(this)  // Trigger biometric authentication when app is resumed
        }
    }

    private fun fingerprintLogin(activity: AppCompatActivity) { // Generalized to handle both MainActivity and WelcomeActivity

        executor = ContextCompat.getMainExecutor(this)
        biometricPrompt = BiometricPrompt(this, executor,
            object : BiometricPrompt.AuthenticationCallback() {

                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    //Toast.makeText(activity, "Auth error $errString", Toast.LENGTH_SHORT).show()
                }

                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    if (activity is LaunchScreen2Activity) {
                        activity.startActivity(Intent(activity, MainActivity::class.java))
                        activity.finish()
                    }
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    Toast.makeText(activity, "Auth failed", Toast.LENGTH_SHORT).show()
                }
            }
        )

        promptInfo = PromptInfo.Builder()
            .setTitle("Biometric Authentication")
            .setSubtitle("Fingerprint authentication required")
            .setNegativeButtonText("Cancel")
            .build()

        if (isLoggedIn()) {
            biometricPrompt.authenticate(promptInfo)
        } else {
            if (activity is LaunchScreen2Activity) {
                this.startActivity(Intent(activity, LoginActivity::class.java))
            }
        }
    }

    // Checks if the user is currently logged in
    private fun isLoggedIn(): Boolean {
        val user = localUser.getUser() // Retrieve the authentication token
        val expirationTime = localUser.getTokenExpirationTime() // Get the token expiration time
        return user != null && !localUser.isTokenExpired(expirationTime) // Check if the token is valid
    }
}
