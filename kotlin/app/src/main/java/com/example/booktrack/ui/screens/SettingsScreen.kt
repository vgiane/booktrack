package com.example.booktrack.ui.screens

import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.booktrack.data.manager.ImportResult
import com.example.booktrack.ui.viewmodel.SettingsViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    settingsViewModel: SettingsViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by settingsViewModel.uiState.collectAsStateWithLifecycle()
    val dailyGoal by settingsViewModel.dailyGoalMinutes.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    
    var showDailyGoalDialog by remember { mutableStateOf(false) }
    var dailyGoalInput by remember { mutableStateOf(dailyGoal.toString()) }
    
    // File picker launchers
    val exportLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/json")
    ) { uri ->
        uri?.let { settingsViewModel.exportData(it) }
    }
    
    val importLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let { 
            settingsViewModel.setImportConfirmDialogVisible(true)
            // Store URI for later use
        }
    }
    
    Column(
        modifier = modifier.fillMaxSize()
    ) {
        TopAppBar(
            title = { Text("Settings") }
        )
        
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Daily Goal Section
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Daily Reading Goal",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Current Goal",
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Text(
                                text = "$dailyGoal minutes per day",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        
                        Button(
                            onClick = { 
                                dailyGoalInput = dailyGoal.toString()
                                showDailyGoalDialog = true 
                            }
                        ) {
                            Icon(Icons.Default.Flag, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Set Goal")
                        }
                    }
                }
            }
            
            // Data Management section
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Data Management",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Button(
                        onClick = {
                            exportLauncher.launch("booktrack_export_${System.currentTimeMillis()}.json")
                        },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !uiState.isExporting
                    ) {
                        if (uiState.isExporting) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                strokeWidth = 2.dp
                            )
                        } else {
                            Icon(Icons.Default.Download, contentDescription = null)
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(if (uiState.isExporting) "Exporting..." else "Export Data")
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    OutlinedButton(
                        onClick = {
                            importLauncher.launch("application/json")
                        },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !uiState.isImporting
                    ) {
                        if (uiState.isImporting) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                strokeWidth = 2.dp
                            )
                        } else {
                            Icon(Icons.Default.Upload, contentDescription = null)
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(if (uiState.isImporting) "Importing..." else "Import Data")
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Text(
                        text = "Export saves all your books and reading sessions to a JSON file. Import replaces all current data with data from a file.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            
            // Delete all data section
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Danger Zone",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.error
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    OutlinedButton(
                        onClick = { settingsViewModel.setDeleteConfirmDialogVisible(true) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = MaterialTheme.colorScheme.error
                        )
                    ) {
                        Icon(Icons.Default.Delete, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Delete All Data")
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Text(
                        text = "This will permanently delete all books and reading logs. This action cannot be undone.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            
            // App info section
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "About",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Version")
                        Text("1.6.0")
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Developer")
                        Text("Valerio Gianella")
                    }
                }
            }
        }
    }
    
    // Daily Goal Dialog
    if (showDailyGoalDialog) {
        AlertDialog(
            onDismissRequest = { showDailyGoalDialog = false },
            title = { Text("Set Daily Reading Goal") },
            text = {
                Column {
                    Text("How many minutes would you like to read per day?")
                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedTextField(
                        value = dailyGoalInput,
                        onValueChange = { dailyGoalInput = it },
                        label = { Text("Minutes") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val minutes = dailyGoalInput.toIntOrNull()
                        if (minutes != null && minutes > 0) {
                            settingsViewModel.setDailyGoal(minutes)
                            showDailyGoalDialog = false
                        }
                    }
                ) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDailyGoalDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
    
    // Delete confirmation dialog
    if (uiState.showDeleteConfirmDialog) {
        AlertDialog(
            onDismissRequest = { settingsViewModel.setDeleteConfirmDialogVisible(false) },
            title = { Text("Delete All Data") },
            text = { 
                Text("Are you sure you want to delete all books and reading logs? This action cannot be undone.")
            },
            confirmButton = {
                Button(
                    onClick = { settingsViewModel.deleteAllData() },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Delete All")
                }
            },
            dismissButton = {
                TextButton(onClick = { settingsViewModel.setDeleteConfirmDialogVisible(false) }) {
                    Text("Cancel")
                }
            }
        )
    }
    
    // Import confirmation dialog
    if (uiState.showImportConfirmDialog) {
        AlertDialog(
            onDismissRequest = { settingsViewModel.setImportConfirmDialogVisible(false) },
            title = { Text("Import Data") },
            text = { 
                Text("This will replace all your current data with the data from the selected file. Are you sure you want to continue?")
            },
            confirmButton = {
                Button(
                    onClick = { 
                        // settingsViewModel.importData(selectedUri) // Would need to store the URI
                        settingsViewModel.setImportConfirmDialogVisible(false)
                    }
                ) {
                    Text("Import")
                }
            },
            dismissButton = {
                TextButton(onClick = { settingsViewModel.setImportConfirmDialogVisible(false) }) {
                    Text("Cancel")
                }
            }
        )
    }
    
    // Export result dialog
    if (uiState.showExportResult) {
        AlertDialog(
            onDismissRequest = { settingsViewModel.clearExportResult() },
            title = { Text(if (uiState.exportSuccess) "Export Successful" else "Export Failed") },
            text = { 
                Text(if (uiState.exportSuccess) 
                    "Your data has been exported successfully." 
                else 
                    "Failed to export data. Please try again."
                )
            },
            confirmButton = {
                Button(onClick = { settingsViewModel.clearExportResult() }) {
                    Text("OK")
                }
            }
        )
    }
    
    // Import result dialog
    if (uiState.showImportResult) {
        AlertDialog(
            onDismissRequest = { settingsViewModel.clearImportResult() },
            title = { 
                Text(when (uiState.importResult) {
                    is ImportResult.Success -> "Import Successful"
                    is ImportResult.Error -> "Import Failed"
                    else -> "Import Result"
                })
            },
            text = { 
                Text(when (val result = uiState.importResult) {
                    is ImportResult.Success -> 
                        "Successfully imported ${result.booksImported} books and ${result.logsImported} reading sessions."
                    is ImportResult.Error -> 
                        "Failed to import data: ${result.message}"
                    else -> "Unknown result"
                })
            },
            confirmButton = {
                Button(onClick = { settingsViewModel.clearImportResult() }) {
                    Text("OK")
                }
            }
        )
    }
}
