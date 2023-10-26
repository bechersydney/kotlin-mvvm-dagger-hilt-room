package com.sample.kotlin_running_tracker.ui.fragments

import android.Manifest
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.sample.kotlin_running_tracker.R
import com.sample.kotlin_running_tracker.databinding.FragmentRunBinding
import com.sample.kotlin_running_tracker.ui.viewmodels.MainViewModel
import com.sample.kotlin_running_tracker.utils.Constants
import com.sample.kotlin_running_tracker.utils.TrackingUtility
import dagger.hilt.android.AndroidEntryPoint
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions

@AndroidEntryPoint
class RunFragment: Fragment(R.layout.fragment_run), EasyPermissions.PermissionCallbacks {
        private val viewModel: MainViewModel by viewModels()
        private var binding: FragmentRunBinding? = null
        private val view get() = binding!!
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRunBinding.inflate(inflater, container, false)
        requestPermission()
        view.fab.setOnClickListener {
            findNavController().navigate(R.id.action_runFragment_to_trackingFragment)
        }

        return view.root
    }

    private fun requestPermission(){
        if(TrackingUtility.hasLocationPermission(requireContext())){
            return
        }
        val locationPerms = arrayOf(
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
        )
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.Q ){
            locationPerms.plus(Manifest.permission.ACCESS_BACKGROUND_LOCATION,)
        }
        EasyPermissions.requestPermissions(
            this,
            "You need to accept location permission to use this app",
            Constants.REQUEST_CODE_LOCATION_PERMISSION,
            *locationPerms


        )

    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
    }


    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        if(EasyPermissions.somePermissionPermanentlyDenied(this, perms)){
            AppSettingsDialog.Builder(this).build().show()
        } else {
            requestPermission()
        }
    }
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }
}