package com.example.booktrack.data.converters

import androidx.room.TypeConverter
import com.example.booktrack.data.entity.BookStatus
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class Converters {
    
    @TypeConverter
    fun fromLocalDateTime(dateTime: LocalDateTime?): String? {
        return dateTime?.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
    }
    
    @TypeConverter
    fun toLocalDateTime(dateTimeString: String?): LocalDateTime? {
        return dateTimeString?.let {
            LocalDateTime.parse(it, DateTimeFormatter.ISO_LOCAL_DATE_TIME)
        }
    }
    
    @TypeConverter
    fun fromBookStatus(status: BookStatus): String {
        return status.name
    }
    
    @TypeConverter
    fun toBookStatus(statusString: String): BookStatus {
        return BookStatus.valueOf(statusString)
    }
}
