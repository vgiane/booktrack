package com.example.booktrack.data.dao

import androidx.room.*
import com.example.booktrack.data.entity.ReadingLog
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime

@Dao
interface ReadingLogDao {
    
    @Query("SELECT * FROM reading_logs WHERE bookId = :bookId ORDER BY startTime DESC")
    fun getReadingLogsByBook(bookId: Long): Flow<List<ReadingLog>>
    
    @Query("SELECT * FROM reading_logs ORDER BY startTime DESC")
    fun getAllReadingLogs(): Flow<List<ReadingLog>>
    
    @Query("SELECT * FROM reading_logs WHERE startTime >= :startDate AND startTime <= :endDate ORDER BY startTime DESC")
    fun getReadingLogsBetweenDates(startDate: LocalDateTime, endDate: LocalDateTime): Flow<List<ReadingLog>>
    
    @Query("SELECT SUM(elapsedTimeSeconds) FROM reading_logs")
    suspend fun getTotalReadingTime(): Long?
    
    @Query("SELECT SUM(elapsedTimeSeconds) FROM reading_logs WHERE startTime >= :startDate AND startTime <= :endDate")
    suspend fun getTotalReadingTimeBetweenDates(startDate: LocalDateTime, endDate: LocalDateTime): Long?
    
    @Insert
    suspend fun insertReadingLog(readingLog: ReadingLog): Long
    
    @Update
    suspend fun updateReadingLog(readingLog: ReadingLog)
    
    @Delete
    suspend fun deleteReadingLog(readingLog: ReadingLog)
    
    @Query("DELETE FROM reading_logs")
    suspend fun deleteAllReadingLogs()
}
