package com.sample.kotlin_running_tracker.services

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_IMMUTABLE
import android.content.Context
import android.content.Intent
import android.location.Location
import android.os.Build
import android.os.Looper
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.location.*
import com.google.android.gms.location.Priority.PRIORITY_HIGH_ACCURACY
import com.google.android.gms.maps.model.LatLng
import com.sample.kotlin_running_tracker.R
import com.sample.kotlin_running_tracker.services.actions.TrackingServiceActions.*
import com.sample.kotlin_running_tracker.ui.MainActivity
import com.sample.kotlin_running_tracker.utils.Constants.ACTION_SHOW_TRACKING_FRAGMENT
import com.sample.kotlin_running_tracker.utils.Constants.NOTIFICATION_CHANNEL_ID
import com.sample.kotlin_running_tracker.utils.Constants.NOTIFICATION_ID
import com.sample.kotlin_running_tracker.utils.Constants.NOTIFICATION_NAME
import com.sample.kotlin_running_tracker.utils.TrackingUtility
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

// use aliases for complex objects like 2D or 3D list
typealias Polyline = MutableList<LatLng>
typealias Polylines = MutableList<Polyline>

@AndroidEntryPoint
class TrackerService : LifecycleService() {

    private var isServiceRunning = false
    lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    companion object {
        val isTracking = MutableLiveData<Boolean>()
        val pathPoints = MutableLiveData<Polylines>()
    }

    override fun onCreate() {
        super.onCreate()
        postInitialValues()
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        isTracking.observe(this) {
            updateLocationTracking(it)
        }
    }

    private fun postInitialValues() {
        isTracking.postValue(false)
        pathPoints.postValue(mutableListOf())
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.let { incomingIntent ->
            when (incomingIntent.action) {
                ACTION_START_RESUME_SERVICE.name -> startOrResumeCommand()
                ACTION_STOP_SERVICE.name -> stopCommand()
                ACTION_PAUSE_SERVICE.name -> pauseCommand()
                else -> Unit
            }

        }
        return super.onStartCommand(intent, flags, startId)
    }

    private fun startOrResumeCommand() {
        if (!isServiceRunning) {
            isServiceRunning = true
            startForegroundService()
        }
    }

    private fun pauseCommand() {
        Timber.d("pause service")
    }

    private fun stopCommand() {
        Timber.d("stop service")
    }

    private fun startForegroundService() {
        addEmptyPolyline()
        isTracking.postValue(true)
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        createNotification(notificationManager)

        val notificationBuilder = NotificationCompat.Builder(
            this,
            NOTIFICATION_CHANNEL_ID
        ).setAutoCancel(false)
            .setOngoing(true)
            .setSmallIcon(R.drawable.ic_direction_run_black)
            .setContentTitle("Running App")
            .setContentText("00.00.00")
            .setContentIntent(getMainActivityPendingIntent())
        startForeground(NOTIFICATION_ID, notificationBuilder.build())
    }

    @SuppressLint("UnspecifiedImmutableFlag")
    private fun getMainActivityPendingIntent() = PendingIntent.getActivity(
        this,
        0,
        Intent(
            this,
            MainActivity::class.java
        ).also {
            it.action = ACTION_SHOW_TRACKING_FRAGMENT
        },
        FLAG_IMMUTABLE
    )


    private fun createNotification(notificationManager: NotificationManager) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID, NOTIFICATION_NAME, NotificationManager.IMPORTANCE_LOW
            )
            notificationManager.createNotificationChannel(notificationChannel)
        }
    }

    private fun addEmptyPolyline() = pathPoints.value?.apply {
        add(mutableListOf())
        pathPoints.postValue(this)
    } ?: pathPoints.postValue(mutableListOf(mutableListOf()))

    private fun addPathPoint(location: Location?) {
        location?.let {
            val position = LatLng(location.latitude, location.longitude)
            pathPoints.value?.apply {
                last().add(position)
                pathPoints.postValue(this)
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun updateLocationTracking(isTracking: Boolean) {
        if (isTracking && TrackingUtility.hasLocationPermission(this)) {
            val request = LocationRequest.Builder(PRIORITY_HIGH_ACCURACY, 5000L).apply {
                setWaitForAccurateLocation(false)
                setMinUpdateIntervalMillis(5000L)
                setMaxUpdateDelayMillis(2000L)
            }
            fusedLocationProviderClient.requestLocationUpdates(
                request.build(),
                locationCallback,
                Looper.getMainLooper()
            )
        } else {
            fusedLocationProviderClient.removeLocationUpdates(locationCallback)
        }
    }

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(result: LocationResult) {
            super.onLocationResult(result)
            if (isTracking.value!!) {
                result.locations.let { locations ->
                    for (location in locations) {
                        addPathPoint(location)
                        Timber.d("Your location ${location.latitude}  ${location.longitude}")
                    }
                }
            }
        }
    }
}
