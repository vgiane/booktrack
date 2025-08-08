package com.example.booktrack.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.booktrack.data.entity.Book
import com.example.booktrack.ui.components.*
import com.example.booktrack.ui.viewmodel.BookViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActiveBooksScreen(
    bookViewModel: BookViewModel,
    onNavigateToTimer: (Long) -> Unit,
    onNavigateToEditBook: (Long) -> Unit,
    onNavigateToBookView: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by bookViewModel.uiState.collectAsStateWithLifecycle()
    val activeBooks by bookViewModel.activeBooks.collectAsStateWithLifecycle(initialValue = emptyList())
    
    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            TopAppBar(
                title = { Text("Active Books") }
            )
            
            if (activeBooks.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "No active books yet",
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = { bookViewModel.setShowAddBookDialog(true) }
                        ) {
                            Text("Add Your First Book")
                        }
                    }
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(activeBooks) { book ->
                        BookCard(
                            book = book,
                            showTimer = true,
                            onTimerClick = { onNavigateToTimer(book.id) },
                            onEditClick = { onNavigateToEditBook(book.id) },
                            onBookClick = { onNavigateToBookView(book.id) }
                        )
                    }
                }
            }
        }
        
        FloatingActionButton(
            onClick = { bookViewModel.setShowAddBookDialog(true) },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        ) {
            Icon(Icons.Default.Add, contentDescription = "Add Book")
        }
    }
    
    // Dialogs
    if (uiState.showAddBookDialog) {
        EnhancedAddBookDialog(
            onDismiss = { bookViewModel.setShowAddBookDialog(false) },
            onConfirm = { title, author, totalPages, notes ->
                bookViewModel.addBook(title, author, totalPages, notes)
                bookViewModel.setShowAddBookDialog(false)
            }
        )
    }
}
