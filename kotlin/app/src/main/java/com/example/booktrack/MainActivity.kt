package com.example.booktrack

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.booktrack.data.database.BooktrackDatabase
import com.example.booktrack.data.repository.BooktrackRepository
import com.example.booktrack.ui.navigation.Screen
import com.example.booktrack.ui.navigation.bottomNavItems
import com.example.booktrack.ui.screens.*
import com.example.booktrack.ui.theme.BooktrackTheme
import com.example.booktrack.ui.viewmodel.*
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        val database = BooktrackDatabase.getDatabase(this)
        val repository = BooktrackRepository(database.bookDao(), database.readingLogDao())
        val viewModelFactory = ViewModelFactory(repository, this)
        
        setContent {
            BooktrackTheme {
                BooktrackApp(viewModelFactory = viewModelFactory)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BooktrackApp(
    viewModelFactory: ViewModelFactory,
    navController: NavHostController = rememberNavController()
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val scope = rememberCoroutineScope()
    
    val bookViewModel: BookViewModel = viewModel(factory = viewModelFactory)
    val timerViewModel: TimerViewModel = viewModel(factory = viewModelFactory)
    val statisticsViewModel: StatisticsViewModel = viewModel(factory = viewModelFactory)
    val settingsViewModel: SettingsViewModel = viewModel(factory = viewModelFactory)
    
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            if (currentRoute != null && !currentRoute.startsWith("timer")) {
                NavigationBar {
                    bottomNavItems.forEach { screen ->
                        NavigationBarItem(
                            icon = { Icon(screen.icon, contentDescription = screen.title) },
                            label = { Text(screen.title) },
                            selected = currentRoute == screen.route,
                            onClick = {
                                if (currentRoute != screen.route) {
                                    navController.navigate(screen.route) {
                                        popUpTo(navController.graph.startDestinationId) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
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
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.ActiveBooks.route) {
                ActiveBooksScreen(
                    bookViewModel = bookViewModel,
                    onNavigateToTimer = { bookId ->
                        navController.navigate(Screen.Timer.createRoute(bookId))
                    },
                    onNavigateToEditBook = { bookId ->
                        navController.navigate(Screen.EditBook.createRoute(bookId))
                    },
                    onNavigateToBookView = { bookId ->
                        navController.navigate(Screen.BookView.createRoute(bookId))
                    }
                )
            }
            
            composable(Screen.AllBooks.route) {
                AllBooksScreen(
                    bookViewModel = bookViewModel,
                    onNavigateToTimer = { bookId ->
                        navController.navigate(Screen.Timer.createRoute(bookId))
                    },
                    onNavigateToEditBook = { bookId ->
                        navController.navigate(Screen.EditBook.createRoute(bookId))
                    },
                    onNavigateToBookView = { bookId ->
                        navController.navigate(Screen.BookView.createRoute(bookId))
                    }
                )
            }
            
            composable(Screen.Statistics.route) {
                StatisticsScreen(statisticsViewModel = statisticsViewModel)
            }
            
            composable(Screen.Settings.route) {
                SettingsScreen(settingsViewModel = settingsViewModel)
            }
            
            composable(Screen.Timer.route) { backStackEntry ->
                val bookId = backStackEntry.arguments?.getString("bookId")?.toLongOrNull()
                
                if (bookId != null) {
                    var book by remember { mutableStateOf<com.example.booktrack.data.entity.Book?>(null) }
                    
                    LaunchedEffect(bookId) {
                        scope.launch {
                            book = bookViewModel.getBookById(bookId)
                        }
                    }
                    
                    book?.let { currentBook ->
                        TimerScreen(
                            book = currentBook,
                            timerViewModel = timerViewModel,
                            onNavigateBack = { navController.popBackStack() }
                        )
                    }
                } else {
                    // Handle invalid book ID
                    LaunchedEffect(Unit) {
                        navController.popBackStack()
                    }
                }
            }
            
            composable(Screen.EditBook.route) { backStackEntry ->
                val bookId = backStackEntry.arguments?.getString("bookId")?.toLongOrNull()
                
                if (bookId != null) {
                    var book by remember { mutableStateOf<com.example.booktrack.data.entity.Book?>(null) }
                    
                    LaunchedEffect(bookId) {
                        scope.launch {
                            book = bookViewModel.getBookById(bookId)
                        }
                    }
                    
                    book?.let { currentBook ->
                        EditBookScreen(
                            book = currentBook,
                            onNavigateBack = { navController.popBackStack() },
                            onSaveBook = { updatedBook ->
                                scope.launch {
                                    bookViewModel.updateBook(updatedBook)
                                }
                            },
                            onDeleteBook = {
                                scope.launch {
                                    bookViewModel.deleteBook(currentBook)
                                }
                            }
                        )
                    }
                } else {
                    // Handle invalid book ID
                    LaunchedEffect(Unit) {
                        navController.popBackStack()
                    }
                }
            }
            
            composable(Screen.BookView.route) { backStackEntry ->
                val bookId = backStackEntry.arguments?.getString("bookId")?.toLongOrNull()
                
                if (bookId != null) {
                    var book by remember { mutableStateOf<com.example.booktrack.data.entity.Book?>(null) }
                    
                    LaunchedEffect(bookId) {
                        scope.launch {
                            book = bookViewModel.getBookById(bookId)
                        }
                    }
                    
                    book?.let { currentBook ->
                        BookViewScreen(
                            book = currentBook,
                            bookViewModel = bookViewModel,
                            onNavigateBack = { navController.popBackStack() },
                            onNavigateToEdit = { editBookId ->
                                navController.navigate(Screen.EditBook.createRoute(editBookId))
                            },
                            onNavigateToTimer = { timerBookId ->
                                navController.navigate(Screen.Timer.createRoute(timerBookId))
                            }
                        )
                    }
                } else {
                    // Handle invalid book ID
                    LaunchedEffect(Unit) {
                        navController.popBackStack()
                    }
                }
            }
        }
    }
}