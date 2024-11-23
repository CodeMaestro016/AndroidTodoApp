package com.example.notes

import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import androidx.core.app.NotificationCompat
import android.content.SharedPreferences

class StopwatchService : Service() {

    private var timeInSec: Long = 0
    private var minutesPassed: Long = 0
    private val handler = Handler(Looper.getMainLooper())
    private lateinit var runnable: Runnable
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate() {
        super.onCreate()
        sharedPreferences = getSharedPreferences("StopwatchPrefs", Context.MODE_PRIVATE)
        timeInSec = sharedPreferences.getLong("savedTime", 0)
        minutesPassed = timeInSec / 60

        runnable = object : Runnable {
            override fun run() {
                timeInSec++
                val intent = Intent("STOPWATCH_UPDATE")
                intent.putExtra("timeInSec", timeInSec)
                sendBroadcast(intent)

                // Check if another minute has passed
                if (timeInSec % 60 == 0L) {
                    minutesPassed = timeInSec / 60
                    sendMinuteReminder(minutesPassed)
                }

                handler.postDelayed(this, 1000) // Continue update every second
            }
        }
        // Start the timer
        handler.post(runnable)
    }

    private fun sendMinuteReminder(minutes: Long) {
        val notificationIntent = Intent(this, MainActivity2::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

        val notification = NotificationCompat.Builder(this, "stopwatch_channel")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Stopwatch Reminder")
            .setContentText("$minutes minute${if (minutes > 1) "s" else ""} have passed!")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(minutes.toInt(), notification)
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(runnable) // Stop the timer
        sharedPreferences.edit().putLong("savedTime", timeInSec).apply() // Save current time
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}
