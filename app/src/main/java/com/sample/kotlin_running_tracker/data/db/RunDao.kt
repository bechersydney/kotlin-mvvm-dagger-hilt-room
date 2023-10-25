package com.sample.kotlin_running_tracker.data.db

import androidx.lifecycle.LiveData
import androidx.room.*
import com.sample.kotlin_running_tracker.data.db.entities.Run

@Dao
interface RunDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(run: Run)

    @Delete
    suspend fun delete(run: Run)

    @Query(value = "SELECT * FROM running_table ORDER BY timestamp DESC")
    fun getRunsByDate(): LiveData<List<Run>>

    @Query(value = "SELECT * FROM running_table ORDER BY timeInMillis DESC")
    fun getRunsByTimeInMillis(): LiveData<List<Run>>

    @Query(value = "SELECT * FROM running_table ORDER BY caloriesBurned DESC")
    fun getRunsByCaloriesBurned(): LiveData<List<Run>>

    @Query(value = "SELECT * FROM running_table ORDER BY avgSpeedInKWM DESC")
    fun getRunsByAvgSpeed(): LiveData<List<Run>>

    @Query(value = "SELECT * FROM running_table ORDER BY distanceInMeter DESC")
    fun getRunsByDistanceInMeters(): LiveData<List<Run>>

    // for statistics
    @Query(value = "SELECT SUM(timeInMillis) FROM running_table")
    fun getTotalTimeInMillis(): LiveData<Long>

    @Query(value = "SELECT SUM(distanceInMeter) FROM running_table")
    fun getTotalDistanceInMeter(): LiveData<Int>

    @Query(value = "SELECT SUM(caloriesBurned) FROM running_table")
    fun getTotalCaloriesBurned(): LiveData<Int>

    @Query(value = "SELECT AVG(avgSpeedInKWM) FROM running_table")
    fun getTotalAverageSpeed(): LiveData<Float>
}