package com.sample.kotlin_running_tracker.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.sample.kotlin_running_tracker.R
import com.sample.kotlin_running_tracker.data.db.RunDao
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var dao: RunDao
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
}