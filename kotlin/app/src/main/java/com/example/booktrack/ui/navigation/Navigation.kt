package com.example.booktrack.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Info
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String) {
    object ActiveBooks : Screen("active_books")
    object AllBooks : Screen("all_books")
    object Statistics : Screen("statistics")
    object Settings : Screen("settings")
    object AddBook : Screen("add_book")
    object BookDetails : Screen("book_details/{bookId}") {
        fun createRoute(bookId: Long) = "book_details/$bookId"
    }
    object ManageBook : Screen("manage_book/{bookId}") {
        fun createRoute(bookId: Long) = "manage_book/$bookId"
    }
    object Timer : Screen("timer/{bookId}") {
        fun createRoute(bookId: Long) = "timer/$bookId"
    }
}

data class BottomNavItem(
    val screen: Screen,
    val title: String,
    val icon: ImageVector
)

val bottomNavItems = listOf(
    BottomNavItem(Screen.ActiveBooks, "Active", Icons.Filled.Home),
    BottomNavItem(Screen.AllBooks, "All Books", Icons.AutoMirrored.Filled.List),
    BottomNavItem(Screen.Statistics, "Statistics", Icons.Filled.Info),
    BottomNavItem(Screen.Settings, "Settings", Icons.Filled.Settings)
)
