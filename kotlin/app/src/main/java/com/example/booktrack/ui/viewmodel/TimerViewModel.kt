package com.example.booktrack.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.booktrack.data.entity.Book
import com.example.booktrack.data.entity.ReadingLog
import com.example.booktrack.data.repository.BooktrackRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDateTime

class TimerViewModel(private val repository: BooktrackRepository) : ViewModel() {
    
    private val _uiState = MutableStateFlow(TimerUiState())
    val uiState: StateFlow<TimerUiState> = _uiState.asStateFlow()
    
    fun startTimer(book: Book) {
        _uiState.value = _uiState.value.copy(
            currentBook = book,
            isRunning = true,
            isPaused = false,
            startTime = LocalDateTime.now(),
            elapsedSeconds = 0
        )
        startInternalTimer()
    }
    
    fun pauseTimer() {
        _uiState.value = _uiState.value.copy(
            isRunning = false,
            isPaused = true
        )
    }
    
    fun resumeTimer() {
        _uiState.value = _uiState.value.copy(
            isRunning = true,
            isPaused = false
        )
        startInternalTimer()
    }
    
    fun cancelTimer() {
        _uiState.value = TimerUiState()
    }
    
    fun stopTimer() {
        _uiState.value = _uiState.value.copy(
            isRunning = false,
            showSaveDialog = true,
            endTime = LocalDateTime.now()
        )
    }
    
    fun saveReadingSession(pagesRead: Int?, startTime: LocalDateTime, endTime: LocalDateTime) {
        val currentState = _uiState.value
        val book = currentState.currentBook ?: return
        
        viewModelScope.launch {
            val readingLog = ReadingLog(
                bookId = book.id,
                startTime = startTime,
                endTime = endTime,
                elapsedTimeSeconds = currentState.elapsedSeconds,
                pagesRead = pagesRead
            )
            repository.insertReadingLog(readingLog)
            _uiState.value = TimerUiState()
        }
    }
    
    fun setSaveDialogVisible(visible: Boolean) {
        _uiState.value = _uiState.value.copy(showSaveDialog = visible)
    }
    
    private fun startInternalTimer() {
        viewModelScope.launch {
            while (_uiState.value.isRunning) {
                kotlinx.coroutines.delay(1000L)
                if (_uiState.value.isRunning) {
                    _uiState.value = _uiState.value.copy(
                        elapsedSeconds = _uiState.value.elapsedSeconds + 1
                    )
                }
            }
        }
    }
    
    fun updateBookNotes(notes: String) {
        val book = _uiState.value.currentBook ?: return
        viewModelScope.launch {
            repository.updateBook(book.copy(notes = notes.takeIf { it.isNotBlank() }))
            _uiState.value = _uiState.value.copy(
                currentBook = book.copy(notes = notes.takeIf { it.isNotBlank() })
            )
        }
    }
    
    fun formatTime(seconds: Long): String {
        val hours = seconds / 3600
        val minutes = (seconds % 3600) / 60
        val secs = seconds % 60
        return String.format("%02d:%02d:%02d", hours, minutes, secs)
    }
}

data class TimerUiState(
    val currentBook: Book? = null,
    val isRunning: Boolean = false,
    val isPaused: Boolean = false,
    val elapsedSeconds: Long = 0,
    val startTime: LocalDateTime? = null,
    val endTime: LocalDateTime? = null,
    val showSaveDialog: Boolean = false
)
