package com.example.booktrack.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.booktrack.data.database.Book
import com.example.booktrack.data.database.BookStatus
import com.example.booktrack.data.database.ReadingLog
import com.example.booktrack.data.repository.BookRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import android.content.Context
import android.content.SharedPreferences

class BookViewModel(
    private val repository: BookRepository,
    private val context: Context
) : ViewModel() {
    
    private val _activeBooks = MutableStateFlow<List<Book>>(emptyList())
    val activeBooks: StateFlow<List<Book>> = _activeBooks.asStateFlow()
    
    private val _allBooks = MutableStateFlow<List<Book>>(emptyList())
    val allBooks: StateFlow<List<Book>> = _allBooks.asStateFlow()
    
    private val _currentBook = MutableStateFlow<Book?>(null)
    val currentBook: StateFlow<Book?> = _currentBook.asStateFlow()
    
    private val _currentBookLogs = MutableStateFlow<List<ReadingLog>>(emptyList())
    val currentBookLogs: StateFlow<List<ReadingLog>> = _currentBookLogs.asStateFlow()
    
    private val _allReadingLogs = MutableStateFlow<List<ReadingLog>>(emptyList())
    val allReadingLogs: StateFlow<List<ReadingLog>> = _allReadingLogs.asStateFlow()
    
    private val _dailyGoal = MutableStateFlow<Int?>(null)
    val dailyGoal: StateFlow<Int?> = _dailyGoal.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    private val sharedPreferences: SharedPreferences = 
        context.getSharedPreferences("booktrack_prefs", Context.MODE_PRIVATE)

    init {
        loadBooks()
        loadAllReadingLogs()
        loadDailyGoal()
    }

    private fun loadBooks() {
        viewModelScope.launch {
            repository.getActiveBooks().collect { books ->
                _activeBooks.value = books
            }
        }
        
        viewModelScope.launch {
            repository.getAllBooks().collect { books ->
                _allBooks.value = books
            }
        }
    }

    fun loadBookDetails(bookId: Long) {
        viewModelScope.launch {
            repository.getBookById(bookId).collect { book ->
                _currentBook.value = book
            }
        }
        
        viewModelScope.launch {
            repository.getReadingLogsForBook(bookId).collect { logs ->
                _currentBookLogs.value = logs
            }
        }
    }

    fun addBook(book: Book) {
        viewModelScope.launch {
            _isLoading.value = true
            repository.insertBook(book)
            _isLoading.value = false
        }
    }

    fun updateBook(book: Book) {
        viewModelScope.launch {
            _isLoading.value = true
            repository.updateBook(book)
            _isLoading.value = false
        }
    }

    fun deleteBook(book: Book) {
        viewModelScope.launch {
            _isLoading.value = true
            repository.deleteBook(book)
            _isLoading.value = false
        }
    }

    fun addReadingSession(bookId: Long, durationSeconds: Long, pagesRead: Int?) {
        viewModelScope.launch {
            val readingLog = ReadingLog(
                bookId = bookId,
                date = System.currentTimeMillis(),
                duration = durationSeconds.toInt(),
                pagesRead = pagesRead
            )
            repository.insertReadingLog(readingLog)
        }
    }

    fun deleteReadingLog(readingLog: ReadingLog) {
        viewModelScope.launch {
            repository.deleteReadingLog(readingLog)
        }
    }

    fun canStartTimer(book: Book): Boolean {
        return book.status == BookStatus.ACTIVE
    }

    fun clearCurrentBook() {
        _currentBook.value = null
        _currentBookLogs.value = emptyList()
    }
    
    private fun loadAllReadingLogs() {
        viewModelScope.launch {
            repository.getAllReadingLogsFlow().collect { logs ->
                _allReadingLogs.value = logs
            }
        }
    }
    
    private fun loadDailyGoal() {
        val goal = sharedPreferences.getInt("daily_goal", 60)
        _dailyGoal.value = if (goal > 0) goal else null
    }
    
    fun setDailyGoal(minutes: Int) {
        viewModelScope.launch {
            sharedPreferences.edit().putInt("daily_goal", minutes).apply()
            _dailyGoal.value = minutes
        }
    }
    
    fun clearAllData() {
        viewModelScope.launch {
            _isLoading.value = true
            repository.clearAllData()
            sharedPreferences.edit().clear().apply()
            _dailyGoal.value = null
            _isLoading.value = false
        }
    }
}
