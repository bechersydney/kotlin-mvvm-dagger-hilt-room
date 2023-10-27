package com.sample.kotlin_running_tracker.ui.fragments

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapsInitializer
import com.google.android.gms.maps.model.PolylineOptions
import com.sample.kotlin_running_tracker.R
import com.sample.kotlin_running_tracker.databinding.FragmentTrackingBinding
import com.sample.kotlin_running_tracker.services.Polyline
import com.sample.kotlin_running_tracker.services.TrackerService
import com.sample.kotlin_running_tracker.services.actions.TrackingServiceActions.*
import com.sample.kotlin_running_tracker.services.repository.TrackingServiceRepository
import com.sample.kotlin_running_tracker.ui.viewmodels.MainViewModel
import com.sample.kotlin_running_tracker.utils.TrackingUtility
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class TrackingFragment : Fragment(R.layout.fragment_tracking) {
    private var menu: Menu? = null
    private var binding: FragmentTrackingBinding? = null
    private val view get() = binding!!

    private val viewModel: MainViewModel by viewModels()
    private var isTracking = false;
    private var pathPoints = mutableListOf<Polyline>()
    private var currentTimeInMillis = 0L


    @Inject
    lateinit var mainServiceRepository: TrackingServiceRepository

    private var map: GoogleMap? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentTrackingBinding.inflate(inflater, container, false)
        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.toolbar_tracking_menu, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.miCancelRun -> {
                        if(currentTimeInMillis > 0L){
                            mainServiceRepository.sendCommandToService(ACTION_STOP_SERVICE.name)
                        } else {
                            Toast.makeText(requireContext(), "No Run to cancel", Toast.LENGTH_SHORT).show()
                        }
                        true
                    }
                    else -> false
                }
            }

        })
        initView(savedInstanceState)
        initEvents()
        subscribeToObserver()
        return view.root
    }

    private fun initView(savedInstanceState: Bundle?) {
        MapsInitializer.initialize(requireContext(), MapsInitializer.Renderer.LATEST) {}
        view.mapView.apply {
            onCreate(savedInstanceState)
            getMapAsync {
                map = it
                addAllPolyline()
            }
        }
    }

    private fun initEvents() {
        view.btnToggleRun.setOnClickListener {
            toggleRun()
        }
        view.btnFinishRun.setOnClickListener {
            mainServiceRepository.sendCommandToService(ACTION_STOP_SERVICE.name)
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

    //
    private fun addLatestPolyline() {
        if (pathPoints.isNotEmpty() && pathPoints.last().size > 1) {
            val preLastLatLng = pathPoints.last()[pathPoints.last().size - 2]
            val lastLng = pathPoints.last().last()
            val polylineOptions = PolylineOptions()
                .color(Color.RED)
                .width(8f)
                .add(preLastLatLng)
                .add(lastLng)

            map?.addPolyline(polylineOptions)
        }
    }

    private fun addAllPolyline() {
        for (polyline in pathPoints) {
            val polylineOptions = PolylineOptions()
                .color(Color.RED)
                .width(8f)
                .addAll(polyline)
            map?.addPolyline(polylineOptions)
        }
    }

    private fun moveCameraToUser() {
        if (pathPoints.isNotEmpty() && pathPoints.last().isNotEmpty()) {
            map?.animateCamera(CameraUpdateFactory.newLatLngZoom(pathPoints.last().last(), 15f))
        }
    }

    private fun updateTracking(isTracking: Boolean) {
        this.isTracking = isTracking

        when {
            !isTracking -> {
                view.btnToggleRun.text = "Start"
                view.btnFinishRun.visibility = View.VISIBLE
            }
            else -> {
                view.btnToggleRun.text = "Stop"
                view.btnFinishRun.visibility = View.GONE
            }
        }
    }

    private fun toggleRun() {
        val actionCommand = if (isTracking) ACTION_PAUSE_SERVICE else ACTION_START_RESUME_SERVICE
        mainServiceRepository.sendCommandToService(actionCommand.name)
    }

    private fun subscribeToObserver() {
        TrackerService.isTracking.observe(viewLifecycleOwner) {
            updateTracking(it)
        }
        TrackerService.pathPoints.observe(viewLifecycleOwner) {
            pathPoints = it
            addLatestPolyline()
            moveCameraToUser()
        }
        TrackerService.timeInMillis.observe(viewLifecycleOwner) {
            currentTimeInMillis = it
            val formattedTime = TrackingUtility.getFormattedStopWatchTime(it, true)
            view.tvTimer.text = formattedTime
        }
    }
}