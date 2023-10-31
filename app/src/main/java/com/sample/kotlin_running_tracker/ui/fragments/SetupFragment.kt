package com.sample.kotlin_running_tracker.ui.fragments

import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.sample.kotlin_running_tracker.R
import com.sample.kotlin_running_tracker.databinding.FragmentSetupBinding
import com.sample.kotlin_running_tracker.ui.MainActivity
import com.sample.kotlin_running_tracker.utils.Constants.KEY_FIRST_TIME_TOGGLE
import com.sample.kotlin_running_tracker.utils.Constants.KEY_NAME
import com.sample.kotlin_running_tracker.utils.Constants.KEY_WEIGHT
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SetupFragment : Fragment(R.layout.fragment_setup) {
    private var binding: FragmentSetupBinding? = null
    private val view get() = binding!!

    @Inject
    lateinit var sharedPreferences: SharedPreferences

    // inject first time toggle here
    // lateinit and direct @Inject is not working with primitive type
    @set:Inject
    var firstTimeToggle = true
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSetupBinding.inflate(inflater, container, false)

        // check if not first time using app
        if (!firstTimeToggle) {
            // remove the set up from stack so that when user click back set up fragment wont show
            val navOption = NavOptions.Builder()
                .setPopUpTo(
                    R.id.setupFragment,
                    true
                ) // pop is used to remove fragment and true: also remove latest added fragment id
                .build()
            findNavController().navigate(
                R.id.action_setupFragment_to_runFragment,
                savedInstanceState,
                navOption
            )
        }


        view.tvContinue.setOnClickListener {
            val success = writeUserData()
            if (!success) {
                Snackbar.make(
                    requireView(),
                    "Invalid credentials",
                    Snackbar.LENGTH_SHORT
                ).show()
            } else {
                findNavController().navigate(R.id.action_setupFragment_to_runFragment)
            }

        }

        return view.root
    }

    private fun writeUserData(): Boolean {
        val name = view.etName.text.toString()
        val weight = view.etWeight.text.toString()

        if (name.isEmpty() || weight.isEmpty()) {
            return false
        }
        sharedPreferences.edit().apply {
            putFloat(KEY_WEIGHT, weight.toFloat())
            putString(KEY_NAME, name)
            putBoolean(KEY_FIRST_TIME_TOGGLE, false)
            apply()
        }.also {
            val toolbarText = "Lets go $name"
            (requireActivity() as MainActivity).binding.tvToolbarTitle.text = toolbarText
        }
        return true

    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}