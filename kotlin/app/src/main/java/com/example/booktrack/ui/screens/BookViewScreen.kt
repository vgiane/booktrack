package com.example.booktrack.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.booktrack.data.entity.Book
import com.example.booktrack.data.entity.BookStatus
import com.example.booktrack.data.entity.ReadingLog
import com.example.booktrack.ui.components.CommonDialogs
import com.example.booktrack.ui.viewmodel.BookViewModel
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookViewScreen(
    book: Book,
    bookViewModel: BookViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToEdit: (Long) -> Unit,
    onNavigateToTimer: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val readingLogs by bookViewModel.getReadingLogsByBook(book.id)
        .collectAsStateWithLifecycle(initialValue = emptyList())
    
    var showDeleteLogDialog by remember { mutableStateOf<ReadingLog?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Book Details") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    if (book.status == BookStatus.ACTIVE) {
                        IconButton(onClick = { onNavigateToTimer(book.id) }) {
                            Icon(Icons.Default.Timer, contentDescription = "Start Reading")
                        }
                    }
                    IconButton(onClick = { onNavigateToEdit(book.id) }) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit Book")
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Book Cover and Basic Info
            item {
                BookCoverSection(book = book)
            }
            
            // Book Details
            item {
                BookDetailsSection(book = book)
            }
            
            // Notes Section
            if (!book.notes.isNullOrBlank()) {
                item {
                    NotesSection(notes = book.notes)
                }
            }
            
            // Reading Sessions Header
            item {
                Text(
                    text = "Reading Sessions",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
            }
            
            // Reading Sessions List
            if (readingLogs.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No reading sessions yet",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            } else {
                items(readingLogs.sortedByDescending { it.startTime }) { log ->
                    ReadingLogItem(
                        readingLog = log,
                        onDeleteClick = { showDeleteLogDialog = log }
                    )
                }
            }
        }
    }
    
    // Delete Reading Log Dialog
    showDeleteLogDialog?.let { logToDelete ->
        CommonDialogs.ConfirmationDialog(
            title = "Delete Reading Session",
            message = "Are you sure you want to delete this reading session? This action cannot be undone.",
            onConfirm = {
                bookViewModel.deleteReadingLog(logToDelete)
                showDeleteLogDialog = null
            },
            onDismiss = { showDeleteLogDialog = null }
        )
    }
}

@Composable
private fun BookCoverSection(
    book: Book,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Book Cover
        Box(
            modifier = Modifier
                .width(120.dp)
                .height(180.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(
                    brush = Brush.linearGradient(
                        colors = getBookCoverColors(book.title)
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(8.dp)
            ) {
                Text(
                    text = book.title,
                    style = MaterialTheme.typography.titleSmall,
                    color = Color.White,
                    textAlign = TextAlign.Center,
                    maxLines = 4,
                    fontWeight = FontWeight.Bold
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = getAuthorInitials(book.author),
                    style = MaterialTheme.typography.headlineMedium,
                    color = Color.White.copy(alpha = 0.8f),
                    fontWeight = FontWeight.Bold
                )
            }
        }
        
        // Basic Info
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = book.title,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            
            Text(
                text = "by ${book.author}",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            StatusChip(status = book.status)
        }
    }
}

@Composable
private fun BookDetailsSection(
    book: Book,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Details",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            
            Divider()
            
            if (book.totalPages != null) {
                DetailRow(label = "Total Pages", value = book.totalPages.toString())
            }
            
            DetailRow(label = "Status", value = book.status.name.lowercase().replaceFirstChar { it.uppercase() })
        }
    }
}

@Composable
private fun NotesSection(
    notes: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Notes",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            
            Divider()
            
            Text(
                text = notes,
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}

@Composable
private fun ReadingLogItem(
    readingLog: ReadingLog,
    onDeleteClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = readingLog.startTime.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                
                Text(
                    text = "${readingLog.startTime.format(DateTimeFormatter.ofPattern("HH:mm"))} - " +
                            "${readingLog.endTime.format(DateTimeFormatter.ofPattern("HH:mm"))}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                Text(
                    text = formatDuration(readingLog.elapsedTimeSeconds),
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium
                )
                
                if (readingLog.pagesRead != null) {
                    Text(
                        text = "${readingLog.pagesRead} pages read",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            
            IconButton(onClick = onDeleteClick) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = "Delete session",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

@Composable
private fun StatusChip(
    status: BookStatus,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        color = getStatusColor(status)
    ) {
        Text(
            text = status.name.lowercase().replaceFirstChar { it.uppercase() },
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
            style = MaterialTheme.typography.labelMedium,
            color = Color.White
        )
    }
}

@Composable
private fun DetailRow(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Medium
        )
    }
}

private fun formatDuration(seconds: Long): String {
    val hours = seconds / 3600
    val minutes = (seconds % 3600) / 60
    
    return when {
        hours > 0 -> "${hours}h ${minutes}m"
        minutes > 0 -> "${minutes}m"
        else -> "${seconds}s"
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

private fun getStatusColor(status: BookStatus): Color {
    return when (status) {
        BookStatus.READ -> Color(0xFF4CAF50)
        BookStatus.PAUSED -> Color(0xFFFF9800)
        BookStatus.ABANDONED -> Color(0xFFF44336)
        BookStatus.ACTIVE -> Color(0xFF2196F3)
    }
}
