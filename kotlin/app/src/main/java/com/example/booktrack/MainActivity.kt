package com.example.booktrack

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.booktrack.data.database.AppDatabase
import com.example.booktrack.data.database.BookStatus
import com.example.booktrack.data.repository.BookRepository
import com.example.booktrack.ui.navigation.Screen
import com.example.booktrack.ui.navigation.bottomNavItems
import com.example.booktrack.ui.screens.ActiveBooksScreen
import com.example.booktrack.ui.screens.AddBookScreen
import com.example.booktrack.ui.screens.AllBooksScreen
import com.example.booktrack.ui.screens.ManageBookScreen
import com.example.booktrack.ui.screens.StatisticsScreen
import com.example.booktrack.ui.screens.SettingsScreen
import com.example.booktrack.ui.screens.TimerScreen
import com.example.booktrack.ui.theme.BooktrackTheme
import com.example.booktrack.ui.viewmodels.BookViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Initialize database and repository
        val database = AppDatabase.getDatabase(this)
        val repository = BookRepository(database.bookDao(), database.readingLogDao())
        val bookViewModel = BookViewModel(repository, this)
        
        setContent {
            BooktrackTheme {
                BooktrackApp(bookViewModel)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BooktrackApp(
    bookViewModel: BookViewModel
) {
    val navController = rememberNavController()
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStackEntry?.destination?.route
    
    val activeBooks by bookViewModel.activeBooks.collectAsState()
    val allBooks by bookViewModel.allBooks.collectAsState()
    val currentBook by bookViewModel.currentBook.collectAsState()
    val currentBookLogs by bookViewModel.currentBookLogs.collectAsState()

    Scaffold(
        bottomBar = {
            if (shouldShowBottomBar(currentRoute)) {
                NavigationBar {
                    bottomNavItems.forEach { item ->
                        NavigationBarItem(
                            icon = { Icon(item.icon, contentDescription = item.title) },
                            label = { Text(item.title) },
                            selected = currentRoute == item.screen.route,
                            onClick = {
                                navController.navigate(item.screen.route) {
                                    popUpTo(navController.graph.startDestinationId) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.ActiveBooks.route,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            composable(Screen.ActiveBooks.route) {
                ActiveBooksScreen(
                    books = activeBooks,
                    onAddBookClick = {
                        navController.navigate(Screen.AddBook.route)
                    },
                    onBookTimerClick = { book ->
                        if (bookViewModel.canStartTimer(book)) {
                            navController.navigate(Screen.Timer.createRoute(book.id))
                        }
                    },
                    onBookManageClick = { book ->
                        navController.navigate(Screen.ManageBook.createRoute(book.id))
                    }
                )
            }
            
            composable(Screen.AllBooks.route) {
                AllBooksScreen(
                    books = allBooks,
                    onAddBookClick = {
                        navController.navigate(Screen.AddBook.route)
                    },
                    onBookTimerClick = { book ->
                        if (bookViewModel.canStartTimer(book)) {
                            navController.navigate(Screen.Timer.createRoute(book.id))
                        }
                    },
                    onBookManageClick = { book ->
                        navController.navigate(Screen.ManageBook.createRoute(book.id))
                    }
                )
            }
            
            composable(Screen.AddBook.route) {
                AddBookScreen(
                    onSave = { book ->
                        bookViewModel.addBook(book)
                        navController.popBackStack()
                    },
                    onNavigateBack = {
                        navController.popBackStack()
                    }
                )
            }
            
            composable(Screen.Timer.route) { backStackEntry ->
                val bookId = backStackEntry.arguments?.getString("bookId")?.toLongOrNull()
                bookId?.let { id ->
                    LaunchedEffect(id) {
                        bookViewModel.loadBookDetails(id)
                    }
                    
                    currentBook?.let { book ->
                        TimerScreen(
                            book = book,
                            onSaveSession = { durationSeconds, pagesRead ->
                                bookViewModel.addReadingSession(id, durationSeconds, pagesRead)
                                navController.popBackStack()
                            },
                            onCancel = {
                                bookViewModel.clearCurrentBook()
                                navController.popBackStack()
                            }
                        )
                    }
                }
            }
            
            composable(Screen.ManageBook.route) { backStackEntry ->
                val bookId = backStackEntry.arguments?.getString("bookId")?.toLongOrNull()
                bookId?.let { id ->
                    LaunchedEffect(id) {
                        bookViewModel.loadBookDetails(id)
                    }
                    
                    currentBook?.let { book ->
                        ManageBookScreen(
                            book = book,
                            readingLogs = currentBookLogs,
                            onSave = { updatedBook ->
                                bookViewModel.updateBook(updatedBook)
                                navController.popBackStack()
                            },
                            onDelete = {
                                bookViewModel.deleteBook(book)
                                bookViewModel.clearCurrentBook()
                                navController.popBackStack()
                            },
                            onDeleteReadingLog = { readingLog ->
                                bookViewModel.deleteReadingLog(readingLog)
                            },
                            onNavigateBack = {
                                bookViewModel.clearCurrentBook()
                                navController.popBackStack()
                            }
                        )
                    }
                }
            }
            
            composable(Screen.Statistics.route) {
                StatisticsScreen(viewModel = bookViewModel)
            }
            
            composable(Screen.Settings.route) {
                SettingsScreen(viewModel = bookViewModel)
            }
        }
    }
}

private fun shouldShowBottomBar(currentRoute: String?): Boolean {
    return when {
        currentRoute == Screen.ActiveBooks.route -> true
        currentRoute == Screen.AllBooks.route -> true
        currentRoute == Screen.Statistics.route -> true
        currentRoute == Screen.Settings.route -> true
        else -> false
    }
}