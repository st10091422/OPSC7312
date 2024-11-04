package com.opsc.opsc7312.ui.helper

import android.content.Context
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.fragment.app.FragmentActivity
import java.util.concurrent.Executors

class BiometricAuthHelper(private val context: Context) {
    private val biometricManager = BiometricManager.from(context)
    private val executor = Executors.newSingleThreadExecutor()

    fun authenticate(callback: (Boolean) -> Unit) {
        if (canAuthenticate()) {
            val promptInfo =
                BiometricPrompt.PromptInfo.Builder().setTitle("Biometric Authentication")
                    .setSubtitle("Log in using your fingerprint").setNegativeButtonText("Cancel")
                    .build()

            val biometricPrompt = BiometricPrompt(context as FragmentActivity,
                executor,
                object : BiometricPrompt.AuthenticationCallback() {
                    override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                        super.onAuthenticationError(errorCode, errString)
                        callback(false) // Authentication failed
                    }

                    override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                        super.onAuthenticationSucceeded(result)
                        callback(true) // Authentication successful
                    }

                    override fun onAuthenticationFailed() {
                        super.onAuthenticationFailed()
                        callback(false) // Authentication failed
                    }
                })

            biometricPrompt.authenticate(promptInfo)
        } else {
            callback(false) // Biometric authentication not available
        }
    }

    private fun canAuthenticate(): Boolean {
        return biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG or BiometricManager.Authenticators.DEVICE_CREDENTIAL) == BiometricManager.BIOMETRIC_SUCCESS
    }

}