package com.tobaxiom.placebo.streakView

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.tobaxiom.placebo.data.Streak
import com.tobaxiom.placebo.details.MonthlyCalendar
import java.time.Instant
import java.time.LocalDate
import java.time.YearMonth
import java.time.ZoneId
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StreakPage(
    streak: Streak,
    isCompletedToday: Boolean,
    completedDates: Set<LocalDate>,
    currentStreak: Int,
    longestStreak: Int,
    onMarkToday: () -> Unit,
    onUnmarkToday: () -> Unit,
    onMonthChange: (YearMonth) -> Unit,
    onToggleReminder: (Boolean, Long?) -> Unit,
    onBackClicked: () -> Unit
) {
    var currentMonth by remember { mutableStateOf(YearMonth.now()) }
    var showTimePicker by remember { mutableStateOf(false) }
    val haptic = LocalHapticFeedback.current

    // Boundaries: from habit creation month to today
    val minMonth = remember(streak.startDate) {
        if (streak.startDate > 0) {
            YearMonth.from(Instant.ofEpochMilli(streak.startDate).atZone(ZoneId.systemDefault()).toLocalDate())
        } else {
            YearMonth.now()
        }
    }
    val maxMonth = YearMonth.now()

    LaunchedEffect(currentMonth) {
        onMonthChange(currentMonth)
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = streak.name,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {
                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        onBackClicked()
                    }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
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
                containerColor = MaterialTheme.colorScheme.surface,
                contentPadding = WindowInsets.safeDrawing.asPaddingValues()
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    val buttonColors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )

                    if (isCompletedToday) {
                        Button(
                            onClick = {
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                onUnmarkToday()
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp),
                            colors = buttonColors
                        ) {
                            Text("Unmark Today")
                        }
                    } else {
                        Button(
                            onClick = {
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                onMarkToday()
                            },
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
                .padding(horizontal = 16.dp)
                .fillMaxSize()
        ) {
            MonthlyCalendar(
                currentMonth = currentMonth,
                minMonth = minMonth,
                maxMonth = maxMonth,
                completedDates = completedDates,
                onMonthChange = { currentMonth = it },
                modifier = Modifier.padding(bottom = 24.dp)
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                ),
                shape = MaterialTheme.shapes.extraLarge
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    StatItem(label = "Current", value = currentStreak.toString())
                    StatItem(label = "Longest", value = longestStreak.toString())
                }
            }
            
            if (streak.isReminderEnabled && streak.reminderTime != null) {
                val calendar = Calendar.getInstance().apply { timeInMillis = streak.reminderTime }
                val hour = calendar.get(Calendar.HOUR_OF_DAY)
                val minute = calendar.get(Calendar.MINUTE)
                Text(
                    text = "Reminder set for ${String.format("%02d:%02d", hour, minute)}",
                    modifier = Modifier.padding(top = 16.dp, start = 8.dp),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }

    if (showTimePicker) {
        val timePickerState = rememberTimePickerState()
        AlertDialog(
            onDismissRequest = { 
                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                showTimePicker = false 
            },
            confirmButton = {
                Button(onClick = {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
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
                Button(onClick = { 
                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    showTimePicker = false 
                }) {
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
        Text(
            text = label, 
            style = MaterialTheme.typography.labelLarge, 
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value, 
            style = MaterialTheme.typography.displaySmall, 
            fontWeight = FontWeight.ExtraBold, 
            color = MaterialTheme.colorScheme.primary
        )
    }
}
