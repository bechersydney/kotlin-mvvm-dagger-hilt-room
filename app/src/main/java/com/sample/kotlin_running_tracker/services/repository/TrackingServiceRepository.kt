package com.sample.kotlin_running_tracker.services.repository

import android.content.Context
import android.content.Intent
import android.os.Build
import com.sample.kotlin_running_tracker.services.TrackerService
import javax.inject.Inject
import com.sample.kotlin_running_tracker.services.actions.TrackingServiceActions.*
import javax.inject.Singleton

@Singleton
class TrackingServiceRepository @Inject constructor(
    val context: Context
) {
    fun sendCommandToService(action: String){
        Intent(context, TrackerService::class.java).also {
            it.action = action
            startServiceIntent(it)
        }
    }

    private fun startServiceIntent(intent: Intent){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            context.startForegroundService(intent)
        } else {
            context.startService(intent)
        }
    }

}