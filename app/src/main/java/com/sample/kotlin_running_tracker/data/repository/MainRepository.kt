package com.sample.kotlin_running_tracker.data.repository

import androidx.lifecycle.LiveData
import com.sample.kotlin_running_tracker.data.db.RunDao
import com.sample.kotlin_running_tracker.data.db.entities.Run
import com.sample.kotlin_running_tracker.utils.enums.SortType
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MainRepository @Inject constructor(
    private val runDao: RunDao
) {
    suspend fun upsertRun(run: Run) = runDao.upsert(run)

    suspend fun deleteRun(run: Run) = runDao.delete(run)

    fun getRunListByFilter(filter: SortType = SortType.DATE): LiveData<List<Run>> {
        return when (filter) {
            SortType.DATE -> runDao.getRunsByDate()
            SortType.DISTANCE -> runDao.getRunsByDistanceInMeters()
            SortType.RUNNING_TIME -> runDao.getRunsByTimeInMillis()
            SortType.CALORIES_BURNED -> runDao.getRunsByCaloriesBurned()
            SortType.AVERAGE_SPEED -> runDao.getRunsByAvgSpeed()
            else -> throw IllegalArgumentException("Invalid filter  value")
        }
    }

    fun getTotalRunTimeInMillis() = runDao.getTotalTimeInMillis()
    fun getTotalRunDistanceInMeter() = runDao.getTotalDistanceInMeter()
    fun getTotalRunCaloriesBurned() = runDao.getTotalCaloriesBurned()
    fun getTotalRunAvgSpeed() = runDao.getRunsByAvgSpeed()


}