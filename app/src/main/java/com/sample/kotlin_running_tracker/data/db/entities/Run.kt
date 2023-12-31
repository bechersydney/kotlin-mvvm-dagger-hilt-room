package com.sample.kotlin_running_tracker.data.db.entities

import android.graphics.Bitmap
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "running_table")
data class Run(
    var image: Bitmap? = null,
    var distanceInMeter: Int = 0,
    var timestamp: Long = 0L,
    var avgSpeedInKWM: Float = 0f,
    var timeInMillis: Long = 0L,
    var caloriesBurned: Int = 0
) {
    @PrimaryKey(autoGenerate = true)
    var id: Int? = null
}