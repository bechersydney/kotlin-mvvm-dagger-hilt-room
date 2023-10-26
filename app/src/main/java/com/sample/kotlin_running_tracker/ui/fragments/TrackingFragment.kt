package com.sample.kotlin_running_tracker.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapsInitializer
import com.sample.kotlin_running_tracker.R
import com.sample.kotlin_running_tracker.databinding.FragmentTrackingBinding
import com.sample.kotlin_running_tracker.services.repository.TrackingServiceRepository
import com.sample.kotlin_running_tracker.ui.viewmodels.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class TrackingFragment: Fragment(R.layout.fragment_tracking) {
    private val viewModel: MainViewModel by viewModels()
    private var binding: FragmentTrackingBinding? = null
    private val view get() = binding!!

    @Inject
    lateinit var mainServiceRepository: TrackingServiceRepository

    private var map: GoogleMap? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentTrackingBinding.inflate(inflater, container, false)
        initView(savedInstanceState)
        initEvents()
        return view.root
    }

    private fun initView(savedInstanceState: Bundle?){
        MapsInitializer.initialize(requireContext(), MapsInitializer.Renderer.LATEST){}
        view.mapView.apply {
            onCreate(savedInstanceState)
            getMapAsync{
                map = it
            }
        }
    }
    private fun initEvents(){
        view.btnToggleRun.setOnClickListener {
            mainServiceRepository.startService()
        }
    }

    override fun onStart() {
        super.onStart()
        view.mapView.onStart()
    }

    override fun onResume() {
        super.onResume()
        view.mapView.onResume()
    }

    override fun onStop() {
        super.onStop()
        view.mapView.onStop()
    }

    override fun onPause() {
        super.onPause()
        view.mapView.onPause()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        view.mapView.onLowMemory()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        view.mapView.onSaveInstanceState(outState)
    }
}