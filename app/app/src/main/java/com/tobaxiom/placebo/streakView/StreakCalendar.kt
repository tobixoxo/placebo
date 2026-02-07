package com.tobaxiom.placebo.streakView

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.unit.dp
import com.tobaxiom.placebo.data.Completion
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

@Composable
fun StreakCalendar(completions: List<Completion>, modifier: Modifier = Modifier) {
    val today = LocalDate.now()
    val completionDates = completions.map { Instant.ofEpochMilli(it.date).atZone(ZoneId.systemDefault()).toLocalDate() }.toSet()

    // We want to show a 5-week (35 day) calendar grid.
    // Let's determine the start date for this grid.
    val todayDayIndex = today.dayOfWeek.value % 7 // Sunday is 0, Monday is 1, etc.
    val gridEndDate = today.plusDays(6 - todayDayIndex.toLong())
    val gridStartDate = gridEndDate.minusDays(34)

    Column(modifier = modifier.fillMaxWidth()) {
        Text("Contribution Graph (Last 5 Weeks)", modifier = Modifier.padding(bottom = 8.dp))

        (0 until 5).forEach { week ->
            Row(modifier = Modifier.fillMaxWidth()) {
                (0 until 7).forEach { day ->
                    val date = gridStartDate.plusDays((week * 7 + day).toLong())
                    val isMarked = date in completionDates

                    val color = when {
                        date.isAfter(today) -> Color.Transparent
                        isMarked -> Color(0xFF4CAF50)
                        else -> Color.LightGray
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(1f)
                            .padding(2.dp)
                            .clip(RectangleShape)
                            .background(color)
                    )
                }
            }
        }
    }
}
