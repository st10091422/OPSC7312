package com.opsc.opsc7312poe.ui.activity

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
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.opsc.opsc7312poe.R
import com.opsc.opsc7312poe.api.data.User
import com.opsc.opsc7312poe.api.local.LocalUser
import com.opsc.opsc7312poe.api.viewmodel.UserViewModel
import com.opsc.opsc7312poe.databinding.ActivityRegisterBinding

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding

    private lateinit var viewModel: UserViewModel

    private val firebaseAuth = FirebaseAuth.getInstance()
    // Google Sign-In client for managing Google sign-in
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
            .requestIdToken(getString(R.string.default_web_client_id)) // Token for Firebase
            .requestEmail() // Request the user's email
            .build()

        // Initialize the Google Sign-In client with the options
        googleSignInClient = GoogleSignIn.getClient(this, gso)


        binding.btnLogIn.setOnClickListener{
            userRegister()
        }

        binding.btnGoogle.setOnClickListener{
            googleRegister()
        }

        binding.redirectLogin.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java)) // Start LoginActivity
            finish()
        }
    }
    // Method to register the user
    private fun userRegister() {
        // Show a progress dialog while registering

        // Retrieve user input from the UI
        val username = binding.etUsername.text.toString()
        val email = binding.etEmail.text.toString()
        val password = binding.etUsername.text.toString() // Should be password field instead

        // Validate the input fields
        if (username.isBlank()) {
            Toast.makeText(this, "Username cannot be empty", Toast.LENGTH_SHORT).show() // Show logout message
            return
        }

        if (email.isEmpty()) {
            Toast.makeText(this, "Email cannot be empty", Toast.LENGTH_SHORT).show() // Show logout message
            return
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Invalid email format", Toast.LENGTH_SHORT).show() // Show logout message
            return
        }

        if (password.isBlank()) {
            Toast.makeText(this, "Password cannot be empty!", Toast.LENGTH_SHORT).show() // Show logout message
            return
        }

        // Create a new User object with the provided details
        val user = User(username = username, email = email, password = password)

        // Observe the authentication status for registration success or failure
        viewModel.status.observe(this) { status ->
            // Handle registration status changes (success or failure)
            if (status) {
                // Update the progress dialog for successful registration
                Toast.makeText(this, "Registered successfully!", Toast.LENGTH_SHORT).show() // Show logout message
                startActivity(Intent(this, LoginActivity::class.java)) // Start LoginActivity
                finish()
            } else {
                // Update the progress dialog for unsuccessful registration
                Toast.makeText(this, "User already exists!", Toast.LENGTH_SHORT).show() // Show logout message

            }
        }

        // Observe error messages related to registration
        viewModel.message.observe(this) { message ->
            // Handle error messages and show timeout dialog if necessary
            if (message == "timeout" || message.contains("Unable to resolve host")) {
                Toast.makeText(this, "Connection Timeout", Toast.LENGTH_SHORT).show() // Show logout message
                viewModel.register(user) // Attempt to register the user again

            }
        }

        // Call the register method to initiate the login process
        viewModel.register(user)
    }

    // Method to redirect to the login activity
    private fun redirectToLogin() {
        startActivity(Intent(this, LoginActivity::class.java)) // Start LoginActivity
        finish() // Finish current activity
    }

    // Method to initiate Google sign-in process
    fun googleRegister() {
        // Theis method were adapted from YouTube
        // https://youtu.be/suVgcrPwYKQ?si=2FCFY8EXmnnaZuh0
        // Easy Tuto
        // https://www.youtube.com/@EasyTuto1

        firebaseAuth.signOut() // Sign out of Firebase to ensure a clean login

        // Sign out from Google account
        googleSignInClient.signOut().addOnCompleteListener {
            // Start Google sign-in intent
            val signInIntent = googleSignInClient.signInIntent
            startActivityForResult(signInIntent, RC_SIGN_IN) // Launch sign-in activity
        }
    }

    // Handle the result from the Google sign-in activity
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data) // Call the superclass method
        // Theis method were adapted from YouTube
        // https://youtu.be/suVgcrPwYKQ?si=2FCFY8EXmnnaZuh0
        // Easy Tuto
        // https://www.youtube.com/@EasyTuto1

        if (requestCode == RC_SIGN_IN) {
            // Get the sign-in task result
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInResult(task) // Handle the result
        }
    }

    // Process the result of Google sign-in
    private fun handleSignInResult(task: Task<GoogleSignInAccount>) {
        // Theis method were adapted from YouTube
        // https://youtu.be/suVgcrPwYKQ?si=2FCFY8EXmnnaZuh0
        // Easy Tuto
        // https://www.youtube.com/@EasyTuto1

        try {
            // Get the signed-in account details
            val account = task.getResult(ApiException::class.java)
            firebaseAuthWithGoogle(account.idToken!!) // Authenticate with Firebase using the Google ID token
        } catch (e: ApiException) {
            // Handle error during sign-in
            Log.d("error", "something went wrong...") // Log the error
            Toast.makeText(this, "${e.statusCode}", Toast.LENGTH_SHORT).show() // Show error message
        }
    }

    // Authenticate with Firebase using Google credentials
    private fun firebaseAuthWithGoogle(idToken: String) {
        // Theis method were adapted from YouTube
        // https://youtu.be/suVgcrPwYKQ?si=2FCFY8EXmnnaZuh0
        // Easy Tuto
        // https://www.youtube.com/@EasyTuto1

        val credential = GoogleAuthProvider.getCredential(idToken, null) // Get Google credentials
        firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val firebaseUser = firebaseAuth.currentUser // Get current user

                    // Send ID token to the backend if user exists
                    if (firebaseUser != null) {
                        firebaseUser.displayName?.let { firebaseUser.email?.let { it1 ->
                            sendTokenToBackend(it, it1) // Send token and email to the backend
                        } }
                    }
                } else {
                    // Handle error during Firebase authentication
                    Toast.makeText(this, "something went wrong:firebaseAuthWithGoogle", Toast.LENGTH_SHORT).show()
                }
            }
    }

    // Send the user's token to the backend for further processing
    private fun sendTokenToBackend(username: String, email: String) {
        val user = User(username = username, email = email) // Create a new User object

        // Show a progress dialog while sending the token
        // Observe the authentication status for success or failure
        viewModel.status.observe(this) { status ->
            // This observer implementation was adapted from stackoverflow
            // https://stackoverflow.com/questions/47025233/android-lifecycle-library-cannot-add-the-same-observer-with-different-lifecycle
            // Kevin Robatel
            // https://stackoverflow.com/users/244702/kevin-robatel

            if (status) {
                // Update the progress dialog for successful registration
                Toast.makeText(this, "Connection Timeout", Toast.LENGTH_SHORT).show() // Show logout message
                startActivity(Intent(this, LoginActivity::class.java)) // Start LoginActivity
                finish()
            } else {
                // Update the progress dialog for unsuccessful registration
                Toast.makeText(this, "Registration failed", Toast.LENGTH_SHORT).show() // Show logout message
            }
        }

        // Observe error messages related to sending the token
        viewModel.message.observe(this) { message ->
            // This observer implementation was adapted from stackoverflow
            // https://stackoverflow.com/questions/47025233/android-lifecycle-library-cannot-add-the-same-observer-with-different-lifecycle
            // Kevin Robatel
            // https://stackoverflow.com/users/244702/kevin-robatel

            // Handle error messages and show timeout dialog if necessary
            if (message == "timeout" || message.contains("Unable to resolve host")) {
                Toast.makeText(this, "Connection Timeout", Toast.LENGTH_SHORT).show() // Show logout message

                viewModel.registerWithSSO(user) // Attempt to register the user again

            }
        }

        // Call the registerWithSSO method to initiate the login process
        viewModel.registerWithSSO(user)
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