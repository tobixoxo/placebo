package com.tobaxiom.placebo.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.tobaxiom.placebo.StreaksApplication
import com.tobaxiom.placebo.util.NotificationHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.ZoneOffset

class NotificationReceiver : BroadcastReceiver() {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onReceive(context: Context, intent: Intent) {
        val pendingResult = goAsync()
        val app = context.applicationContext as StreaksApplication
        val repository = app.repository
        val streakId = intent.getIntExtra("STREAK_ID", -1)

        if (streakId == -1) {
            pendingResult.finish()
            return
        }

        scope.launch {
            try {
                // Fetch only the specific streak by ID from the database
                val streak = repository.getStreakById(streakId)
                if (streak != null && !streak.isArchived && streak.isReminderEnabled) {
                    val today = LocalDate.now().atStartOfDay().toEpochSecond(ZoneOffset.UTC) * 1000
                    val completions = repository.getCompletionsForStreak(streakId).first()
                    val isMarkedToday = completions.any { it.date == today }

                    if (!isMarkedToday) {
                        NotificationHelper.showNotification(context, streak.id, streak.name)
                    }
                }
            } finally {
                pendingResult.finish()
            }
        }
    }
}
