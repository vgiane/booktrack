package com.example.booktrack.data.dto

import com.example.booktrack.data.entity.Book
import com.example.booktrack.data.entity.BookStatus
import com.example.booktrack.data.entity.ReadingLog
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

data class ExportData(
    @SerializedName("export_date")
    val exportDate: String,
    val books: List<BookExportData>
) {
    companion object {
        fun fromBooks(books: List<Book>, readingLogs: Map<Long, List<ReadingLog>>): ExportData {
            val exportDate = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) + "Z"
            val bookData = books.map { book ->
                val logs = readingLogs[book.id] ?: emptyList()
                BookExportData.fromBook(book, logs)
            }
            
            return ExportData(
                exportDate = exportDate,
                books = bookData
            )
        }
        
        fun toJson(exportData: ExportData): String {
            return Gson().toJson(exportData)
        }
        
        fun fromJson(json: String): ExportData {
            return Gson().fromJson(json, ExportData::class.java)
        }
    }
}

data class BookExportData(
    val title: String,
    val author: String,
    val status: String,
    @SerializedName("totalPages")
    val totalPages: Int? = null,
    val notes: String? = null,
    @SerializedName("reading_logs")
    val readingLogs: List<ReadingLogExportData>
) {
    companion object {
        fun fromBook(book: Book, readingLogs: List<ReadingLog>): BookExportData {
            return BookExportData(
                title = book.title,
                author = book.author,
                status = book.status.name.lowercase().replaceFirstChar { it.uppercase() },
                totalPages = book.totalPages,
                notes = book.notes,
                readingLogs = readingLogs.map { ReadingLogExportData.fromReadingLog(it) }
            )
        }
        
        fun toBook(bookData: BookExportData): Book {
            val status = when (bookData.status.lowercase()) {
                "active" -> BookStatus.ACTIVE
                "read" -> BookStatus.READ
                "paused" -> BookStatus.PAUSED
                "abandoned" -> BookStatus.ABANDONED
                else -> BookStatus.ACTIVE
            }
            
            return Book(
                title = bookData.title,
                author = bookData.author,
                status = status,
                totalPages = bookData.totalPages,
                notes = bookData.notes
            )
        }
    }
}

data class ReadingLogExportData(
    @SerializedName("start_time")
    val startTime: String,
    @SerializedName("end_time")
    val endTime: String,
    val time: Long, // Duration in seconds
    @SerializedName("pages_read")
    val pagesRead: Int? = null
) {
    companion object {
        fun fromReadingLog(readingLog: ReadingLog): ReadingLogExportData {
            return ReadingLogExportData(
                startTime = readingLog.startTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) + "Z",
                endTime = readingLog.endTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) + "Z",
                time = readingLog.elapsedTimeSeconds,
                pagesRead = readingLog.pagesRead
            )
        }
        
        fun toReadingLog(logData: ReadingLogExportData, bookId: Long): ReadingLog {
            val startTime = LocalDateTime.parse(logData.startTime.removeSuffix("Z"))
            val endTime = LocalDateTime.parse(logData.endTime.removeSuffix("Z"))
            
            return ReadingLog(
                bookId = bookId,
                startTime = startTime,
                endTime = endTime,
                elapsedTimeSeconds = logData.time,
                pagesRead = logData.pagesRead
            )
        }
    }
}
