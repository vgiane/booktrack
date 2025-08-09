package com.example.booktrack.data.database

import androidx.room.TypeConverter

class Converters {
    @TypeConverter
    fun fromBookStatus(status: BookStatus): String {
        return status.name
    }

    @TypeConverter
    fun toBookStatus(status: String): BookStatus {
        return BookStatus.valueOf(status)
    }
}
