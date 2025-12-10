package bob.colbaskin.it_tech2025.verification_log.data.encrypted_db

import androidx.room.TypeConverter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

class DateConverters {

    @TypeConverter
    fun stringToDate(value: String?): Date? {
        return value?.let {
            try {
                val formatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US)
                formatter.timeZone = TimeZone.getTimeZone("UTC")
                formatter.parse(it)
            } catch (e: Exception) {
                null
            }
        }
    }

    @TypeConverter
    fun dateToString(date: Date?): String? {
        return date?.let {
            val formatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US)
            formatter.timeZone = TimeZone.getTimeZone("UTC")
            formatter.format(it)
        }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }

    @TypeConverter
    fun timestampToDate(value: Long?): Date? {
        return value?.let { Date(it) }
    }
}
