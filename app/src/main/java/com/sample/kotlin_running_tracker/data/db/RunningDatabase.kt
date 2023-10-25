package com.sample.kotlin_running_tracker.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.sample.kotlin_running_tracker.data.db.entities.Run

@Database(
    entities = [Run::class],
    version = 1
)
@TypeConverters(Converters::class)
abstract class RunningDatabase : RoomDatabase() {
    abstract fun getRunDao(): RunDao
//    companion object {
//        @Volatile
//        private var instance: RunningDatabase? = null
//        private val LOCK = Any()
//        operator fun invoke(context: Context) = instance ?: synchronized(LOCK) {
//            instance ?: createDatabase(context).also { instance = it }
//        }
//
//        private fun createDatabase(context: Context): RunningDatabase =
//            Room.databaseBuilder(
//                context.applicationContext,
//                RunningDatabase::class.java,
//                "ShoppingDB.db"
//            ).build()
//
//    }
}