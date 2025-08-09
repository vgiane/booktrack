package com.example.booktrack.data.database

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(tableName = "reading_logs",
    foreignKeys = [ForeignKey(entity = Book::class,
        parentColumns = ["id"],
        childColumns = ["bookId"],
        onDelete = ForeignKey.CASCADE)])
data class ReadingLog(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val bookId: Long,
    val date: Long,
    val duration: Int,
    val pagesRead: Int?
)
