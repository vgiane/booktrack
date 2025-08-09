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
}
