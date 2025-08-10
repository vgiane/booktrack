package com.example.booktrack.services

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Binder
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.example.booktrack.MainActivity // Assuming MainActivity is your entry point
import com.example.booktrack.R
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.util.Locale

class TimerService : Service() {

    private val binder = TimerBinder()
    private var timerJob: Job? = null
    private val _elapsedTime = MutableStateFlow(0L) // in milliseconds
    val elapsedTime: StateFlow<Long> = _elapsedTime

    private val _isTimerRunning = MutableStateFlow(false)
    val isTimerRunning: StateFlow<Boolean> = _isTimerRunning

    private var currentBookTitle: String = "Reading"

    private val serviceScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    companion object {
        const val ACTION_START = "com.example.booktrack.services.action.START"
        const val ACTION_PAUSE = "com.example.booktrack.services.action.PAUSE"
        const val ACTION_RESUME = "com.example.booktrack.services.action.RESUME"
        const val ACTION_STOP = "com.example.booktrack.services.action.STOP"
        const val EXTRA_BOOK_TITLE = "com.example.booktrack.services.extra.BOOK_TITLE"

        private const val NOTIFICATION_ID = 1
        private const val NOTIFICATION_CHANNEL_ID = "TimerServiceChannel"
    }

    inner class TimerBinder : Binder() {
        fun getService(): TimerService = this@TimerService
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val action = intent?.action
        currentBookTitle = intent?.getStringExtra(EXTRA_BOOK_TITLE) ?: currentBookTitle

        when (action) {
            ACTION_START -> startTimer()
            ACTION_PAUSE -> pauseTimer()
            ACTION_RESUME -> resumeTimer()
            ACTION_STOP -> stopTimerService()
        }
        return START_NOT_STICKY
    }

    private fun startTimer() {
        if (_isTimerRunning.value) return // Already running

        _elapsedTime.value = 0L // Reset or load persisted time if needed
        timerJob?.cancel() // Cancel any existing job first
        timerJob = serviceScope.launch {
            _isTimerRunning.value = true
            try {
                while (isActive) { // Use isActive from the coroutine scope
                    delay(1000)
                    _elapsedTime.value += 1000
                    updateNotification(_elapsedTime.value)
                }
            } finally {
                // Ensure isTimerRunning is false when the coroutine stops for any reason
                if (coroutineContext.isActive) { // Only set to false if this job is the one controlling it
                    _isTimerRunning.value = false
                }
            }
        }
        startForeground(NOTIFICATION_ID, createNotification(_elapsedTime.value))
    }

    private fun pauseTimer() {
        timerJob?.cancel() // This will trigger the finally block in the job, setting _isTimerRunning to false.
        // If job was not active, _isTimerRunning should already be false.
        // For safety, explicitly set it if the job was active before cancelling.
        if (_isTimerRunning.value) {
           _isTimerRunning.value = false
        }
        updateNotification(_elapsedTime.value, isPaused = true)
        stopForeground(false)
    }

    private fun resumeTimer() {
         if (_isTimerRunning.value) return // Already running

        timerJob?.cancel() // Cancel any existing job first (e.g., if called after pause)
        timerJob = serviceScope.launch {
            _isTimerRunning.value = true
            try {
                while (isActive) { // Use isActive from the coroutine scope
                    delay(1000)
                    _elapsedTime.value += 1000
                    updateNotification(_elapsedTime.value)
                }
            } finally {
                 if (coroutineContext.isActive) {
                    _isTimerRunning.value = false
                }
            }
        }
        startForeground(NOTIFICATION_ID, createNotification(_elapsedTime.value))
    }


    private fun stopTimerService() {
        timerJob?.cancel() // This will trigger the finally block.
        _isTimerRunning.value = false // Explicitly set
        _elapsedTime.value = 0L // Reset timer
        stopForeground(true) // True removes the notification
        stopSelf()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                "Reading Timer Channel",
                NotificationManager.IMPORTANCE_LOW
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(serviceChannel)
        }
    }

    private fun createNotification(timeInMillis: Long, isPaused: Boolean = false): Notification {
        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntentFlags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        } else {
            PendingIntent.FLAG_UPDATE_CURRENT
        }
        val pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, pendingIntentFlags)

        val statusText = when {
            _isTimerRunning.value -> "Running"
            isPaused -> "Paused" 
            else -> "Stopped"
        }
        val formattedTime = formatTime(timeInMillis)

        return NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            .setContentTitle(currentBookTitle)
            .setContentText("Timer: $formattedTime - $statusText")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentIntent(pendingIntent)
            .setOnlyAlertOnce(true)
            .setOngoing(true)
            .build()
    }

    private fun updateNotification(timeInMillis: Long, isPaused: Boolean = false) {
        val notification = createNotification(timeInMillis, isPaused = isPaused || !_isTimerRunning.value)
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(NOTIFICATION_ID, notification)
    }

    private fun formatTime(milliseconds: Long): String {
        val totalSeconds = milliseconds / 1000
        val hours = totalSeconds / 3600
        val minutes = (totalSeconds % 3600) / 60
        val seconds = totalSeconds % 60
        return if (hours > 0) {
            String.format(Locale.getDefault(), "%02d:%02d:%02d", hours, minutes, seconds)
        } else {
            String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds)
        }
    }

    override fun onBind(intent: Intent): IBinder = binder

    override fun onDestroy() {
        super.onDestroy()
        timerJob?.cancel()
        serviceScope.cancel()
        _isTimerRunning.value = false
    }
}
