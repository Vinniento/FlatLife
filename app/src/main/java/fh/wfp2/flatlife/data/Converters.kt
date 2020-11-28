package fh.wfp2.flatlife.data

import androidx.room.TypeConverter
import java.util.*

class Converters {
    @TypeConverter
    fun calenderToDatestamp(calender: Calendar): Long = calender.timeInMillis

    @TypeConverter
    fun datestampToCalender(value: Long): Calendar =
        Calendar.getInstance().apply { timeInMillis = value }
}
