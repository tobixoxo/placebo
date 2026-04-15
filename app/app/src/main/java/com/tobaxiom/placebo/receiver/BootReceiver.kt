package com.tobaxiom.placebo.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.tobaxiom.placebo.StreaksApplication
import com.tobaxiom.placebo.util.ReminderManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class BootReceiver : BroadcastReceiver() {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            val app = context.applicationContext as StreaksApplication
            val repository = app.repository

            scope.launch {
                val activeStreaks = repository.activeStreaks.first()
                for (streak in activeStreaks) {
                    if (streak.isReminderEnabled && streak.reminderTime != null) {
                        ReminderManager.scheduleReminder(context, streak.id, streak.reminderTime)
                    }
                }
            }
        }
    }
}
