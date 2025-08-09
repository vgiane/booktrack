package com.example.booktrack.data.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ReadingLogDao {
    @Insert
    suspend fun insertReadingLog(readingLog: ReadingLog)

    @Delete
    suspend fun deleteReadingLog(readingLog: ReadingLog)

    @Query("SELECT * FROM reading_logs WHERE bookId = :bookId ORDER BY date DESC")
    fun getReadingLogsForBook(bookId: Long): Flow<List<ReadingLog>>
    
    @Query("SELECT * FROM reading_logs ORDER BY date DESC")
    suspend fun getAllReadingLogs(): List<ReadingLog>
    
    @Query("SELECT * FROM reading_logs ORDER BY date DESC")
    fun getAllReadingLogsFlow(): Flow<List<ReadingLog>>
    
    @Query("DELETE FROM reading_logs")
    suspend fun deleteAllReadingLogs()
    
    @Query("SELECT SUM(duration) FROM reading_logs WHERE date >= :startDate AND date <= :endDate")
    suspend fun getTotalReadingTimeInRange(startDate: Long, endDate: Long): Int?
}
