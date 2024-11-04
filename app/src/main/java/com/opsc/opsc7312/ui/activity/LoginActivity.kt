package com.opsc.opsc7312.ui.activity

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
import com.opsc.opsc7312.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var viewModel: UserViewModel
    private val firebaseAuth = FirebaseAuth.getInstance()
    private lateinit var googleSignInClient: GoogleSignInClient
    private val RC_SIGN_IN = 123
    private lateinit var localUser: LocalUser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this).get(UserViewModel::class.java)
        localUser = LocalUser.getInstance(this)

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        binding.btnLogIn.setOnClickListener { userLogin() }
        binding.btnGoogle.setOnClickListener { googleSignIn() }
        binding.redirectRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
            finish()
        }
    }

    private fun userLogin() {
        val email = binding.etEmail.text.toString()
        val password = binding.etPassword.text.toString()

        if (email.isEmpty()) {
            Toast.makeText(this, "Enter an email", Toast.LENGTH_SHORT).show()
            return
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Enter a valid email format", Toast.LENGTH_SHORT).show()
            return
        }

        if (password.isEmpty()) {
            Toast.makeText(this, "Enter a password", Toast.LENGTH_SHORT).show()
            return
        }

        val user = User(email = email, password = password)

        viewModel.status.observe(this) { status ->
            if (status) {
                Toast.makeText(this, "Login successful!", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            } else {
                Toast.makeText(this, "Login failed!", Toast.LENGTH_SHORT).show()
            }
        }

        viewModel.message.observe(this) { message ->
            if (message == "timeout" || message.contains("Unable to resolve host")) {
                Toast.makeText(this, "Connection timeout", Toast.LENGTH_SHORT).show()
            }
        }

        viewModel.userData.observe(this) { userData ->
            val expiresIn = System.currentTimeMillis() + (24 * 60 * 60 * 1000)
            localUser.saveUser(userData, expiresIn)
        }

        viewModel.login(user)
    }

    private fun googleSignIn() {
        googleSignInClient.signOut().addOnCompleteListener {
            val signInIntent = googleSignInClient.signInIntent
            startActivityForResult(signInIntent, RC_SIGN_IN)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInResult(task)
        }
    }

    private fun handleSignInResult(task: Task<GoogleSignInAccount>) {
        try {
            val account = task.getResult(ApiException::class.java) ?: return
            firebaseAuthWithGoogle(account.idToken!!)
        } catch (e: ApiException) {
            Log.e("LoginActivity", "Google sign-in failed: ${e.message}")
            Toast.makeText(this, "Sign-in failed: ${e.statusCode}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val firebaseUser = firebaseAuth.currentUser
                    firebaseUser?.getIdToken(true)
                        ?.addOnCompleteListener(OnCompleteListener<GetTokenResult> { getTokenTask ->
                            if (getTokenTask.isSuccessful) {
                                val token = getTokenTask.result?.token
                                token?.let {
                                    sendTokenToBackend(firebaseUser.displayName ?: "", firebaseUser.email ?: "", it)
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

    private fun sendTokenToBackend(username: String, email: String, idToken: String){
        Log.d("LoginActivity", "User logged in with Google Sign-In:")
        Log.d("LoginActivity", "Username: $username")
        Log.d("LoginActivity", "Email: $email")
        Log.d("LoginActivity", "ID Token: $idToken") // Log the ID token

        Toast.makeText(this, "Google Sign-In successful!", Toast.LENGTH_SHORT).show()

        val user = User(username = username, email = email, token = idToken) // Include ID token in User object

        viewModel.loginWithSSO(user) // Call ViewModel method to handle SSO login

        viewModel.status.observe(this) { status ->
            if (status) {
                Toast.makeText(this, "Login successful!", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            } else {
                Toast.makeText(this, "Login failed!", Toast.LENGTH_SHORT).show()
            }
        }

        viewModel.message.observe(this) { message ->
            if (message == "timeout" || message.contains("Unable to resolve host")) {
                Toast.makeText(this, "Connection Timeout!", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
