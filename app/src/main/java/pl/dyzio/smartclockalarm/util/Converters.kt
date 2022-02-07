package pl.dyzio.smartclockalarm.util

import androidx.room.TypeConverter
import java.util.*

class Converters {
    @TypeConverter
    fun fromTimeStamp(value: Long?) : Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(dateVal: Date?) : Long? {
        return dateVal?.time?.toLong()
    }
}