package uk.ac.shef.oak.com4510.model

import androidx.room.ProvidedTypeConverter
import androidx.room.TypeConverter
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

class Converters {
    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }

    @TypeConverter
    fun dateToTimestamp(value: Long?): Date? {
        return value?.let {
            Date(it)
        }
    }

}