package com.opsc.opsc7312.ui.fragment

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.opsc.opsc7312.MainActivity
import com.opsc.opsc7312.R
import com.opsc.opsc7312.api.local.LocalUser
import com.opsc.opsc7312.databinding.FragmentSettingsBinding


class SettingsFragment : Fragment() {
    private lateinit var localUser: LocalUser
    private lateinit var binding: FragmentSettingsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentSettingsBinding.inflate(layoutInflater)
        localUser = LocalUser.getInstance(requireContext())

        binding.editProfile.setOnClickListener {
            findNavController().navigate(R.id.action_settingsFragment_to_profileFragment)
        }

        binding.security.setOnClickListener {
            findNavController().navigate(R.id.action_settingsFragment_to_securityFragment)
        }

        binding.settings.setOnClickListener {
            findNavController().navigate(R.id.action_settingsFragment_to_mainSettingsFragment)
        }

        binding.logout.setOnClickListener {
            showDialog()
        }

        binding.backButton.setOnClickListener {
            findNavController().navigateUp() // Navigate back using Navigation Component
        }

        return binding.root
    }

    private fun showDialog() {
// Inflate the custom dialog view
        val dialogView = layoutInflater.inflate(R.layout.confrim_dialog, null)

        // Create the AlertDialog using a custom view
        val dialogBuilder = AlertDialog.Builder(requireContext()).setView(dialogView).create()

        // Find the dialog views
        val confirmButton: LinearLayout = dialogView.findViewById(R.id.confirmButton)
        val cancelButton: LinearLayout = dialogView.findViewById(R.id.cancelButton)
        val titleTextView: TextView = dialogView.findViewById(R.id.titleTextView)
        val messageTextView: TextView = dialogView.findViewById(R.id.messageTextView)
        val buttonText: TextView = dialogView.findViewById(R.id.btnConfirmTxt)

        // Optionally set a custom title or message if needed
        titleTextView.text = "End Session?"
        messageTextView.text = "Are you sure you want to \nlogout?"
        //buttonText.text = ""

        // Set click listeners for the buttons
        confirmButton.setOnClickListener {
            // Confirm delete action
            logout() // Call method to update the category
        }


        cancelButton.setOnClickListener {
            // Just close the dialog without doing anything
            dialogBuilder.dismiss()
        }

        // Show the dialog
        dialogBuilder.show()
    }

    private fun logout() {
        localUser.clearUser() // Clear the stored token
        Toast.makeText(requireContext(), "Logged out", Toast.LENGTH_SHORT)
            .show() // Show logout message
        startActivity(
            Intent(
                requireContext(),
                MainActivity::class.java
            )
        ) // Restart the MainActivity
    }
}