package com.example.booktrack.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Composable
fun NotesDialog(
    currentNotes: String?,
    onDismiss: () -> Unit,
    onSave: (String) -> Unit
) {
    var notes by remember { mutableStateOf(currentNotes ?: "") }
    
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Book Notes",
                    style = MaterialTheme.typography.headlineSmall
                )
                
                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Notes") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 4,
                    maxLines = 8
                )
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            onSave(notes)
                        }
                    ) {
                        Text("Save")
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SaveReadingSessionDialog(
    elapsedSeconds: Long,
    startTime: LocalDateTime,
    endTime: LocalDateTime,
    onDismiss: () -> Unit,
    onSave: (pagesRead: Int?, startTime: LocalDateTime, endTime: LocalDateTime) -> Unit
) {
    var pagesReadText by remember { mutableStateOf("") }
    var editedStartTime by remember { mutableStateOf(startTime) }
    var editedEndTime by remember { mutableStateOf(endTime) }
    
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Save Reading Session",
                    style = MaterialTheme.typography.headlineSmall
                )
                
                Text(
                    text = "Elapsed Time: ${formatTime(elapsedSeconds)}",
                    style = MaterialTheme.typography.bodyLarge
                )
                
                Text(
                    text = "Start Time: ${startTime.format(DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm"))}",
                    style = MaterialTheme.typography.bodyMedium
                )
                
                Text(
                    text = "End Time: ${endTime.format(DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm"))}",
                    style = MaterialTheme.typography.bodyMedium
                )
                
                OutlinedTextField(
                    value = pagesReadText,
                    onValueChange = { pagesReadText = it },
                    label = { Text("Pages Read (Optional)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            val pagesRead = pagesReadText.toIntOrNull()
                            onSave(pagesRead, editedStartTime, editedEndTime)
                        }
                    ) {
                        Text("Save Session")
                    }
                }
            }
        }
    }
}

private fun formatTime(seconds: Long): String {
    val hours = seconds / 3600
    val minutes = (seconds % 3600) / 60
    val secs = seconds % 60
    return String.format("%02d:%02d:%02d", hours, minutes, secs)
}

object CommonDialogs {
    @Composable
    fun ConfirmationDialog(
        title: String,
        message: String,
        onConfirm: () -> Unit,
        onDismiss: () -> Unit,
        confirmText: String = "Confirm",
        dismissText: String = "Cancel"
    ) {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text(title) },
            text = { Text(message) },
            confirmButton = {
                Button(
                    onClick = onConfirm,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text(confirmText)
                }
            },
            dismissButton = {
                TextButton(onClick = onDismiss) {
                    Text(dismissText)
                }
            }
        )
    }
}
