package com.example.booktrack.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.booktrack.data.api.GoogleBookItem
import com.example.booktrack.data.service.GoogleBooksService
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EnhancedAddBookDialog(
    onDismiss: () -> Unit,
    onConfirm: (title: String, author: String, totalPages: Int?, notes: String?) -> Unit
) {
    var selectedTab by remember { mutableStateOf(0) }
    
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.9f)
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                Text(
                    text = "Add New Book",
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(16.dp)
                )
                
                TabRow(selectedTabIndex = selectedTab) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        text = { Text("Manual Entry") }
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        text = { Text("Search Books") }
                    )
                }
                
                when (selectedTab) {
                    0 -> ManualBookEntry(
                        onDismiss = onDismiss,
                        onConfirm = onConfirm,
                        modifier = Modifier.padding(16.dp)
                    )
                    1 -> GoogleBooksSearch(
                        onDismiss = onDismiss,
                        onConfirm = onConfirm,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun ManualBookEntry(
    onDismiss: () -> Unit,
    onConfirm: (title: String, author: String, totalPages: Int?, notes: String?) -> Unit,
    modifier: Modifier = Modifier
) {
    var title by remember { mutableStateOf("") }
    var author by remember { mutableStateOf("") }
    var totalPagesText by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }
    
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Title *") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            
            OutlinedTextField(
                value = author,
                onValueChange = { author = it },
                label = { Text("Author *") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            
            OutlinedTextField(
                value = totalPagesText,
                onValueChange = { totalPagesText = it },
                label = { Text("Total Pages") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
            
            OutlinedTextField(
                value = notes,
                onValueChange = { notes = it },
                label = { Text("Notes") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3,
                maxLines = 5
            )
        }
        
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
                    if (title.isNotBlank() && author.isNotBlank()) {
                        val totalPages = totalPagesText.toIntOrNull()
                        onConfirm(title, author, totalPages, notes)
                    }
                },
                enabled = title.isNotBlank() && author.isNotBlank()
            ) {
                Text("Add")
            }
        }
    }
}

@Composable
private fun GoogleBooksSearch(
    onDismiss: () -> Unit,
    onConfirm: (title: String, author: String, totalPages: Int?, notes: String?) -> Unit,
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }
    var searchResults by remember { mutableStateOf<List<GoogleBookItem>>(emptyList()) }
    var isSearching by remember { mutableStateOf(false) }
    var searchError by remember { mutableStateOf<String?>(null) }
    
    val scope = rememberCoroutineScope()
    val keyboardController = LocalSoftwareKeyboardController.current
    val googleBooksService = remember { GoogleBooksService() }
    
    fun performSearch() {
        if (searchQuery.isNotBlank()) {
            scope.launch {
                isSearching = true
                searchError = null
                
                val result = googleBooksService.searchBooks(searchQuery)
                result.fold(
                    onSuccess = { response ->
                        searchResults = response.items ?: emptyList()
                        isSearching = false
                    },
                    onFailure = { error ->
                        searchError = error.message ?: "Search failed"
                        isSearching = false
                    }
                )
            }
        }
    }
    
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            label = { Text("Search books...") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            trailingIcon = {
                IconButton(onClick = { 
                    keyboardController?.hide()
                    performSearch() 
                }) {
                    Icon(Icons.Default.Search, contentDescription = "Search")
                }
            },
            keyboardActions = KeyboardActions(
                onSearch = {
                    keyboardController?.hide()
                    performSearch()
                }
            ),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search)
        )
        
        when {
            isSearching -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            
            searchError != null -> {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Text(
                        text = "Error: $searchError",
                        modifier = Modifier.padding(16.dp),
                        color = MaterialTheme.colorScheme.onErrorContainer
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
            }
            
            searchResults.isEmpty() && searchQuery.isNotBlank() -> {
                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "No books found for \"$searchQuery\"",
                        modifier = Modifier.padding(16.dp),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
            }
            
            searchResults.isNotEmpty() -> {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(searchResults) { book ->
                        GoogleBookItem(
                            book = book,
                            onClick = {
                                val title = book.volumeInfo.title ?: ""
                                val author = book.volumeInfo.authors?.joinToString(", ") ?: ""
                                val totalPages = book.volumeInfo.pageCount
                                val notes = book.volumeInfo.description
                                
                                if (title.isNotBlank() && author.isNotBlank()) {
                                    onConfirm(title, author, totalPages, notes)
                                }
                            }
                        )
                    }
                }
            }
            
            else -> {
                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Enter a book title, author, or ISBN to search",
                        modifier = Modifier.padding(16.dp),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
            }
        }
        
        // Cancel button at the bottom
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    }
}

@Composable
private fun GoogleBookItem(
    book: GoogleBookItem,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = book.volumeInfo.title ?: "Unknown Title",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            
            if (!book.volumeInfo.authors.isNullOrEmpty()) {
                Text(
                    text = book.volumeInfo.authors.joinToString(", "),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            
            if (book.volumeInfo.pageCount != null) {
                Text(
                    text = "${book.volumeInfo.pageCount} pages",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
