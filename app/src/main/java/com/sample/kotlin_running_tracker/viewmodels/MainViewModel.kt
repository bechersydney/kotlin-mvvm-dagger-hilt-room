package com.sample.kotlin_running_tracker.viewmodels

import androidx.lifecycle.ViewModel
import com.sample.kotlin_running_tracker.data.repository.MainRepository
import javax.inject.Inject

class MainViewModel @Inject constructor(
    private val repository: MainRepository
): ViewModel() {

}