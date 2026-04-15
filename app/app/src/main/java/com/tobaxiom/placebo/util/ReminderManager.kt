package com.tobaxiom.placebo.util

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import com.tobaxiom.placebo.receiver.NotificationReceiver
import java.util.Calendar

object ReminderManager {

    fun scheduleReminder(context: Context, streakId: Int, reminderTimeMs: Long) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        
        // Check for exact alarm permission on Android 12+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (!alarmManager.canScheduleExactAlarms()) {
                // We cannot schedule exact alarms. 
                // In a production app, we should guide the user to settings.
                // For now, let's fallback to setAndAllowWhileIdle or just return.
                // Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
            }
        }

        val intent = Intent(context, NotificationReceiver::class.java).apply {
            putExtra("STREAK_ID", streakId)
        }
        
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            streakId, // Unique request code per streak
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val calendar = Calendar.getInstance().apply {
            timeInMillis = reminderTimeMs
            // Ensure we are scheduling for the future
            if (before(Calendar.getInstance())) {
                add(Calendar.DAY_OF_YEAR, 1)
            }
        }

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    calendar.timeInMillis,
                    pendingIntent
                )
            } else {
                alarmManager.setExact(
                    AlarmManager.RTC_WAKEUP,
                    calendar.timeInMillis,
                    pendingIntent
                )
            }
        } catch (e: SecurityException) {
            // Handle lack of permission on Android 14+
        }
    }

    fun cancelReminder(context: Context, streakId: Int) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, NotificationReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            streakId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(pendingIntent)
    }
}
