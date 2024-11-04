package com.opsc.opsc7312poe.ui.fragment

import android.content.Context
import android.content.SharedPreferences
import android.content.res.ColorStateList
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SwitchCompat
import androidx.core.content.ContextCompat
import com.opsc.opsc7312poe.R
import com.opsc.opsc7312poe.databinding.FragmentMainSettingsBinding


class MainSettingsFragment : Fragment() {
    private lateinit var binding: FragmentMainSettingsBinding
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMainSettingsBinding.inflate(layoutInflater)

        sharedPreferences = requireActivity().getSharedPreferences("settings", Context.MODE_PRIVATE)

        applySwitchStyles()

        // Load saved preferences from SharedPreferences to set switch states
        binding.switchMode.isChecked = sharedPreferences.getBoolean("Mode", true)


        setupListeners()


        binding.backButton.setOnClickListener {
            activity?.supportFragmentManager?.popBackStack()
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
            ),
            intArrayOf(
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
            // Save preference for notification enabled state
            sharedPreferences.edit().putBoolean("Mode", isChecked).apply()

        }

        binding.notifications.setOnClickListener {
            changeCurrentFragment(NotificationSettingsFragment())
        }

        binding.language.setOnClickListener {
            changeCurrentFragment(LanguageFragment())
        }
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