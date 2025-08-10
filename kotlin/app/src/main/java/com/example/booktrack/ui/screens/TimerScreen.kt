package com.example.booktrack.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.booktrack.data.database.Book
import kotlinx.coroutines.delay
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimerScreen(
    book: Book,
    onSaveSession: (Long, Int?) -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isRunning by remember { mutableStateOf(true) }
    var isPaused by remember { mutableStateOf(false) }
    var elapsedTime by remember { mutableLongStateOf(0L) }
    var showSaveDialog by remember { mutableStateOf(false) }
    var notes by remember { mutableStateOf(book.notes ?: "") }

    // Timer effect
    LaunchedEffect(isRunning) {
        while (isRunning) {
            delay(1000)
            elapsedTime += 1000
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Reading Timer") },
                navigationIcon = {
                    IconButton(onClick = onCancel) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Book info
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = book.title,
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = "by ${book.author}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Timer display
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Column(
                    modifier = Modifier.padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = formatTime(elapsedTime),
                        style = MaterialTheme.typography.displayLarge.copy(
                            fontSize = 48.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Text(
                        text = when {
                            isRunning -> "Reading..."
                            isPaused -> "Paused"
                            else -> "Ready to start"
                        },
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }

            // Timer controls
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Cancel button
                OutlinedButton(
                    onClick = onCancel,
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Icon(Icons.Filled.Close, contentDescription = null)
                }

                // Play/Pause button
                Button(
                    onClick = {
                        if (isRunning) {
                            isRunning = false
                            isPaused = true
                        } else {
                            isRunning = true
                            isPaused = false
                        }
                    }
                ) {
                    Icon(
                        if (isRunning) Icons.Filled.PauseCircle else Icons.Filled.PlayArrow,
                        contentDescription = if (isRunning) "Pause" else "Play"
                    )
                }

                // Stop & Save button
                Button(
                    onClick = { showSaveDialog = true },
                    enabled = elapsedTime > 0,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.tertiary
                    )
                ) {
                    Icon(Icons.Filled.Check, contentDescription = null)
                }
            }

            // Notes section
            OutlinedTextField(
                value = notes,
                onValueChange = { notes = it },
                label = { Text("Notes") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                maxLines = 5
            )
        }

        // Save session dialog
        if (showSaveDialog) {
            var pagesRead by remember { mutableStateOf("") }

            AlertDialog(
                onDismissRequest = { showSaveDialog = false },
                title = { Text("Save Reading Session") },
                text = {
                    Column {
                        Text("Duration: ${formatTime(elapsedTime)}")
                        Spacer(modifier = Modifier.height(16.dp))
                        OutlinedTextField(
                            value = pagesRead,
                            onValueChange = { pagesRead = it },
                            label = { Text("Pages read (optional)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            isRunning = false
                            onSaveSession(
                                elapsedTime / 1000, // Convert to seconds
                                pagesRead.toIntOrNull()
                            )
                            showSaveDialog = false
                        }
                    ) {
                        Text("Save")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showSaveDialog = false }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}

private fun formatTime(milliseconds: Long): String {
    val totalSeconds = milliseconds / 1000
    val hours = totalSeconds / 3600
    val minutes = (totalSeconds % 3600) / 60
    val seconds = totalSeconds % 60
    
    return if (hours > 0) {
        String.format(Locale.getDefault(), "%02d:%02d:%02d", hours, minutes, seconds)    } else {
        String.format(Locale.getDefault(),"%02d:%02d", minutes, seconds)
    }
}
