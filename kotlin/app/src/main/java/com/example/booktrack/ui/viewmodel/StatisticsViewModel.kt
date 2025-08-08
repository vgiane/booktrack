package com.example.booktrack.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.booktrack.data.repository.BooktrackRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

class StatisticsViewModel(private val repository: BooktrackRepository) : ViewModel() {
    
    private val _uiState = MutableStateFlow(StatisticsUiState())
    val uiState: StateFlow<StatisticsUiState> = _uiState.asStateFlow()
    
    init {
        loadStatistics()
    }
    
    private fun loadStatistics() {
        viewModelScope.launch {
            val totalTime = repository.getTotalReadingTime()
            val last7DaysData = getLast7DaysData()
            
            _uiState.value = _uiState.value.copy(
                totalReadingTimeSeconds = totalTime,
                last7DaysData = last7DaysData,
                isLoading = false
            )
        }
    }
    
    private suspend fun getLast7DaysData(): List<DayReadingData> {
        val today = LocalDate.now()
        val data = mutableListOf<DayReadingData>()
        
        for (i in 6 downTo 0) {
            val date = today.minusDays(i.toLong())
            val startDateTime = date.atStartOfDay()
            val endDateTime = date.atTime(LocalTime.MAX)
            
            val totalTime = repository.getTotalReadingTimeBetweenDates(startDateTime, endDateTime)
            
            data.add(
                DayReadingData(
                    date = date,
                    readingTimeSeconds = totalTime
                )
            )
        }
        
        return data
    }
    
    fun loadWeeklyData() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            
            val today = LocalDate.now()
            val weeklyData = mutableListOf<WeekReadingData>()
            
            // Get data for the last 12 weeks
            for (week in 11 downTo 0) {
                val startOfWeek = today.minusWeeks(week.toLong()).with(java.time.DayOfWeek.MONDAY)
                val endOfWeek = startOfWeek.plusDays(6)
                
                val startDateTime = startOfWeek.atStartOfDay()
                val endDateTime = endOfWeek.atTime(LocalTime.MAX)
                
                val totalTime = repository.getTotalReadingTimeBetweenDates(startDateTime, endDateTime)
                
                weeklyData.add(
                    WeekReadingData(
                        weekStart = startOfWeek,
                        weekEnd = endOfWeek,
                        readingTimeSeconds = totalTime
                    )
                )
            }
            
            _uiState.value = _uiState.value.copy(
                weeklyData = weeklyData,
                isLoading = false
            )
        }
    }
    
    fun loadMonthlyData() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            
            val today = LocalDate.now()
            val monthlyData = mutableListOf<MonthReadingData>()
            
            // Get data for the last 12 months
            for (month in 11 downTo 0) {
                val targetMonth = today.minusMonths(month.toLong())
                val startOfMonth = targetMonth.withDayOfMonth(1)
                val endOfMonth = targetMonth.withDayOfMonth(targetMonth.lengthOfMonth())
                
                val startDateTime = startOfMonth.atStartOfDay()
                val endDateTime = endOfMonth.atTime(LocalTime.MAX)
                
                val totalTime = repository.getTotalReadingTimeBetweenDates(startDateTime, endDateTime)
                
                monthlyData.add(
                    MonthReadingData(
                        month = targetMonth,
                        readingTimeSeconds = totalTime
                    )
                )
            }
            
            _uiState.value = _uiState.value.copy(
                monthlyData = monthlyData,
                isLoading = false
            )
        }
    }
    
    fun formatTime(seconds: Long): String {
        val hours = seconds / 3600
        val minutes = (seconds % 3600) / 60
        return when {
            hours > 0 -> "${hours}h ${minutes}m"
            minutes > 0 -> "${minutes}m"
            else -> "${seconds}s"
        }
    }
}

data class StatisticsUiState(
    val isLoading: Boolean = true,
    val totalReadingTimeSeconds: Long = 0,
    val last7DaysData: List<DayReadingData> = emptyList(),
    val weeklyData: List<WeekReadingData> = emptyList(),
    val monthlyData: List<MonthReadingData> = emptyList()
)

data class DayReadingData(
    val date: LocalDate,
    val readingTimeSeconds: Long
)

data class WeekReadingData(
    val weekStart: LocalDate,
    val weekEnd: LocalDate,
    val readingTimeSeconds: Long
)

data class MonthReadingData(
    val month: LocalDate,
    val readingTimeSeconds: Long
)
