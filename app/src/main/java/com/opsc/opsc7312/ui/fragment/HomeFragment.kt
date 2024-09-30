package com.opsc.opsc7312.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.opsc.opsc7312.R
import com.opsc.opsc7312.databinding.FragmentCreateTransactionBinding
import com.opsc.opsc7312.databinding.FragmentHomeBinding
import com.opsc.opsc7312.databinding.FragmentTransactionDetailsBinding


class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(layoutInflater)

//        binding.backButton.setOnClickListener {
//            activity?.supportFragmentManager?.popBackStack()
//        }

        return binding.root
    }


}