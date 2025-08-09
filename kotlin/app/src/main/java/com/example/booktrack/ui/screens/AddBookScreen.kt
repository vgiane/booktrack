package com.example.booktrack.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.booktrack.data.database.Book
import com.example.booktrack.data.database.BookStatus

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddBookScreen(
    onSave: (Book) -> Unit,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var title by remember { mutableStateOf("") }
    var author by remember { mutableStateOf("") }
    var totalPages by remember { mutableStateOf("") }
    var coverImage by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }
    
    var titleError by remember { mutableStateOf(false) }
    var authorError by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add Book") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
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
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = title,
                onValueChange = { 
                    title = it
                    titleError = false
                },
                label = { Text("Title *") },
                isError = titleError,
                supportingText = if (titleError) { { Text("Title is required") } } else null,
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = author,
                onValueChange = { 
                    author = it
                    authorError = false
                },
                label = { Text("Author *") },
                isError = authorError,
                supportingText = if (authorError) { { Text("Author is required") } } else null,
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = totalPages,
                onValueChange = { totalPages = it },
                label = { Text("Total Pages") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = coverImage,
                onValueChange = { coverImage = it },
                label = { Text("Cover Image URL") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = notes,
                onValueChange = { notes = it },
                label = { Text("Notes") },
                minLines = 3,
                maxLines = 5,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    // Validate input
                    titleError = title.isBlank()
                    authorError = author.isBlank()
                    
                    if (!titleError && !authorError) {
                        val book = Book(
                            title = title.trim(),
                            author = author.trim(),
                            totalPages = totalPages.toIntOrNull(),
                            coverImage = coverImage.ifBlank { null },
                            status = BookStatus.ACTIVE,
                            notes = notes.ifBlank { null }
                        )
                        onSave(book)
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Save Book")
            }
        }
    }
}
