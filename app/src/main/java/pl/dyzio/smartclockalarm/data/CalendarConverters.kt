package pl.dyzio.smartclockalarm.data

import androidx.room.TypeConverter
import java.util.*

class CalendarConverters {
        @TypeConverter
        fun calendarToDatestamp(calendar: Calendar): Long = calendar.timeInMillis

        @TypeConverter
        fun datestampToCalendar(value: Long): Calendar =
            Calendar.getInstance().apply { timeInMillis = value }
}