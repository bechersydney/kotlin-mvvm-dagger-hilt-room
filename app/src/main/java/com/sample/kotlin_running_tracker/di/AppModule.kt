package com.sample.kotlin_running_tracker.di

import android.content.Context
import androidx.room.Room
import com.sample.kotlin_running_tracker.data.db.RunDao
import com.sample.kotlin_running_tracker.data.db.RunningDatabase
import com.sample.kotlin_running_tracker.utils.Constants.RUNNING_DATABASE
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    fun providesRunDatabase(context: Context) =
        Room.databaseBuilder(context, RunningDatabase::class.java, RUNNING_DATABASE).build()

    @Provides
    fun provideContext(@ApplicationContext context: Context):Context = context.applicationContext

    @Provides
    fun provideRunDao(db: RunningDatabase): RunDao = db.getRunDao()
}