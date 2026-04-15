package com.tobaxiom.placebo

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import com.tobaxiom.placebo.data.AppDatabase
import com.tobaxiom.placebo.data.StreaksRepository

class StreaksApplication : Application() {
    val database by lazy { AppDatabase.getDatabase(this) }
    val repository by lazy { StreaksRepository(database.streakDao(), database.completionDao()) }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Streak Reminders"
            val descriptionText = "Notifications to remind you to complete your streaks"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    companion object {
        const val CHANNEL_ID = "streak_reminders_channel"
    }
}
