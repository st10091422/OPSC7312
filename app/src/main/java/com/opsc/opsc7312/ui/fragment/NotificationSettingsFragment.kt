package com.opsc.opsc7312.ui.fragment

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
import com.opsc.opsc7312.R
import com.opsc.opsc7312.databinding.FragmentNotificationSettingsBinding


class NotificationSettingsFragment : Fragment() {
    private lateinit var binding: FragmentNotificationSettingsBinding
    private lateinit var sharedPreferences: SharedPreferences
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentNotificationSettingsBinding.inflate(layoutInflater)

        sharedPreferences = requireActivity().getSharedPreferences("settings", Context.MODE_PRIVATE)

        // Apply custom colors to the switches
        applySwitchStyles()

        // Load saved preferences from SharedPreferences to set switch states
        binding.switchSound.isChecked = sharedPreferences.getBoolean("Sound", true)
        binding.switchVibrate.isChecked = sharedPreferences.getBoolean("Vibrate", true)
        binding.switchGeneral.isChecked = sharedPreferences.getBoolean("General", true)
        binding.switchExpenseReminder.isChecked = sharedPreferences.getBoolean("ExpenseReminder", true)
        binding.switchTransactionUpdate.isChecked = sharedPreferences.getBoolean("TransactionUpdate", true)


        // Set up listeners for each switch to save preferences when changed
        setupListeners()

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

       setTint(binding.switchGeneral, tintColor)
        setTint(binding.switchSound, tintColor)
        setTint(binding.switchVibrate, tintColor)
        setTint(binding.switchTransactionUpdate, tintColor)
        setTint(binding.switchExpenseReminder, tintColor)
    }

    private fun setTint(switch: SwitchCompat, tint: ColorStateList){
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

        binding.switchGeneral.setOnCheckedChangeListener { _, isChecked ->
            // Save preference for notification enabled state
            sharedPreferences.edit().putBoolean("General", isChecked).apply()
        }

        binding.switchSound.setOnCheckedChangeListener { _, isChecked ->
            // Save preference for purchase notifications
            sharedPreferences.edit().putBoolean("Sound", isChecked).apply()
        }

        binding.switchVibrate.setOnCheckedChangeListener { _, isChecked ->
            // Save preference for goal progress notifications
            sharedPreferences.edit().putBoolean("Vibrate", isChecked).apply()
        }

        binding.switchTransactionUpdate.setOnCheckedChangeListener { _, isChecked ->
            // Save preference for goal reached alerts
            sharedPreferences.edit().putBoolean("TransactionUpdate", isChecked).apply()
        }

        binding.switchExpenseReminder.setOnCheckedChangeListener { _, isChecked ->
            // Save preference for profile update notifications
            sharedPreferences.edit().putBoolean("ExpenseReminder", isChecked).apply()
        }

    }

}