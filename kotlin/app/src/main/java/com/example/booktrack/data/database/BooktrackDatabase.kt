package com.example.booktrack.data.database

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import android.content.Context
import com.example.booktrack.data.converters.Converters
import com.example.booktrack.data.dao.BookDao
import com.example.booktrack.data.dao.ReadingLogDao
import com.example.booktrack.data.entity.Book
import com.example.booktrack.data.entity.ReadingLog

@Database(
    entities = [Book::class, ReadingLog::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class BooktrackDatabase : RoomDatabase() {
    
    abstract fun bookDao(): BookDao
    abstract fun readingLogDao(): ReadingLogDao
    
    companion object {
        @Volatile
        private var INSTANCE: BooktrackDatabase? = null
        
        fun getDatabase(context: Context): BooktrackDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    BooktrackDatabase::class.java,
                    "booktrack_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
