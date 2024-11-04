package com.opsc.opsc7312.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.opsc.opsc7312.api.data.User
import com.opsc.opsc7312.api.local.LocalUser
import com.opsc.opsc7312.api.viewmodel.UserViewModel
import com.opsc.opsc7312.databinding.FragmentSecurity2Binding

class Security2Fragment : Fragment() {

    private lateinit var binding: FragmentSecurity2Binding
    private val userViewModel: UserViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSecurity2Binding.inflate(inflater, container, false)

        binding.backButton.setOnClickListener {
            activity?.supportFragmentManager?.popBackStack()
        }

        binding.btnUpdatePassword.setOnClickListener {
            val currentPassword = binding.etCurrentPassword.text.toString()
            val newPassword = binding.etNewPassword.text.toString()
            val confirmPassword = binding.etConfirmNewPassword.text.toString()

            if (newPassword == confirmPassword) {
                // Get user ID from LocalUser
                val userId = LocalUser.getInstance(requireContext()).getUser()?.id

                if (userId != null) {
                    val updatedUser = User(password = newPassword)

                    userViewModel.updateUser(userId, updatedUser)

                    userViewModel.status.observe(viewLifecycleOwner) { isSuccess ->
                        if (isSuccess) {
                            Toast.makeText(requireContext(), "Password updated successfully", Toast.LENGTH_SHORT).show()
                            activity?.supportFragmentManager?.popBackStack()
                        } else {
                            Toast.makeText(requireContext(), "Password update failed", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    // Handle case where user ID is not available (e.g., user not logged in)
                    Toast.makeText(requireContext(), "User not logged in", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(requireContext(), "Passwords do not match", Toast.LENGTH_SHORT).show()
            }
        }

        return binding.root
    }
}