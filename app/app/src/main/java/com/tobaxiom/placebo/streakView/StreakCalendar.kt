package com.tobaxiom.placebo.streakView

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.unit.dp
import com.tobaxiom.placebo.Streak
import java.time.LocalDate

@Composable
fun StreakCalendar(streak: Streak, modifier: Modifier = Modifier) {
    val today = LocalDate.now()

    // We want to show a 5-week (35 day) calendar grid.
    // Let's determine the start date for this grid.
    // The grid should end on the last day of the current week (Saturday).
    // DayOfWeek enum: MONDAY (1) to SUNDAY (7).
    // To make Sunday the first day of the week (index 0), we can use (day.value % 7).
    val todayDayIndex = today.dayOfWeek.value % 7 // Sunday is 0, Monday is 1, etc.

    // Calculate the end date of the grid (the upcoming Saturday).
    val gridEndDate = today.plusDays(6 - todayDayIndex.toLong())
    // Calculate the start date of the grid (34 days before the end date).
    val gridStartDate = gridEndDate.minusDays(34)

    Column(modifier = modifier.fillMaxWidth()) {
        Text("Contribution Graph (Last 5 Weeks)", modifier = Modifier.padding(bottom = 8.dp))

        // Create 5 rows, one for each week.
        (0 until 5).forEach { week ->
            Row(modifier = Modifier.fillMaxWidth()) {
                // Create 7 cells in each row, one for each day.
                (0 until 7).forEach { day ->
                    val date = gridStartDate.plusDays((week * 7 + day).toLong())
                    val isMarked = date in streak.markedDays

                    val color = when {
                        // Future dates are transparent
                        date.isAfter(today) -> Color.Transparent
                        // Marked dates are green
                        isMarked -> Color(0xFF4CAF50)
                        // Unmarked, past dates are gray
                        else -> Color.LightGray
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f) // Distribute width equally
                            .aspectRatio(1f) // Make cells square
                            .padding(2.dp)
                            .clip(RectangleShape)
                            .background(color)
                    )
                }
            }
        }
    }
}
