package com.opsc.opsc7312.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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

        return binding.root
    }
}