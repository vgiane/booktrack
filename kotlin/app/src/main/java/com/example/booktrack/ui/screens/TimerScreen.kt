package com.example.booktrack.ui.screens

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.example.booktrack.data.database.Book
import com.example.booktrack.services.TimerService
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimerScreen(
    book: Book,
    onSaveSession: (Long, Int?) -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var timerService by remember { mutableStateOf<TimerService?>(null) }
    var isBound by remember { mutableStateOf(false) }

    val elapsedTime by timerService?.elapsedTime?.collectAsState() ?: remember { mutableLongStateOf(0L) }
    val isTimerRunning by timerService?.isTimerRunning?.collectAsState() ?: remember { mutableStateOf(false) }

    var showSaveDialog by remember { mutableStateOf(false) }
    var notes by remember { mutableStateOf(book.notes ?: "") }

    val serviceConnection = remember {
        object : ServiceConnection {
            override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
                val binder = service as TimerService.TimerBinder
                timerService = binder.getService()
                isBound = true
                // Optionally, start the timer immediately if not already running,
                // or if it was previously running and screen was off.
                // For now, we assume service is started/managed externally or by initial actions.
            }

            override fun onServiceDisconnected(name: ComponentName?) {
                timerService = null
                isBound = false
            }
        }
    }

    // Bind and unbind to service, start service
    LaunchedEffect(key1 = book.id) { // Use a key that ensures this runs once per screen instance
        // Start the service
        Intent(context, TimerService::class.java).also { intent ->
            intent.putExtra(TimerService.EXTRA_BOOK_TITLE, book.title)
            // Decide if you want to auto-start or wait for play button
            // context.startService(intent.apply { action = TimerService.ACTION_START })
            // For now, let's assume it should start and run immediately
            context.startForegroundService(intent.apply { action = TimerService.ACTION_START })
        }
        // Bind to the service
        Intent(context, TimerService::class.java).also { intent ->
            context.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
        }
    }

    // Lifecycle observer to unbind service
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner, serviceConnection) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_DESTROY) {
                if (isBound) {
                    context.unbindService(serviceConnection)
                    isBound = false
                }
                // Decide if the service should be stopped when the screen is destroyed
                // For now, we assume it keeps running until explicitly stopped by user.
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            // Ensure unbinding if not already done by ON_DESTROY
            if (isBound) {
                try {
                    context.unbindService(serviceConnection)
                } catch (e: IllegalArgumentException) {
                    // Service not registered, ignore
                }
                isBound = false
            }
        }
    }


    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Reading Timer") },
            )
        }
    ) { paddingValues ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Book info (simplified for brevity, original can be kept)
            Text(
                text = book.title,
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                textAlign = TextAlign.Center
            )
            Text(
                text = "by ${book.author}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            // Timer display
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Column(
                    modifier = Modifier.padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = formatTime(elapsedTime),
                        style = MaterialTheme.typography.displayLarge.copy(
                            fontSize = 48.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Text(
                        text = if (isTimerRunning) "Reading..." else "Paused",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }

            // Timer controls
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Cancel button
                OutlinedButton(
                    onClick = onCancel,
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Icon(Icons.Filled.Close, contentDescription = null)
                }

                // Play/Pause button
                Button(
                    onClick = {
                        if (isBound && timerService != null) {
                            val intent = Intent(context, TimerService::class.java)
                            if (isTimerRunning) {
                                intent.action = TimerService.ACTION_PAUSE
                            } else {
                                intent.putExtra(TimerService.EXTRA_BOOK_TITLE, book.title) // Ensure title is fresh
                                intent.action = TimerService.ACTION_RESUME // Or ACTION_START if it could be fully stopped
                            }
                            context.startService(intent)
                        }
                    }
                ) {
                    Icon(
                        if (isTimerRunning) Icons.Filled.PauseCircle else Icons.Filled.PlayArrow,
                        contentDescription = if (isTimerRunning) "Pause Timer" else "Resume Timer"
                    )
                }

                // Save Session button
                Button(
                    onClick = {
                        if (isBound && timerService != null) {
                            context.startService(Intent(context, TimerService::class.java).apply { action = TimerService.ACTION_PAUSE })
                            showSaveDialog = true
                        }
                    },
                    enabled = elapsedTime > 0, // Enable only if timer has run
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.secondary
                    ),
                ) {
                    Icon(Icons.Filled.Save, contentDescription = "Save")
                }
            }

            // Notes section
            OutlinedTextField(
                value = notes,
                onValueChange = { notes = it },
                label = { Text("Notes") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                maxLines = 5
            )
        }

        // Save session dialog
        if (showSaveDialog) {
            var pagesRead by remember { mutableStateOf("") }
            val currentElapsedTime = timerService?.elapsedTime?.value ?: 0L // Capture current time for dialog

            AlertDialog(
                onDismissRequest = {
                    // User dismissed dialog (e.g., by clicking outside or back button)
                    if (isBound && timerService != null) {
                         // Resume timer if it was running or explicitly paused for the dialog
                        context.startService(Intent(context, TimerService::class.java).apply {
                            putExtra(TimerService.EXTRA_BOOK_TITLE, book.title)
                            action = TimerService.ACTION_RESUME
                        })
                    }
                    showSaveDialog = false
                },
                title = { Text("Save Reading Session") },
                text = {
                    Column {
                        Text("Duration: ${formatTime(currentElapsedTime)}")
                        Spacer(modifier = Modifier.height(16.dp))
                        OutlinedTextField(
                            value = pagesRead,
                            onValueChange = { pagesRead = it },
                            label = { Text("Pages read (optional)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            if (isBound && timerService != null) {
                                onSaveSession(
                                    currentElapsedTime / 1000, // Convert to seconds
                                    pagesRead.toIntOrNull()
                                )
                                // Stop and reset the service after saving
                                context.startService(Intent(context, TimerService::class.java).apply { action = TimerService.ACTION_STOP })
                            }
                            showSaveDialog = false
                            // Optionally navigate back after saving: onCancel()
                        }
                    ) {
                        Text("Save")
                    }
                },
                dismissButton = {
                    TextButton(onClick = {
                        if (isBound && timerService != null) {
                           // Resume timer
                           context.startService(Intent(context, TimerService::class.java).apply {
                               putExtra(TimerService.EXTRA_BOOK_TITLE, book.title)
                               action = TimerService.ACTION_RESUME
                           })
                        }
                        showSaveDialog = false
                    }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}

private fun formatTime(milliseconds: Long): String {
    val totalSeconds = milliseconds / 1000
    val hours = totalSeconds / 3600
    val minutes = (totalSeconds % 3600) / 60
    val seconds = totalSeconds % 60
    
    return if (hours > 0) {
        String.format(Locale.getDefault(), "%02d:%02d:%02d", hours, minutes, seconds)    } else {
        String.format(Locale.getDefault(),"%02d:%02d", minutes, seconds)
    }
}
