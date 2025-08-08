package com.example.booktrack.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "books")
data class Book(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val author: String,
    val totalPages: Int? = null,
    val status: BookStatus = BookStatus.ACTIVE,
    val notes: String? = null,
    val coverImagePath: String? = null // For future use if custom images are added
)

enum class BookStatus {
    ACTIVE,
    READ,
    PAUSED,
    ABANDONED
}
