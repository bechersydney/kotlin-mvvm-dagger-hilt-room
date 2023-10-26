package com.sample.kotlin_running_tracker.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.sample.kotlin_running_tracker.R
import com.sample.kotlin_running_tracker.databinding.FragmentSettingsBinding
import com.sample.kotlin_running_tracker.databinding.FragmentSetupBinding

class SetupFragment: Fragment(R.layout.fragment_setup) {
    private var binding: FragmentSetupBinding ? = null
    private val view get() = binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSetupBinding.inflate(inflater, container, false)
        view.tvContinue.setOnClickListener{
            findNavController().navigate(R.id.action_setupFragment_to_runFragment)
        }
        return view.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}