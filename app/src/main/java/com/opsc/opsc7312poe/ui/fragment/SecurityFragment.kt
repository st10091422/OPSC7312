package com.opsc.opsc7312.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.opsc.opsc7312.R
import com.opsc.opsc7312.databinding.FragmentSecurityBinding

class SecurityFragment : Fragment() {
    private lateinit var binding: FragmentSecurityBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSecurityBinding.inflate(layoutInflater)

        binding.backButton.setOnClickListener {
            activity?.supportFragmentManager?.popBackStack()
        }

        // Set click listener for the "Change Password" option
        binding.changePassword.setOnClickListener {
            // Navigate to Security2Fragment
            findNavController().navigate(R.id.action_securityFragment_to_security2Fragment)
        }

        // Dark theme toggle
        binding.biometricsSwitch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
        }

        return binding.root
    }
}