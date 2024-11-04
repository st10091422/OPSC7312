package com.opsc.opsc7312poe.ui.fragment

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.opsc.opsc7312poe.R
import com.opsc.opsc7312poe.databinding.FragmentLanguageBinding


class LanguageFragment : Fragment() {
    private lateinit var binding: FragmentLanguageBinding
    private lateinit var sharedPreferences: SharedPreferences


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLanguageBinding.inflate(layoutInflater)

        binding.backButton.setOnClickListener {
            activity?.supportFragmentManager?.popBackStack()
        }

        sharedPreferences = requireActivity().getSharedPreferences("settings", Context.MODE_PRIVATE)

        val selectedLanguage = sharedPreferences.getString("language", "English")
        val dropdownItems = arrayListOf("Afrikaans", "English",  "Zulu")

        val customAdapter = ArrayAdapter(requireContext(), R.layout.custom_spinner_item, dropdownItems)
        binding.spinner.adapter = customAdapter

        val selectedIndex = dropdownItems.indexOf(selectedLanguage)
        if (selectedIndex >= 0) {
            binding.spinner.setSelection(selectedIndex)
        }
        binding.spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                // Handle item selection
                val newLanguage = parent?.getItemAtPosition(position).toString()
                // You can save the selected language to SharedPreferences
                sharedPreferences.edit().putString("language", newLanguage).apply()

                // Optionally, show a message or take action based on the selected language
                // Example: Toast.makeText(requireContext(), "Selected: $selectedLanguage", Toast.LENGTH_SHORT).show()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Handle case when no item is selected (if needed)
            }
        }

        return binding.root
    }


}