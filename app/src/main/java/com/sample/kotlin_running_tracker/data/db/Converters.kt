package com.sample.kotlin_running_tracker.data.db

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.sample.kotlin_running_tracker.data.db.entities.Run
import java.io.ByteArrayOutputStream

class Converters {
    @TypeConverter
    fun fromBitmap(bitmap: Bitmap): ByteArray {
        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
        return outputStream.toByteArray()
    }
    @TypeConverter
    fun toBitMap(rawBytes: ByteArray): Bitmap {
        return BitmapFactory.decodeByteArray(rawBytes, 0, rawBytes.size)
    }
}