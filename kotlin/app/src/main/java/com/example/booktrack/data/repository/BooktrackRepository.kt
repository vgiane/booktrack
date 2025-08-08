package com.example.booktrack.data.repository

import com.example.booktrack.data.dao.BookDao
import com.example.booktrack.data.dao.ReadingLogDao
import com.example.booktrack.data.entity.Book
import com.example.booktrack.data.entity.BookStatus
import com.example.booktrack.data.entity.ReadingLog
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime

class BooktrackRepository(
    private val bookDao: BookDao,
    private val readingLogDao: ReadingLogDao
) {
    
    // Book operations
    fun getAllBooks(): Flow<List<Book>> = bookDao.getAllBooks()
    
    fun getBooksByStatus(status: BookStatus): Flow<List<Book>> = bookDao.getBooksByStatus(status)
    
    suspend fun getBookById(id: Long): Book? = bookDao.getBookById(id)
    
    suspend fun insertBook(book: Book): Long = bookDao.insertBook(book)
    
    suspend fun updateBook(book: Book) = bookDao.updateBook(book)
    
    suspend fun deleteBook(book: Book) = bookDao.deleteBook(book)
    
    suspend fun deleteBookById(bookId: Long) = bookDao.deleteBookById(bookId)
    
    suspend fun deleteAllBooks() = bookDao.deleteAllBooks()
    
    // Reading log operations
    fun getReadingLogsByBook(bookId: Long): Flow<List<ReadingLog>> = 
        readingLogDao.getReadingLogsByBook(bookId)
    
    fun getAllReadingLogs(): Flow<List<ReadingLog>> = readingLogDao.getAllReadingLogs()
    
    fun getReadingLogsBetweenDates(startDate: LocalDateTime, endDate: LocalDateTime): Flow<List<ReadingLog>> =
        readingLogDao.getReadingLogsBetweenDates(startDate, endDate)
    
    suspend fun getTotalReadingTime(): Long = readingLogDao.getTotalReadingTime() ?: 0L
    
    suspend fun getTotalReadingTimeBetweenDates(startDate: LocalDateTime, endDate: LocalDateTime): Long =
        readingLogDao.getTotalReadingTimeBetweenDates(startDate, endDate) ?: 0L
    
    suspend fun insertReadingLog(readingLog: ReadingLog): Long = readingLogDao.insertReadingLog(readingLog)
    
    suspend fun updateReadingLog(readingLog: ReadingLog) = readingLogDao.updateReadingLog(readingLog)
    
    suspend fun deleteReadingLog(readingLog: ReadingLog) = readingLogDao.deleteReadingLog(readingLog)
    
    suspend fun deleteAllReadingLogs() = readingLogDao.deleteAllReadingLogs()
    
    // Combined operations
    suspend fun deleteAllData() {
        deleteAllReadingLogs()
        deleteAllBooks()
    }
}
