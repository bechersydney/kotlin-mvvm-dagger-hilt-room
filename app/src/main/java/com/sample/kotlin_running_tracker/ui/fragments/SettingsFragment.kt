package com.sample.kotlin_running_tracker.ui.fragments

import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import com.sample.kotlin_running_tracker.R
import com.sample.kotlin_running_tracker.databinding.FragmentSettingsBinding
import com.sample.kotlin_running_tracker.ui.MainActivity
import com.sample.kotlin_running_tracker.utils.Constants.KEY_NAME
import com.sample.kotlin_running_tracker.utils.Constants.KEY_WEIGHT
import javax.inject.Inject

class SettingsFragment : Fragment(R.layout.fragment_settings) {
    private var binding: FragmentSettingsBinding? = null
    private val view get() = binding!!

    @Inject
    lateinit var sharedPreferences: SharedPreferences

    @set:Inject
    var username = ""

    @set:Inject
    var weight = 0f

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSettingsBinding.inflate(inflater, container, false)
        initEvents()
        loadAllUserData()
        return view.root
    }

    private fun initEvents() {
        view.btnApplyChanges.setOnClickListener {
            val success = applyChangeToSharedPref()
            val message =
                if (success) "Changes saved successfully" else "Update user details failed!"
            Snackbar.make(requireView(), message, Snackbar.LENGTH_SHORT).show()

        }
    }

    private fun loadAllUserData() {
        view.etName.setText(username)
        view.etWeight.setText(weight.toString())
    }

    private fun applyChangeToSharedPref(): Boolean {
        val nameText = view.etName.toString()
        val weight = view.etWeight.toString()
        if (nameText.isEmpty() || weight.isEmpty()) {
            return false
        }
        sharedPreferences.edit().apply {
            putString(KEY_NAME, nameText)
            putFloat(KEY_WEIGHT, weight.toFloat())
            apply()
        }.also {
            val toolbarText = "Lets go $nameText"
            (requireActivity() as MainActivity).binding.tvToolbarTitle.text = toolbarText
            return true
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}