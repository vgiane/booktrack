package com.example.booktrack.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
    object ActiveBooks : Screen("active_books", "Active", Icons.Default.Home)
    object AllBooks : Screen("all_books", "All Books", Icons.Default.List)
    object Statistics : Screen("statistics", "Statistics", Icons.Default.Info)
    object Settings : Screen("settings", "Settings", Icons.Default.Settings)
    object Timer : Screen("timer/{bookId}", "Timer", Icons.Default.Home) {
        fun createRoute(bookId: Long) = "timer/$bookId"
    }
    object EditBook : Screen("edit_book/{bookId}", "Edit Book", Icons.Default.Home) {
        fun createRoute(bookId: Long) = "edit_book/$bookId"
    }
    object BookView : Screen("book_view/{bookId}", "Book Details", Icons.Default.Home) {
        fun createRoute(bookId: Long) = "book_view/$bookId"
    }
}

val bottomNavItems = listOf(
    Screen.ActiveBooks,
    Screen.AllBooks,
    Screen.Statistics,
    Screen.Settings
)
