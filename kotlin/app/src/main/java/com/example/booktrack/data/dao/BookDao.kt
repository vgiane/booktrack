package com.example.booktrack.data.dao

import androidx.room.*
import com.example.booktrack.data.entity.Book
import com.example.booktrack.data.entity.BookStatus
import kotlinx.coroutines.flow.Flow

@Dao
interface BookDao {
    
    @Query("SELECT * FROM books ORDER BY title ASC")
    fun getAllBooks(): Flow<List<Book>>
    
    @Query("SELECT * FROM books WHERE status = :status ORDER BY title ASC")
    fun getBooksByStatus(status: BookStatus): Flow<List<Book>>
    
    @Query("SELECT * FROM books WHERE id = :id")
    suspend fun getBookById(id: Long): Book?
    
    @Insert
    suspend fun insertBook(book: Book): Long
    
    @Update
    suspend fun updateBook(book: Book)
    
    @Delete
    suspend fun deleteBook(book: Book)
    
    @Query("DELETE FROM books WHERE id = :bookId")
    suspend fun deleteBookById(bookId: Long)
    
    @Query("DELETE FROM books")
    suspend fun deleteAllBooks()
}
