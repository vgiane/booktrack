package com.example.booktrack.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.booktrack.ui.viewmodels.BookViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.Instant
import java.time.ZoneId
import java.time.DayOfWeek
import java.time.temporal.TemporalAdjusters
import java.time.YearMonth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatisticsScreen(
    viewModel: BookViewModel
) {
    val readingLogs by viewModel.allReadingLogs.collectAsState()
    val dailyGoal by viewModel.dailyGoal.collectAsState(initial = 60) // Default to 60 if not set

    // Calculate statistics
    val totalReadingTime = readingLogs.sumOf { it.duration } / 60
    val today = LocalDate.now()
    val todayReadingTime = readingLogs
        .filter {
            val logDate = Instant.ofEpochMilli(it.date).atZone(ZoneId.systemDefault()).toLocalDate()
            logDate == today
        }
        .sumOf { it.duration } / 60

    // Calculate This Week's Progress (Monday - Sunday)
    val startOfWeek = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
    val weeklyReadingTime = readingLogs
        .filter {
            val logDate = Instant.ofEpochMilli(it.date).atZone(ZoneId.systemDefault()).toLocalDate()
            !logDate.isBefore(startOfWeek) && !logDate.isAfter(today) // Count from Monday up to today
        }
        .sumOf { it.duration } / 60
    val weeklyGoal = (dailyGoal ?: 60) * 7

    // Calculate This Month's Progress
    val currentYearMonth = YearMonth.from(today)
    val startOfMonth = today.with(TemporalAdjusters.firstDayOfMonth())
    val monthlyReadingTime = readingLogs
        .filter {
            val logDate = Instant.ofEpochMilli(it.date).atZone(ZoneId.systemDefault()).toLocalDate()
            !logDate.isBefore(startOfMonth) && !logDate.isAfter(today) // Count from start of month up to today
        }
        .sumOf { it.duration } / 60
    val daysInMonth = currentYearMonth.lengthOfMonth()
    val monthlyGoal = (dailyGoal ?: 60) * daysInMonth

    // Calculate last 7 days data
    val last7Days = (0..6).map { daysAgo ->
        val date = today.minusDays(daysAgo.toLong())
        val dayReadingTime = readingLogs
            .filter {
                val logDate = Instant.ofEpochMilli(it.date).atZone(ZoneId.systemDefault()).toLocalDate()
                logDate == date
            }
            .sumOf { it.duration } / 60
        Pair(date, dayReadingTime)
    }.reversed()

    val maxDailyTime = last7Days.maxOfOrNull { it.second } ?: 1

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // Header
        Text(
            text = "Statistics",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        // Today's Progress Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Today's Progress",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Text(
                    text = "$todayReadingTime / ${dailyGoal ?: 60} minutes",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (todayReadingTime >= (dailyGoal ?: 60))
                        MaterialTheme.colorScheme.primary
                    else
                        MaterialTheme.colorScheme.onSurface
                )

                val progress = if ((dailyGoal ?: 0) > 0) {
                    (todayReadingTime.toFloat() / (dailyGoal ?: 60)).coerceAtMost(1f)
                } else 0f

                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                        .height(8.dp),
                    color = if (progress >= 1f)
                        MaterialTheme.colorScheme.primary
                    else
                        MaterialTheme.colorScheme.secondary,
                    trackColor = ProgressIndicatorDefaults.linearTrackColor,
                )
            }
        }

        // This Week's Progress Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "This Week's Progress",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Text(
                    text = "$weeklyReadingTime / $weeklyGoal minutes",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (weeklyReadingTime >= weeklyGoal)
                        MaterialTheme.colorScheme.primary
                    else
                        MaterialTheme.colorScheme.onSurface
                )

                val weeklyProgressAmount = if (weeklyGoal > 0) {
                    (weeklyReadingTime.toFloat() / weeklyGoal).coerceAtMost(1f)
                } else 0f

                LinearProgressIndicator(
                    progress = { weeklyProgressAmount },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                        .height(8.dp),
                    color = if (weeklyProgressAmount >= 1f)
                        MaterialTheme.colorScheme.primary
                    else
                        MaterialTheme.colorScheme.secondary,
                    trackColor = ProgressIndicatorDefaults.linearTrackColor,
                )
            }
        }

        // This Month's Progress Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "This Month's Progress",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Text(
                    text = "$monthlyReadingTime / $monthlyGoal minutes",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (monthlyReadingTime >= monthlyGoal)
                        MaterialTheme.colorScheme.primary
                    else
                        MaterialTheme.colorScheme.onSurface
                )

                val monthlyProgressAmount = if (monthlyGoal > 0) {
                    (monthlyReadingTime.toFloat() / monthlyGoal).coerceAtMost(1f)
                } else 0f

                LinearProgressIndicator(
                    progress = { monthlyProgressAmount },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                        .height(8.dp),
                    color = if (monthlyProgressAmount >= 1f)
                        MaterialTheme.colorScheme.primary
                    else
                        MaterialTheme.colorScheme.secondary,
                    trackColor = ProgressIndicatorDefaults.linearTrackColor,
                )
            }
        }

        // Total Reading Time Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Total Reading Time",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                val hours = totalReadingTime / 60
                val minutes = totalReadingTime % 60

                Text(
                    text = if (hours > 0) "${hours}h ${minutes}m" else "${minutes}m",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }

        // Last 7 Days Chart Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Last 7 Days",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                // Simple bar chart
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.Bottom
                ) {
                    last7Days.forEach { (date, minutes) ->
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.weight(1f)
                        ) {
                            // Bar
                            val barHeight = if (maxDailyTime > 0) {
                                (minutes.toFloat() / maxDailyTime * 150).dp
                            } else 0.dp

                            Box(
                                modifier = Modifier
                                    .width(24.dp)
                                    .height(barHeight.coerceAtLeast(2.dp))
                                    .background(
                                        if (minutes > 0)
                                            MaterialTheme.colorScheme.primary
                                        else
                                            MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                                        shape = MaterialTheme.shapes.small
                                    )
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            // Date label
                            Text(
                                text = date.format(DateTimeFormatter.ofPattern("MMM d")),
                                fontSize = 10.sp,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.width(40.dp)
                            )

                            // Minutes label
                            if (minutes > 0) {
                                Text(
                                    text = "${minutes}m",
                                    fontSize = 8.sp,
                                    color = MaterialTheme.colorScheme.primary,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.width(40.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        // Reading Sessions Count
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Reading Sessions",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Text(
                    text = "${readingLogs.size} sessions",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}
