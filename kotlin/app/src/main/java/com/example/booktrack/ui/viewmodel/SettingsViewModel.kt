package com.example.booktrack.ui.viewmodel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.booktrack.data.manager.DataManager
import com.example.booktrack.data.manager.ImportResult
import com.example.booktrack.data.repository.BooktrackRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val repository: BooktrackRepository,
    private val context: Context
) : ViewModel() {
    
    private val dataManager = DataManager(repository, context)
    
    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()
    
    private val _dailyGoalMinutes = MutableStateFlow(60) // Default 60 minutes
    val dailyGoalMinutes: StateFlow<Int> = _dailyGoalMinutes.asStateFlow()
    
    fun deleteAllData() {
        viewModelScope.launch {
            repository.deleteAllData()
            _uiState.value = _uiState.value.copy(showDeleteConfirmDialog = false)
        }
    }
    
    fun setDeleteConfirmDialogVisible(visible: Boolean) {
        _uiState.value = _uiState.value.copy(showDeleteConfirmDialog = visible)
    }
    
    fun exportData(uri: Uri) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isExporting = true)
            val success = dataManager.exportDataToFile(uri)
            _uiState.value = _uiState.value.copy(
                isExporting = false,
                exportSuccess = success,
                showExportResult = true
            )
        }
    }
    
    fun importData(uri: Uri) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isImporting = true, showImportConfirmDialog = false)
            val result = dataManager.importDataFromFile(uri)
            _uiState.value = _uiState.value.copy(
                isImporting = false,
                importResult = result,
                showImportResult = true
            )
        }
    }
    
    fun setImportConfirmDialogVisible(visible: Boolean) {
        _uiState.value = _uiState.value.copy(showImportConfirmDialog = visible)
    }
    
    fun clearExportResult() {
        _uiState.value = _uiState.value.copy(showExportResult = false, exportSuccess = false)
    }
    
    fun clearImportResult() {
        _uiState.value = _uiState.value.copy(showImportResult = false, importResult = null)
    }
    
    fun setDailyGoal(minutes: Int) {
        _dailyGoalMinutes.value = minutes
        // In a real app, you would save this to SharedPreferences or database
    }
    
    fun getDailyGoal(): Int = _dailyGoalMinutes.value
}

data class SettingsUiState(
    val showDeleteConfirmDialog: Boolean = false,
    val showImportConfirmDialog: Boolean = false,
    val showExportResult: Boolean = false,
    val showImportResult: Boolean = false,
    val isExporting: Boolean = false,
    val isImporting: Boolean = false,
    val exportSuccess: Boolean = false,
    val importResult: ImportResult? = null
)
