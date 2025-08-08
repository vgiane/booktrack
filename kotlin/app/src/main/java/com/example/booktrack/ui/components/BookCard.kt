package com.example.booktrack.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.PlayArrow
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.booktrack.data.entity.Book
import com.example.booktrack.data.entity.BookStatus

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookCard(
    book: Book,
    showTimer: Boolean = false,
    onTimerClick: () -> Unit = {},
    onEditClick: () -> Unit = {},
    onBookClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(0.6f) // Made smaller for 3xN grid
            .clickable { onBookClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Book cover area
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
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
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White,
                        textAlign = TextAlign.Center,
                        maxLines = 3,
                        overflow = TextOverflow.Ellipsis,
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
            
            // Action buttons area
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(4.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                if (showTimer && book.status == BookStatus.ACTIVE) {
                    IconButton(onClick = onTimerClick) {
                        Icon(
                            Icons.Default.Timer,
                            contentDescription = "Start Reading",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
                
                IconButton(onClick = onEditClick) {
                    Icon(
                        Icons.Default.Edit,
                        contentDescription = "Edit Book",
                        tint = MaterialTheme.colorScheme.secondary
                    )
                }
            }
            
            // Status indicator
            if (book.status != BookStatus.ACTIVE) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = getStatusColor(book.status)
                ) {
                    Text(
                        text = book.status.name,
                        modifier = Modifier.padding(4.dp),
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
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
        BookStatus.ACTIVE -> Color.Transparent
    }
}
