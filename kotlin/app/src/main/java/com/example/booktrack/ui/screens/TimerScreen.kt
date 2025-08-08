package com.example.booktrack.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.booktrack.data.entity.Book
import com.example.booktrack.ui.components.NotesDialog
import com.example.booktrack.ui.components.SaveReadingSessionDialog
import com.example.booktrack.ui.viewmodel.TimerViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimerScreen(
    book: Book,
    timerViewModel: TimerViewModel,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by timerViewModel.uiState.collectAsStateWithLifecycle()
    
    var notes by remember { mutableStateOf(book.notes ?: "") }
    
    LaunchedEffect(book) {
        if (uiState.currentBook == null) {
            timerViewModel.startTimer(book)
        }
    }
    
    Column(
        modifier = modifier.fillMaxSize()
    ) {
        
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // Book info section
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Book cover
                Box(
                    modifier = Modifier
                        .size(120.dp, 160.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(
                            brush = Brush.linearGradient(
                                colors = getBookCoverColors(book.title)
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = getAuthorInitials(book.author),
                        style = MaterialTheme.typography.headlineLarge,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Book title
            Text(
                text = book.title,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            
            Text(
                text = "by ${book.author}",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Timer display
            Card(
                modifier = Modifier.padding(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Text(
                    text = timerViewModel.formatTime(uiState.elapsedSeconds),
                    style = MaterialTheme.typography.displayLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(32.dp)
                )
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Control buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                // Cancel button
                OutlinedButton(
                    onClick = {
                        timerViewModel.cancelTimer()
                        onNavigateBack()
                    }
                ) {
                    Icon(Icons.Default.Close, contentDescription = "Cancel")
                }
                
                // Pause/Resume button
                if (uiState.isRunning) {
                    Button(
                        onClick = { timerViewModel.pauseTimer() }
                    ) {
                        Icon(Icons.Filled.Pause, contentDescription = "Pause")
                    }
                } else if (uiState.isPaused) {
                    Button(
                        onClick = { timerViewModel.resumeTimer() }
                    ) {
                        Icon(Icons.Default.PlayArrow, contentDescription = "Resume")
                    }
                }
                
                // Stop & Save button
                Button(
                    onClick = { timerViewModel.stopTimer() },
                    enabled = uiState.elapsedSeconds > 0
                ) {
                    Icon(Icons.Default.Done, contentDescription = "Stop & Save")
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Notes section at bottom
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Notes",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    OutlinedTextField(
                        value = notes,
                        onValueChange = { 
                            notes = it
                            // Auto-save notes when navigating away
                            timerViewModel.updateBookNotes(it)
                        },
                        label = { Text("Add your reading notes...") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 4,
                        maxLines = 8
                    )
                }
            }
        }
        }
    }
    
    // Save session dialog
    if (uiState.showSaveDialog && uiState.startTime != null && uiState.endTime != null) {
        SaveReadingSessionDialog(
            elapsedSeconds = uiState.elapsedSeconds,
            startTime = uiState.startTime!!,
            endTime = uiState.endTime!!,
            onDismiss = { timerViewModel.setSaveDialogVisible(false) },
            onSave = { pagesRead: Int?, startTime: java.time.LocalDateTime, endTime: java.time.LocalDateTime ->
                timerViewModel.saveReadingSession(pagesRead, startTime, endTime)
                onNavigateBack()
            }
        )
    }
}

private fun getBookCoverColors(title: String): List<Color> {
    val colorSets = listOf(
        listOf(Color(0xFF6200EE), Color(0xFF3700B3)),
        listOf(Color(0xFF03DAC6), Color(0xFF018786)),
        listOf(Color(0xFFFF6B6B), Color(0xFFEE5A52)),
        listOf(Color(0xFF4ECDC4), Color(0xFF44A08D)),
        listOf(Color(0xFF45B7D1), Color(0xFF3A9BC1)),
        listOf(Color(0xFFF093FB), Color(0xFFF5576C)),
        listOf(Color(0xFF4FACFE), Color(0xFF00F2FE)),
        listOf(Color(0xFFA8E6CF), Color(0xFF88D8A3))
    )
    
    val hash = title.hashCode()
    val index = kotlin.math.abs(hash) % colorSets.size
    return colorSets[index]
}

private fun getAuthorInitials(author: String): String {
    return author.split(" ")
        .take(2)
        .map { it.first().uppercaseChar() }
        .joinToString("")
}
