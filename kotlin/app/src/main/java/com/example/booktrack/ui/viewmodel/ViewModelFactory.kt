package com.example.booktrack.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.booktrack.data.repository.BooktrackRepository

class ViewModelFactory(
    private val repository: BooktrackRepository,
    private val context: Context
) : ViewModelProvider.Factory {
    
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when (modelClass) {
            BookViewModel::class.java -> BookViewModel(repository) as T
            TimerViewModel::class.java -> TimerViewModel(repository) as T
            StatisticsViewModel::class.java -> StatisticsViewModel(repository) as T
            SettingsViewModel::class.java -> SettingsViewModel(repository, context) as T
            else -> throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
