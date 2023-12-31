package com.sample.kotlin_running_tracker.di

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.sample.kotlin_running_tracker.R
import com.sample.kotlin_running_tracker.ui.MainActivity
import com.sample.kotlin_running_tracker.utils.Constants
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ServiceComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ServiceScoped

@Module
@InstallIn(ServiceComponent::class)
object ServiceModule {

    @ServiceScoped
    @Provides
    fun provideFusedLocationProviderClient(
        @ApplicationContext app: Context
    ): FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(app.applicationContext)


    @ServiceScoped
    @Provides
    fun providePendingIntent(@ApplicationContext app: Context): PendingIntent =
        PendingIntent.getActivity(
            app,
            0,
            Intent(
                app,
                MainActivity::class.java
            ).also {
                it.action = Constants.ACTION_SHOW_TRACKING_FRAGMENT
            },
            PendingIntent.FLAG_IMMUTABLE
        )

    @ServiceScoped
    @Provides
    fun provideBaseNotificationBuilder(
        @ApplicationContext app: Context,
        pendingIntent: PendingIntent
    ) =
        NotificationCompat.Builder(
            app,
            Constants.NOTIFICATION_CHANNEL_ID
        ).setAutoCancel(false)
            .setOngoing(true)
            .setSmallIcon(R.drawable.ic_direction_run_black)
            .setContentTitle("Running App")
            .setContentText("00.00.00")
            .setContentIntent(pendingIntent)
}