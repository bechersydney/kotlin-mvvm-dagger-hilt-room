package com.sample.kotlin_running_tracker.ui.viewmodels

import androidx.lifecycle.ViewModel
import com.sample.kotlin_running_tracker.data.repository.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class StatisticsViewModel @Inject constructor(
    val repository: MainRepository
) : ViewModel()  {
}