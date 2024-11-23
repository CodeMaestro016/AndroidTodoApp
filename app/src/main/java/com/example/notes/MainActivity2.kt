package com.example.notes

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat


import android.app.NotificationChannel
import android.app.NotificationManager



class MainActivity2 : AppCompatActivity() {

    lateinit var timerView: TextView
    lateinit var startButton: Button
    lateinit var stopButton: Button
    lateinit var resetButton: Button

    private var timeInSec: Long = 0

    private val stopwatch = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            timeInSec = intent?.getLongExtra("timeInSec", 0) ?: 0
            updateTimer()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)

        checkNotificationPer()
        createNotification()

        timerView = findViewById(R.id.timer)
        startButton = findViewById(R.id.startButton)
        stopButton = findViewById(R.id.stopButton)
        resetButton = findViewById(R.id.resetButton)

        startButton.setOnClickListener {
            startService(Intent(this, StopwatchService::class.java))
        }

        stopButton.setOnClickListener {
            stopService(Intent(this, StopwatchService::class.java))
        }

        resetButton.setOnClickListener {
            stopService(Intent(this, StopwatchService::class.java))
            timeInSec = 0
            getSharedPreferences("StopwatchPrefs", Context.MODE_PRIVATE)
                // Reset saved time
                .edit().putLong("savedTime", 0).apply()
            updateTimer()
        }
    }

    override fun onResume() {
        super.onResume()
       // registerReceiver(stopwatch, IntentFilter("STOPWATCH_UPDATE"))
        timeInSec = getSharedPreferences("StopwatchPrefs", Context.MODE_PRIVATE)
            .getLong("savedTime", 0) // Restore saved time
        updateTimer()
    }

    override fun onPause() {
        super.onPause()
        unregisterReceiver(stopwatch)
    }

    private fun updateTimer() {
        val min = (timeInSec / 60).toInt()
        val sec = (timeInSec % 60).toInt()
        val timeFormatted = String.format("%02d:%02d", min, sec)
        timerView.text = timeFormatted
    }


// Display notification functions
    private fun checkNotificationPer() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.POST_NOTIFICATIONS), 1001)
            }
        }
    }

    private fun createNotification() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Stopwatch Notifications"
            val descriptionText = "Notifications for stopwatch reminders"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel("stopwatch_channel", name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}
