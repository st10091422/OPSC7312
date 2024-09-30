package com.opsc.opsc7312.ui.fragment

import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import com.opsc.opsc7312.R
import com.opsc.opsc7312.databinding.FragmentNotificationSettingsBinding
import com.opsc.opsc7312.databinding.FragmentSecurityBinding
import com.opsc.opsc7312.databinding.FragmentSettingsBinding


class SecurityFragment : Fragment() {
    private lateinit var binding: FragmentSecurityBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSecurityBinding.inflate(layoutInflater)

        binding.backButton.setOnClickListener {
            activity?.supportFragmentManager?.popBackStack()
        }

        binding.addFingerprint.setOnClickListener {
            showDialog()
        }
        return binding.root
    }

    private fun showDialog() {
// Inflate the custom dialog view
        val dialogView = layoutInflater.inflate(R.layout.coming_soon_dialog, null)

        // Create the AlertDialog using a custom view
        val dialogBuilder = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .create()

        val cancelButton: LinearLayout = dialogView.findViewById(R.id.cancelButton)
        val titleTextView: TextView = dialogView.findViewById(R.id.titleTextView)
        val messageTextView: TextView = dialogView.findViewById(R.id.messageTextView)

        // Optionally set a custom title or message if needed
        titleTextView.text = "Coming Soon"
        messageTextView.text = "This feature will be implemented in final POE"
        //buttonText.text = ""

        // Set click listeners for the buttons


        cancelButton.setOnClickListener {
            // Just close the dialog without doing anything
            dialogBuilder.dismiss()
        }

        // Show the dialog
        dialogBuilder.show()
    }
}