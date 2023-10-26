package com.sample.kotlin_running_tracker.ui.fragments

import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.sample.kotlin_running_tracker.R
import com.sample.kotlin_running_tracker.ui.viewmodels.MainViewModel
import com.sample.kotlin_running_tracker.ui.viewmodels.StatisticsViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class StatisticFragment: Fragment(R.layout.fragment_statisctics) {
    private val viewModel: StatisticsViewModel by viewModels()
}