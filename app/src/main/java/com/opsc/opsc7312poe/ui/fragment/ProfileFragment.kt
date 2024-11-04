package com.opsc.opsc7312poe.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.opsc.opsc7312poe.MainActivity
import com.opsc.opsc7312poe.api.data.User
import com.opsc.opsc7312poe.api.local.LocalUser
import com.opsc.opsc7312poe.api.viewmodel.UserViewModel
import com.opsc.opsc7312poe.databinding.FragmentProfileBinding


class ProfileFragment : Fragment() {

    private lateinit var binding: FragmentProfileBinding

    private lateinit var viewModel: UserViewModel

    private lateinit var localUser: LocalUser

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProfileBinding.inflate(layoutInflater)

        binding.backButton.setOnClickListener {
            activity?.supportFragmentManager?.popBackStack()
        }

        viewModel = ViewModelProvider(this).get(UserViewModel::class.java)

        localUser = LocalUser.getInstance(requireContext())

        val currentUser = localUser.getUser()



        if (currentUser != null) {
            binding.etUsername.setText(currentUser.username)
            binding.etEmail.setText(currentUser.email)
        } else {
            // Handle the scenario where the token is null (e.g., log an error or show a message).
            startActivity(Intent(requireContext(), MainActivity::class.java)) // Restart the MainActivity
        }

        binding.btnSave.setOnClickListener {
            if (currentUser != null) {
                updateProfile(currentUser.id)
            } else {
                // Handle the scenario where the token is null (e.g., log an error or show a message).
                startActivity(Intent(requireContext(), MainActivity::class.java)) // Restart the MainActivity
            }
        }

        return binding.root
    }

    private fun updateProfile(id: String){
        val email = binding.etEmail.text.toString()

        val username = binding.etUsername.text.toString()

        if(email.isEmpty()){
            Toast.makeText(requireContext(), "Enter an email", Toast.LENGTH_SHORT).show() // Show logout message
            return
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(requireContext(), "Enter a valid email format", Toast.LENGTH_SHORT).show() // Show logout message
        }

        if(username.isEmpty()){
            Toast.makeText(requireContext(), "Enter a password", Toast.LENGTH_SHORT).show() // Show logout message
            return
        }

        val user = User(email = email, username = username)

        Log.d("user", "$user")

        // Observe the authentication status for registration success or failure
        viewModel.status.observe(viewLifecycleOwner) { status ->
            // Handle registration status changes (success or failure)
            if (status) {
                // Update the progress dialog for successful registration

                // Dismiss the dialog after 2 seconds and redirect to the login screen
                Toast.makeText(requireContext(), "Profile update successful!", Toast.LENGTH_SHORT).show() // Show logout message

            } else {
                // Update the progress dialog for unsuccessful registration
                Toast.makeText(requireContext(), "profile update failed!", Toast.LENGTH_SHORT).show() // Show logout message

            }
        }

        // Observe error messages related to registration
        viewModel.message.observe(viewLifecycleOwner) { message ->
            // Handle error messages and show timeout dialog if necessary
            if (message == "timeout" || message.contains("Unable to resolve host")) {
                Toast.makeText(requireContext(), "connection timeout", Toast.LENGTH_SHORT).show() // Show logout message

                viewModel.updateUser(id, user) // Attempt to register the user again

            }
        }

        viewModel.userData.observe(viewLifecycleOwner) { user ->
            // This observer implementation was adapted from stackoverflow
            // https://stackoverflow.com/questions/47025233/android-lifecycle-library-cannot-add-the-same-observer-with-different-lifecycle
            // Kevin Robatel
            // https://stackoverflow.com/users/244702/kevin-robatel
            // Get the token expiration time from constants (e.g., 1 hour)

            val expiresIn = System.currentTimeMillis() + (24 * 60 * 60 * 1000)

            // Save user data to local storage
            localUser.saveUser(user, expiresIn)

            // Save user data to local storage
        }
        // Call the register method to initiate the login process
        viewModel.updateUser(id, user)
    }

}