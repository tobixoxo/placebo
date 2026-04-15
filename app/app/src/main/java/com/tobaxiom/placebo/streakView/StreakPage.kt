package com.tobaxiom.placebo.streakView

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tobaxiom.placebo.data.Completion
import com.tobaxiom.placebo.data.Streak
import java.time.LocalDate
import java.time.ZoneOffset
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StreakPage(
    streak: Streak,
    completions: List<Completion>,
    currentStreak: Int,
    longestStreak: Int,
    onMarkToday: () -> Unit,
    onUnmarkToday: () -> Unit,
    onToggleReminder: (Boolean, Long?) -> Unit,
    onBackClicked: () -> Unit
) {
    val today = LocalDate.now().atStartOfDay().toEpochSecond(ZoneOffset.UTC) * 1000
    val isTodayMarked = completions.any { it.date == today }
    var showTimePicker by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = streak.name,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClicked) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = {
                        if (streak.isReminderEnabled) {
                            onToggleReminder(false, null)
                        } else {
                            showTimePicker = true
                        }
                    }) {
                        Icon(
                            imageVector = if (streak.isReminderEnabled) Icons.Default.Notifications else Icons.Default.NotificationsOff,
                            contentDescription = "Toggle Reminder"
                        )
                    }
                }
            )
        },
        bottomBar = {
            BottomAppBar(
                containerColor = MaterialTheme.colorScheme.surface
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    val buttonColors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )

                    if (isTodayMarked) {
                        Button(
                            onClick = onUnmarkToday,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp),
                            colors = buttonColors
                        ) {
                            Text("Unmark Today")
                        }
                    } else {
                        Button(
                            onClick = onMarkToday,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp),
                            colors = buttonColors
                        ) {
                            Text("Mark Today")
                        }
                    }
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            StreakCalendar(completions = completions, modifier = Modifier.padding(bottom = 16.dp))

            Card(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    StatItem(label = "Current Streak", value = currentStreak.toString())
                    StatItem(label = "Longest Streak", value = longestStreak.toString())
                }
            }
            
            if (streak.isReminderEnabled && streak.reminderTime != null) {
                val calendar = Calendar.getInstance().apply { timeInMillis = streak.reminderTime }
                val hour = calendar.get(Calendar.HOUR_OF_DAY)
                val minute = calendar.get(Calendar.MINUTE)
                Text(
                    text = "Reminder set for ${String.format("%02d:%02d", hour, minute)}",
                    modifier = Modifier.padding(top = 8.dp),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }

    if (showTimePicker) {
        val timePickerState = rememberTimePickerState()
        AlertDialog(
            onDismissRequest = { showTimePicker = false },
            confirmButton = {
                Button(onClick = {
                    val calendar = Calendar.getInstance().apply {
                        set(Calendar.HOUR_OF_DAY, timePickerState.hour)
                        set(Calendar.MINUTE, timePickerState.minute)
                        set(Calendar.SECOND, 0)
                        set(Calendar.MILLISECOND, 0)
                    }
                    onToggleReminder(true, calendar.timeInMillis)
                    showTimePicker = false
                }) {
                    Text("OK")
                }
            },
            dismissButton = {
                Button(onClick = { showTimePicker = false }) {
                    Text("Cancel")
                }
            },
            text = {
                TimePicker(state = timePickerState)
            }
        )
    }
}

@Composable
fun StatItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = label, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
        Text(text = value, fontSize = 24.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
    }
}
