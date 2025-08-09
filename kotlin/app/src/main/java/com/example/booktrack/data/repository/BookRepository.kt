package com.example.booktrack.data.repository

import com.example.booktrack.data.database.Book
import com.example.booktrack.data.database.BookDao
import com.example.booktrack.data.database.BookStatus
import com.example.booktrack.data.database.ReadingLog
import com.example.booktrack.data.database.ReadingLogDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class BookRepository(
    private val bookDao: BookDao,
    private val readingLogDao: ReadingLogDao
) {
    fun getAllBooks(): Flow<List<Book>> = bookDao.getAllBooks()
    
    fun getActiveBooks(): Flow<List<Book>> = 
        bookDao.getAllBooks().map { books -> 
            books.filter { it.status == BookStatus.ACTIVE }
        }
    
    fun getBookById(bookId: Long): Flow<Book> = bookDao.getBookById(bookId)
    
    suspend fun insertBook(book: Book): Long {
        return bookDao.insertBook(book)
    }
    
    suspend fun updateBook(book: Book) {
        bookDao.updateBook(book)
    }
    
    suspend fun deleteBook(book: Book) {
        bookDao.deleteBook(book)
    }
    
    fun getReadingLogsForBook(bookId: Long): Flow<List<ReadingLog>> =
        readingLogDao.getReadingLogsForBook(bookId)
    
    suspend fun insertReadingLog(readingLog: ReadingLog) {
        readingLogDao.insertReadingLog(readingLog)
    }
    
    suspend fun deleteReadingLog(readingLog: ReadingLog) {
        readingLogDao.deleteReadingLog(readingLog)
    }
    
    fun getAllReadingLogsFlow(): Flow<List<ReadingLog>> =
        readingLogDao.getAllReadingLogsFlow()
    
    suspend fun clearAllData() {
        readingLogDao.deleteAllReadingLogs()
        bookDao.deleteAllBooks()
    }
}
