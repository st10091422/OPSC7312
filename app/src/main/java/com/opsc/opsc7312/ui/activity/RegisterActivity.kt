package com.opsc.opsc7312.ui.activity

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.auth.api.signin.GoogleSignInStatusCodes
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GetTokenResult
import com.google.firebase.auth.GoogleAuthProvider
import com.opsc.opsc7312.MainActivity
import com.opsc.opsc7312.R
import com.opsc.opsc7312.api.data.User
import com.opsc.opsc7312.api.local.LocalUser
import com.opsc.opsc7312.api.viewmodel.UserViewModel
import com.opsc.opsc7312.databinding.ActivityRegisterBinding

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    private lateinit var viewModel: UserViewModel
    private val firebaseAuth = FirebaseAuth.getInstance()
    private lateinit var googleSignInClient: GoogleSignInClient
    private val RC_SIGN_IN = 123
    private lateinit var localUser: LocalUser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this).get(UserViewModel::class.java)
        localUser = LocalUser.getInstance(this)

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id)).requestEmail().build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        binding.btnLogIn.setOnClickListener {
            userRegister()
        }

        binding.btnGoogle.setOnClickListener {
            googleRegister()
        }

        binding.redirectLogin.setOnClickListener {
            redirectToLogin() // Use the new method
        }
    }

    // Method to register the user
    private fun userRegister() {
        // Retrieve user input from the UI
        val username = binding.etUsername.text.toString()
        val email = binding.etEmail.text.toString()
        val password = binding.etPassword.text.toString() // Updated to the correct password field

        // Validate the input fields
        when {
            username.isBlank() -> {
                Toast.makeText(this, "Username cannot be empty", Toast.LENGTH_SHORT).show()
                return
            }

            email.isEmpty() -> {
                Toast.makeText(this, "Email cannot be empty", Toast.LENGTH_SHORT).show()
                return
            }

            !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                Toast.makeText(this, "Invalid email format", Toast.LENGTH_SHORT).show()
                return
            }

            password.isBlank() -> {
                Toast.makeText(this, "Password cannot be empty!", Toast.LENGTH_SHORT).show()
                return
            }
        }

        // Create a new User object with the provided details
        val user = User(username = username, email = email, password = password)

        // Observe the authentication status for registration success or failure
        viewModel.status.observe(this) { status ->
            if (status) {
                Toast.makeText(this, "Registered successfully!", Toast.LENGTH_SHORT).show()
                redirectToLogin() // Use the new method
            } else {
                Toast.makeText(this, "User already exists!", Toast.LENGTH_SHORT).show()
            }
        }

        // Observe error messages related to registration
        viewModel.status.observe(this) { status ->
            if (status) {
                Toast.makeText(this, "Registered successfully!", Toast.LENGTH_SHORT).show()
                redirectToLogin()
            } else {
                // Check for specific error messages
                val errorMessage = viewModel.message.value
                if (errorMessage == "User already exists") {
                    Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Registration failed!", Toast.LENGTH_SHORT).show()
                }
            }
        }

        viewModel.message.observe(this) { message ->
            if (message == "timeout" || message.contains("Unable to resolve host")) {
                Toast.makeText(this, "Connection Timeout", Toast.LENGTH_SHORT).show()
                // Instead of immediately retrying, you might want to add a delay or retry mechanism
                // viewModel.register(user) // Attempt to register the user again (with delay or retry logic)
            }
        }

        // Call the register method to initiate the login process
        viewModel.register(user)
    }

    // Method to redirect to the login activity
    private fun redirectToLogin() {
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }

    // Method to redirect to the login activity
    private fun redirectToHome() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    // Method to initiate Google sign-in process
    private fun googleRegister() {
        firebaseAuth.signOut() // Sign out of Firebase to ensure a clean login
        googleSignInClient.signOut().addOnCompleteListener {
            val signInIntent = googleSignInClient.signInIntent
            startActivityForResult(signInIntent, RC_SIGN_IN) // Launch sign-in activity
        }
    }

    // Handle the result from the Google sign-in activity
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInResult(task) // Handle the result
        }
    }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account = completedTask.getResult(ApiException::class.java)!!
            Log.d(TAG, "firebaseAuthWithGoogle:" + account.id)
            firebaseAuthWithGoogle(account.idToken!!)
        } catch (e: ApiException) {
            // Google Sign In failed, update UI appropriately
            Log.w(TAG, "Google sign in failed", e)
            when (e.statusCode) {
                GoogleSignInStatusCodes.SIGN_IN_CANCELLED -> {
                    // The user canceled the sign-in flow.
                    Toast.makeText(this, "Sign-in canceled", Toast.LENGTH_SHORT).show()
                }

                GoogleSignInStatusCodes.SIGN_IN_FAILED -> {
                    // Sign-in attempt failed.
                    Toast.makeText(this, "Sign-in failed", Toast.LENGTH_SHORT).show()
                }

                else -> {
                    // Other error codes.
                    Toast.makeText(this, "An unknown error occurred", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    // Authenticate with Firebase using Google credentials
    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener(this) { task ->
            if (task.isSuccessful) {
                val firebaseUser = firebaseAuth.currentUser
                firebaseUser?.getIdToken(true)
                    ?.addOnCompleteListener(OnCompleteListener<GetTokenResult> { getTokenTask ->
                        if (getTokenTask.isSuccessful) {
                            val token = getTokenTask.result?.token
                            token?.let {
                                sendTokenToBackend(
                                    firebaseUser.displayName ?: "", firebaseUser.email ?: "", it
                                )
                            }
                        } else {
                            // Handle error getting token
                            Toast.makeText(this, "Error getting token", Toast.LENGTH_SHORT).show()
                        }
                    })
            } else {
                Toast.makeText(this, "Firebase authentication failed", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Send the user's token to the backend for further processing
    private fun sendTokenToBackend(username: String, email: String, idToken: String) {
        Log.d("RegisterActivity", "User registered with Google Sign-In:")
        Log.d("RegisterActivity", "Username: $username")
        Log.d("RegisterActivity", "Email: $email")
        Log.d("RegisterActivity", "ID Token: $idToken") // Log the ID token

        Toast.makeText(this, "Google Sign-In successful!", Toast.LENGTH_SHORT).show()

        val user = User(
            username = username, email = email, token = idToken
        ) // Include ID token in User object

        viewModel.registerWithSSO(user) // Call ViewModel method to handle SSO registration

        viewModel.status.observe(this) { status ->
            if (status) {
                Toast.makeText(this, "Registration successful!", Toast.LENGTH_SHORT).show()
                redirectToHome() // Redirect to MainActivity after successful registration
            } else {
                // Check for specific error messages
                val errorMessage = viewModel.message.value
                if (errorMessage == "User already exists") {
                    Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
                    // You might want to redirect to login here if the user already exists
                    // redirectToLogin()
                } else {
                    Toast.makeText(this, "Registration failed!", Toast.LENGTH_SHORT).show()
                }
            }
        }

        viewModel.message.observe(this) { message ->
            if (message == "timeout" || message.contains("Unable to resolve host")) {
                Toast.makeText(this, "Connection Timeout!", Toast.LENGTH_SHORT).show()
                // Instead of immediately retrying, you might want to add a delay or retry mechanism
                // viewModel.registerWithSSO(user) // Attempt to register the user again (with delay or retry logic)
            }
        }
    }

    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount > 0) {
            supportFragmentManager.popBackStack() // Go back to the previous fragment
        } else {
            super.onBackPressed() // Default behavior to exit the app
        }
    }
}
