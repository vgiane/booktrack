package com.example.booktrack.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "books")
data class Book(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val author: String,
    val totalPages: Int?,
    val coverImage: String?,
    val status: String,
    val notes: String?
)
