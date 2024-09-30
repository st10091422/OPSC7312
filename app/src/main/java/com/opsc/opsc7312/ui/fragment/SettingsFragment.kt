package com.opsc.opsc7312.ui.fragment

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import com.opsc.opsc7312.MainActivity
import com.opsc.opsc7312.R
import com.opsc.opsc7312.api.local.LocalUser
import com.opsc.opsc7312.databinding.ActivityMainBinding
import com.opsc.opsc7312.databinding.FragmentSettingsBinding


class SettingsFragment : Fragment() {
    private lateinit var localUser: LocalUser
    private lateinit var binding: FragmentSettingsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSettingsBinding.inflate(layoutInflater)

        localUser = LocalUser.getInstance(requireContext())

        binding.editProfile.setOnClickListener {
            changeCurrentFragment(ProfileFragment())
        }

        // Listener for the language section, switches to LanguageFragment
        binding.security.setOnClickListener {
            changeCurrentFragment(SecurityFragment())
        }

        // Listener for the security section, switches to SecurityFragment
        binding.settings.setOnClickListener {
            changeCurrentFragment(MainSettingsFragment())
        }

        // Listener for the appearance section, switches to ThemeFragment
        binding.logout.setOnClickListener {
            showDialog()
        }

        return binding.root
    }

    private fun showDialog() {
// Inflate the custom dialog view
        val dialogView = layoutInflater.inflate(R.layout.confrim_dialog, null)

        // Create the AlertDialog using a custom view
        val dialogBuilder = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .create()

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
        dialogBuilder.show()    }

    private fun logout() {
        localUser.clearUser() // Clear the stored token
        Toast.makeText(requireContext(), "Logged out", Toast.LENGTH_SHORT).show() // Show logout message
        startActivity(Intent(requireContext(), MainActivity::class.java)) // Restart the MainActivity
    }

    // Function to replace the current fragment with a new one and add it to the back stack
    private fun changeCurrentFragment(fragment: Fragment) {
        // This method was adapted from stackoverflow
        // https://stackoverflow.com/questions/52318195/how-to-change-fragment-kotlin
        // Marcos Maliki
        // https://stackoverflow.com/users/8108169/marcos-maliki
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.frame_layout, fragment) // Replace the content of the frame layout
            .addToBackStack(null) // Add the transaction to the back stack
            .commit() // Commit the transaction
    }
}