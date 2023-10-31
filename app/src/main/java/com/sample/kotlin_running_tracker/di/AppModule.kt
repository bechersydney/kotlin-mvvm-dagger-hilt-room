package com.sample.kotlin_running_tracker.di

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import androidx.room.Room
import com.sample.kotlin_running_tracker.data.db.RunDao
import com.sample.kotlin_running_tracker.data.db.RunningDatabase
import com.sample.kotlin_running_tracker.utils.Constants.KEY_FIRST_TIME_TOGGLE
import com.sample.kotlin_running_tracker.utils.Constants.KEY_NAME
import com.sample.kotlin_running_tracker.utils.Constants.KEY_WEIGHT
import com.sample.kotlin_running_tracker.utils.Constants.RUNNING_DATABASE
import com.sample.kotlin_running_tracker.utils.Constants.SHARED_PREF_NAME
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
    fun provideContext(@ApplicationContext context: Context): Context = context.applicationContext

    @Provides
    fun provideRunDao(db: RunningDatabase): RunDao = db.getRunDao()

    @Provides
    fun provideSharedPref(app: Context): SharedPreferences = app.getSharedPreferences(
        SHARED_PREF_NAME, MODE_PRIVATE
    )

    @Provides
    fun provideName(sharedPreferences: SharedPreferences) =
        sharedPreferences.getString(KEY_NAME, "") ?: ""

    @Provides
    fun provideWeight(sharedPreferences: SharedPreferences) =
        sharedPreferences.getFloat(KEY_WEIGHT, 80f)

    @Provides
    fun provideFirstTimeToggle(sharedPreferences: SharedPreferences) =
        sharedPreferences.getBoolean(KEY_FIRST_TIME_TOGGLE, true)
}