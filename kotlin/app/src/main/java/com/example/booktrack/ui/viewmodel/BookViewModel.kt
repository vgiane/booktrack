package com.example.booktrack.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.booktrack.data.entity.Book
import com.example.booktrack.data.entity.BookStatus
import com.example.booktrack.data.entity.ReadingLog
import com.example.booktrack.data.repository.BooktrackRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class BookViewModel(private val repository: BooktrackRepository) : ViewModel() {
    
    private val _uiState = MutableStateFlow(BookUiState())
    val uiState: StateFlow<BookUiState> = _uiState.asStateFlow()
    
    val allBooks = repository.getAllBooks()
    val activeBooks = repository.getBooksByStatus(BookStatus.ACTIVE)
    
    fun addBook(title: String, author: String, totalPages: Int?, notes: String?) {
        viewModelScope.launch {
            val book = Book(
                title = title.trim(),
                author = author.trim(),
                totalPages = totalPages,
                notes = notes?.takeIf { it.isNotBlank() }
            )
            repository.insertBook(book)
        }
    }
    
    fun updateBook(book: Book) {
        viewModelScope.launch {
            repository.updateBook(book)
        }
    }
    
    fun deleteBook(book: Book) {
        viewModelScope.launch {
            repository.deleteBook(book)
        }
    }
    
    fun updateBookStatus(book: Book, newStatus: BookStatus) {
        viewModelScope.launch {
            repository.updateBook(book.copy(status = newStatus))
        }
    }
    
    fun updateBookNotes(book: Book, notes: String) {
        viewModelScope.launch {
            repository.updateBook(book.copy(notes = notes.takeIf { it.isNotBlank() }))
        }
    }
    
    suspend fun getBookById(id: Long): Book? {
        return repository.getBookById(id)
    }
    
    fun getReadingLogsByBook(bookId: Long) = repository.getReadingLogsByBook(bookId)
    
    fun deleteReadingLog(readingLog: ReadingLog) {
        viewModelScope.launch {
            repository.deleteReadingLog(readingLog)
        }
    }
    
    fun setShowAddBookDialog(show: Boolean) {
        _uiState.value = _uiState.value.copy(showAddBookDialog = show)
    }
    
    fun setShowEditBookDialog(show: Boolean, book: Book? = null) {
        _uiState.value = _uiState.value.copy(
            showEditBookDialog = show,
            editingBook = book
        )
    }
    
    fun setShowDeleteConfirmDialog(show: Boolean, book: Book? = null) {
        _uiState.value = _uiState.value.copy(
            showDeleteConfirmDialog = show,
            deletingBook = book
        )
    }
}

data class BookUiState(
    val showAddBookDialog: Boolean = false,
    val showEditBookDialog: Boolean = false,
    val showDeleteConfirmDialog: Boolean = false,
    val editingBook: Book? = null,
    val deletingBook: Book? = null
)
