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
import com.sample.kotlin_running_tracker.utils.Constants.NOTIFICATION_CHANNEL_ID
import com.sample.kotlin_running_tracker.utils.Constants.NOTIFICATION_ID
import com.sample.kotlin_running_tracker.utils.Constants.NOTIFICATION_NAME
import com.sample.kotlin_running_tracker.utils.TrackingUtility
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

// use aliases for complex objects like 2D or 3D list
typealias Polyline = MutableList<LatLng>
typealias Polylines = MutableList<Polyline>

@AndroidEntryPoint
class TrackerService : LifecycleService() {

    @Inject
    lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    @Inject
    lateinit var baseNotificationBuilder: NotificationCompat.Builder


    lateinit var currentNotificationBuilder: NotificationCompat.Builder

    private var isServiceRunning = false


    private var timeInSeconds = MutableLiveData<Long>()

    companion object {
        val isTracking = MutableLiveData<Boolean>()
        val pathPoints = MutableLiveData<Polylines>()
        val timeInMillis = MutableLiveData<Long>()
    }

    override fun onCreate() {
        super.onCreate()
        currentNotificationBuilder = baseNotificationBuilder
        postInitialValues()
        isTracking.observe(this) {
            updateLocationTracking(it)
            updateNotificationTrackingState(it)
        }
    }

    private fun postInitialValues() {
        isTracking.postValue(false)
        pathPoints.postValue(mutableListOf())
        timeInSeconds.postValue(0L)
        timeInMillis.postValue(0L)
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
        } else {
            startTimer()
        }
    }

    private fun pauseCommand() {
        isTracking.postValue(false)
        isTimerEnabled = false
    }

    private fun stopCommand() {
        if (isServiceRunning) {
            isServiceRunning = false
            stopSelf()
        }
    }

    private fun startForegroundService() {
        startTimer()
        isTracking.postValue(true)
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        createNotification(notificationManager)
        startForeground(NOTIFICATION_ID, baseNotificationBuilder.build())

        timeInSeconds.observe(this) {
            val notification = currentNotificationBuilder.setContentText(
                TrackingUtility.getFormattedStopWatchTime(it * 1000L)
            )
            notificationManager.notify(NOTIFICATION_ID, notification.build())
        }
    }


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

    private var isTimerEnabled = false
    private var lapTime = 0L
    private var timeRun = 0L
    private var timeStarted = 0L
    private var lastSecondTimeStamp = 0L

    private fun startTimer() {
        addEmptyPolyline()
        isTracking.postValue(true)
        timeStarted = System.currentTimeMillis()
        isTimerEnabled = true

        CoroutineScope(Dispatchers.Main).launch {
            while (isTracking.value!!) {
                lapTime = System.currentTimeMillis() - timeStarted
                timeInMillis.postValue(timeRun + lapTime)

                if (timeInMillis.value!! > lastSecondTimeStamp + 1000) {
                    timeInSeconds.postValue(timeInSeconds.value!! + 1)
                    lastSecondTimeStamp += 1000L
                }

                delay(50L)
            }
            timeRun += lapTime

        }
    }

    private fun updateNotificationTrackingState(isTracking: Boolean) {
        val notificationText = if (isTracking) "Pause" else "Resume"
        val pendingIntent = if (isTracking) {
            val pauseIntent = Intent(this, TrackerService::class.java).apply {
                action = ACTION_PAUSE_SERVICE.name

            }
            PendingIntent.getService(this, 1, pauseIntent, FLAG_IMMUTABLE)
        } else {
            val resumeIntent = Intent(this, TrackerService::class.java).apply {
                action = ACTION_START_RESUME_SERVICE.name
            }
            PendingIntent.getService(this, 2, resumeIntent, FLAG_IMMUTABLE)
        }

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // remove all action from notification
        currentNotificationBuilder.javaClass.getDeclaredField("mActions").apply {
            isAccessible = true
            set(currentNotificationBuilder, ArrayList<NotificationCompat.Action>())
        }

        currentNotificationBuilder =
            baseNotificationBuilder.addAction(
                R.drawable.ic_pause_black_24dp,
                notificationText,
                pendingIntent
            )
        notificationManager.notify(NOTIFICATION_ID, currentNotificationBuilder.build())
    }
}
