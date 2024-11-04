package com.opsc.opsc7312.ui.fragment

import android.content.Context
import android.content.SharedPreferences
import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.SwitchCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.opsc.opsc7312.R
import com.opsc.opsc7312.databinding.FragmentMainSettingsBinding


class MainSettingsFragment : Fragment() {
    private lateinit var binding: FragmentMainSettingsBinding
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentMainSettingsBinding.inflate(layoutInflater)
        sharedPreferences = requireActivity().getSharedPreferences("settings", Context.MODE_PRIVATE)

        applySwitchStyles()

        // Load saved preferences from SharedPreferences to set switch states
        binding.switchMode.isChecked =
            sharedPreferences.getBoolean("Mode", false) // Assuming default is false for dark mode

        setupListeners()

        binding.backButton.setOnClickListener {
            findNavController().navigateUp() // Navigate back using Navigation Component
        }

        binding.notifications.setOnClickListener {
            findNavController().navigate(R.id.action_mainSettingsFragment_to_notificationSettingsFragment)
        }

        binding.language.setOnClickListener {
            findNavController().navigate(R.id.action_mainSettingsFragment_to_languageFragment)
        }

        return binding.root
    }

    private fun applySwitchStyles() {
        // Create ColorStateList for thumb tint
        // This method was adapted from stackoverflow
        // https://stackoverflow.com/questions/3624280/how-to-use-sharedpreferences-in-android-to-store-fetch-and-edit-values
        // Harneet Kaur
        // https://stackoverflow.com/users/1444525/harneet-kaur
        // Ziem

        // Create ColorStateList for track tint
        val tintColor = ColorStateList(
            arrayOf(
                intArrayOf(android.R.attr.state_checked),  // Checked state
                intArrayOf(-android.R.attr.state_checked) // Unchecked state
            ), intArrayOf(
                ContextCompat.getColor(requireContext(), R.color.yellow_900), // Color when checked
                ContextCompat.getColor(requireContext(), R.color.teal_900) // Color when unchecked
            )
        )

        setTint(binding.switchMode, tintColor)
    }

    private fun setTint(switch: SwitchCompat, tint: ColorStateList) {
        switch.apply {
            thumbTintList = tint
            trackTintList = tint
        }
    }

    //Sets up listeners for each switch. When the state of a switch changes,
    //the corresponding preference is saved to SharedPreferences.
    private fun setupListeners() {
        // This method was adapted from stackoverflow
        // https://stackoverflow.com/questions/3624280/how-to-use-sharedpreferences-in-android-to-store-fetch-and-edit-values
        // Harneet Kaur
        // https://stackoverflow.com/users/1444525/harneet-kaur
        // Ziem

        binding.switchMode.setOnCheckedChangeListener { _, isChecked ->
            sharedPreferences.edit().putBoolean("Mode", isChecked).apply()
            // Apply dark theme immediately
            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
        }

        binding.notifications.setOnClickListener {
            findNavController().navigate(R.id.action_mainSettingsFragment_to_notificationSettingsFragment)
        }

        binding.language.setOnClickListener {
            findNavController().navigate(R.id.action_mainSettingsFragment_to_languageFragment)
        }
    }
}