package com.sample.kotlin_running_tracker.data.repository

import com.sample.kotlin_running_tracker.data.db.RunDao
import com.sample.kotlin_running_tracker.data.db.entities.Run
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MainRepository @Inject constructor(
    private val runDao: RunDao
) {
    suspend fun upsertRun(run: Run) = runDao.upsert(run)

    suspend fun deleteRun(run: Run) = runDao.delete(run)

    fun getRunListByFilter(filter: Int = 0) {
        when (filter) {
            0 -> runDao.getRunsByDate()
            1 -> runDao.getRunsByDistanceInMeters()
            2 -> runDao.getRunsByTimeInMillis()
            3 -> runDao.getRunsByCaloriesBurned()
            4 -> runDao.getRunsByAvgSpeed()
        }
    }

    fun getTotalRunTimeInMillis() = runDao.getTotalTimeInMillis()
    fun getTotalRunDistanceInMeter() = runDao.getTotalDistanceInMeter()
    fun getTotalRunCaloriesBurned() = runDao.getTotalCaloriesBurned()
    fun getTotalRunAvgSpeed() = runDao.getRunsByAvgSpeed()


}