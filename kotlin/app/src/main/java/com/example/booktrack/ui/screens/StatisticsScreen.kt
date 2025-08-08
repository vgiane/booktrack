package com.example.booktrack.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.booktrack.ui.viewmodel.StatisticsViewModel
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatisticsScreen(
    statisticsViewModel: StatisticsViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by statisticsViewModel.uiState.collectAsStateWithLifecycle()
    var selectedTab by remember { mutableStateOf(0) }
    
    Column(
        modifier = modifier.fillMaxSize()
    ) {
        TopAppBar(
            title = { Text("Statistics") }
        )
        
        if (uiState.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                // Total reading time card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Total Reading Time",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = statisticsViewModel.formatTime(uiState.totalReadingTimeSeconds),
                            style = MaterialTheme.typography.headlineLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Tab selector
                TabRow(selectedTabIndex = selectedTab) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        text = { Text("Last 7 Days") }
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = { 
                            selectedTab = 1
                            statisticsViewModel.loadWeeklyData()
                        },
                        text = { Text("Weekly") }
                    )
                    Tab(
                        selected = selectedTab == 2,
                        onClick = { 
                            selectedTab = 2
                            statisticsViewModel.loadMonthlyData()
                        },
                        text = { Text("Monthly") }
                    )
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Chart content
                when (selectedTab) {
                    0 -> DailyChart(
                        data = uiState.last7DaysData,
                        formatTime = statisticsViewModel::formatTime
                    )
                    1 -> WeeklyChart(
                        data = uiState.weeklyData,
                        formatTime = statisticsViewModel::formatTime
                    )
                    2 -> MonthlyChart(
                        data = uiState.monthlyData,
                        formatTime = statisticsViewModel::formatTime
                    )
                }
            }
        }
    }
}

@Composable
private fun DailyChart(
    data: List<com.example.booktrack.ui.viewmodel.DayReadingData>,
    formatTime: (Long) -> String
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(data) { dayData ->
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = dayData.date.format(DateTimeFormatter.ofPattern("MMM dd, EEE")),
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Text(
                        text = formatTime(dayData.readingTimeSeconds),
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}

@Composable
private fun WeeklyChart(
    data: List<com.example.booktrack.ui.viewmodel.WeekReadingData>,
    formatTime: (Long) -> String
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(data) { weekData ->
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "${weekData.weekStart.format(DateTimeFormatter.ofPattern("MMM dd"))} - ${weekData.weekEnd.format(DateTimeFormatter.ofPattern("MMM dd, yyyy"))}",
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = formatTime(weekData.readingTimeSeconds),
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}

@Composable
private fun MonthlyChart(
    data: List<com.example.booktrack.ui.viewmodel.MonthReadingData>,
    formatTime: (Long) -> String
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(data) { monthData ->
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = monthData.month.format(DateTimeFormatter.ofPattern("MMMM yyyy")),
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Text(
                        text = formatTime(monthData.readingTimeSeconds),
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}
