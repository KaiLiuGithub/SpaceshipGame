package com.kailiu.spaceship.database

import androidx.room.TypeConverter
import java.util.*

class DateTypeConverter {
    @TypeConverter
    fun toDate(timestamp: Long?): Date? {
        timestamp?.let {
            return Date(timestamp)
        }
        return null
    }

    @TypeConverter
    fun fromDate(date: Date?): Long? {
        date?.let {
            return date.time
        }
        return null
    }
}