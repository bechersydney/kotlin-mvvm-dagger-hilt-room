package com.sample.kotlin_running_tracker.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sample.kotlin_running_tracker.data.db.entities.Run
import com.sample.kotlin_running_tracker.data.repository.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: MainRepository
): ViewModel() {
    fun createRun(run: Run) = viewModelScope.launch {
        repository.upsertRun(run)
    }
}