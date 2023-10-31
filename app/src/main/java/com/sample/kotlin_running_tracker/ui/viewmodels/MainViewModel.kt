package com.sample.kotlin_running_tracker.ui.viewmodels

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sample.kotlin_running_tracker.data.db.entities.Run
import com.sample.kotlin_running_tracker.data.repository.MainRepository
import com.sample.kotlin_running_tracker.utils.enums.SortType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: MainRepository
) : ViewModel() {
    val runs = MediatorLiveData<List<Run>>()
    val runsCal = repository.getRunListByFilter(SortType.CALORIES_BURNED)
    val runsDate = repository.getRunListByFilter(SortType.DATE)
    val runsTime = repository.getRunListByFilter(SortType.RUNNING_TIME)
    val runsAvg = repository.getRunListByFilter(SortType.AVERAGE_SPEED)
    val runsDist = repository.getRunListByFilter(SortType.DISTANCE)
    var sortType = SortType.DATE

    init {
        runs.addSource(runsCal) { result ->
            if(sortType == SortType.CALORIES_BURNED){
                result?.let { runs.value = it }
            }
        }
        runs.addSource(runsDate) { result ->
            if(sortType == SortType.DATE){
                result?.let { runs.value = it }
            }
        }
        runs.addSource(runsDist) { result ->
            if(sortType == SortType.DISTANCE){
                result?.let { runs.value = it }
            }
        }
        runs.addSource(runsAvg) { result ->
            if(sortType == SortType.AVERAGE_SPEED){
                result?.let { runs.value = it }
            }
        }
        runs.addSource(runsTime) { result ->
            if(sortType == SortType.RUNNING_TIME){
                result?.let { runs.value = it }
            }
        }
    }

    fun sortRuns(type: SortType){
        when(type){
            SortType.CALORIES_BURNED -> runsCal.value?.let { runs.value = it }
            SortType.DISTANCE -> runsDist.value?.let { runs.value = it }
            SortType.AVERAGE_SPEED -> runsAvg.value?.let { runs.value = it }
            SortType.DATE -> runsDate.value?.let { runs.value = it }
            SortType.RUNNING_TIME -> runsTime.value?.let { runs.value = it }
        }.also {
            this.sortType = type
        }

    }

    fun createRun(run: Run) = viewModelScope.launch {
        repository.upsertRun(run)
    }
}