package com.tobaxiom.placebo.util

import android.Manifest
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.tobaxiom.placebo.MainActivity
import com.tobaxiom.placebo.StreaksApplication
import java.time.LocalTime

object NotificationHelper {

    fun showNotification(context: Context, streakId: Int, streakName: String) {
        val timeLeft = 24 - LocalTime.now().hour
        
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent: PendingIntent = PendingIntent.getActivity(
            context, streakId, intent, PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(context, StreaksApplication.CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info) 
            .setContentTitle("Streak Reminder")
            .setContentText("Complete $streakName, $timeLeft hrs left!")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        with(NotificationManagerCompat.from(context)) {
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
                notify(streakId, builder.build())
            }
        }
    }
}
