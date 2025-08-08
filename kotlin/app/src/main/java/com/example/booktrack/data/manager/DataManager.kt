package com.example.booktrack.data.manager

import android.content.Context
import android.net.Uri
import com.example.booktrack.data.dto.BookExportData
import com.example.booktrack.data.dto.ExportData
import com.example.booktrack.data.dto.ReadingLogExportData
import com.example.booktrack.data.repository.BooktrackRepository
import kotlinx.coroutines.flow.first
import java.io.InputStream
import java.io.OutputStream

class DataManager(
    private val repository: BooktrackRepository,
    private val context: Context
) {
    
    suspend fun exportData(): String {
        val books = repository.getAllBooks().first()
        val readingLogs = mutableMapOf<Long, List<com.example.booktrack.data.entity.ReadingLog>>()
        
        books.forEach { book ->
            readingLogs[book.id] = repository.getReadingLogsByBook(book.id).first()
        }
        
        val exportData = ExportData.fromBooks(books, readingLogs)
        return ExportData.toJson(exportData)
    }
    
    suspend fun exportDataToFile(uri: Uri): Boolean {
        return try {
            val jsonData = exportData()
            context.contentResolver.openOutputStream(uri)?.use { outputStream ->
                outputStream.write(jsonData.toByteArray())
                outputStream.flush()
            }
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
    
    suspend fun importDataFromFile(uri: Uri): ImportResult {
        return try {
            val jsonData = context.contentResolver.openInputStream(uri)?.use { inputStream ->
                inputStream.readBytes().toString(Charsets.UTF_8)
            }
            
            if (jsonData != null) {
                importData(jsonData)
            } else {
                ImportResult.Error("Failed to read file")
            }
        } catch (e: Exception) {
            e.printStackTrace()
            ImportResult.Error("Error reading file: ${e.message}")
        }
    }
    
    suspend fun importData(jsonData: String): ImportResult {
        return try {
            val exportData = ExportData.fromJson(jsonData)
            
            // Validate the data structure
            if (!isValidExportData(exportData)) {
                return ImportResult.Error("Invalid data format")
            }
            
            // Clear existing data
            repository.deleteAllData()
            
            // Import books and reading logs
            var booksImported = 0
            var logsImported = 0
            
            exportData.books.forEach { bookData ->
                val book = BookExportData.toBook(bookData)
                val bookId = repository.insertBook(book)
                booksImported++
                
                bookData.readingLogs.forEach { logData ->
                    val readingLog = ReadingLogExportData.toReadingLog(logData, bookId)
                    repository.insertReadingLog(readingLog)
                    logsImported++
                }
            }
            
            ImportResult.Success(booksImported, logsImported)
        } catch (e: Exception) {
            e.printStackTrace()
            ImportResult.Error("Error importing data: ${e.message}")
        }
    }
    
    private fun isValidExportData(exportData: ExportData): Boolean {
        return try {
            // Basic validation
            exportData.books.all { bookData ->
                bookData.title.isNotBlank() && 
                bookData.author.isNotBlank() &&
                bookData.readingLogs.all { logData ->
                    logData.time >= 0 &&
                    logData.startTime.isNotBlank() &&
                    logData.endTime.isNotBlank()
                }
            }
        } catch (e: Exception) {
            false
        }
    }
}

sealed class ImportResult {
    data class Success(val booksImported: Int, val logsImported: Int) : ImportResult()
    data class Error(val message: String) : ImportResult()
}
