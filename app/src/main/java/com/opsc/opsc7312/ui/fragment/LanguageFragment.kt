package com.opsc.opsc7312.ui.fragment

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import com.opsc.opsc7312.R
import com.opsc.opsc7312.databinding.FragmentLanguageBinding
import java.util.Locale

class LanguageFragment : Fragment() {

    private lateinit var binding: FragmentLanguageBinding // Holds the UI elements of the fragment

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLanguageBinding.inflate(layoutInflater) // Inflate the layout for the fragment

        // Set click listener for the back button to go back to the previous screen
        binding.backButton.setOnClickListener {
            activity?.supportFragmentManager?.popBackStack()
        }

        // Define language options and their corresponding codes
        val dropdownItems = arrayListOf("Afrikaans", "English", "Zulu")
        val languageCodes = arrayListOf("af", "en", "zu")

        // Create an adapter for the spinner to display language options
        val customAdapter = ArrayAdapter(requireContext(), R.layout.custom_spinner_item, dropdownItems)
        binding.spinner.adapter = customAdapter // Set the adapter to the spinner


        // Get the current language and set the spinner to the current language
        val currentLocale = Locale.getDefault()
        val selectedIndex = languageCodes.indexOf(currentLocale.language)
        if (selectedIndex >= 0) {
            binding.spinner.setSelection(selectedIndex)
        }

        // Set a listener to detect when a language is selected from the spinner
        binding.spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val newLanguageCode = languageCodes[position] // Get the code of the selected language
                setLocale(newLanguageCode) // Change the app's language
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Do nothing if no language is selected
            }
        }

        return binding.root // Return the root view of the fragment
    }

    // Function to change the app's language
    private fun setLocale(languageCode: String) {
        val locale = Locale(languageCode) // Create a Locale object for the new language
        Locale.setDefault(locale) // Set the new language as the default
        val config = Configuration() // Create a Configuration object
        config.locale = locale // Set the new language in the Configuration
        requireActivity().resources.updateConfiguration(config, requireActivity().resources.displayMetrics) // Update the app's configuration
        activity?.recreate() // Restart the activity to apply the language change
    }
}