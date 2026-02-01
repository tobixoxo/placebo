package com.tobaxiom.placebo.streakView

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tobaxiom.placebo.Streak
import java.time.LocalDate

@Composable
fun StreakPage(
    streak: Streak,
    onMarkToday: () -> Unit,
    onUnmarkToday: () -> Unit
) {
    val isMarked by remember(streak.markedDays) {
        derivedStateOf {
            LocalDate.now() in streak.markedDays
        }
    }
    val currentStreak by remember(streak.markedDays) {
        derivedStateOf {
            streak.getCurrentStreak()
        }
    }
    val longestStreak by remember(streak.markedDays) {
        derivedStateOf {
            streak.getLongestStreak()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = streak.name,
            fontSize = 34.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        // Streak Info Section
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "Current Streak:", fontSize = 20.sp)
                Spacer(modifier = Modifier.weight(1f))
                Text(text = "$currentStreak days", fontSize = 20.sp)
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "Longest Streak:", fontSize = 20.sp)
                Spacer(modifier = Modifier.weight(1f))
                Text(text = "$longestStreak days", fontSize = 20.sp)
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Contribution Graph
        StreakCalendar(streak = streak)

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = {
                if (!isMarked) onMarkToday() else onUnmarkToday()
            },
            modifier = Modifier.padding(bottom = 32.dp)
        ) {
            Text(
                text = if (isMarked) "Marked for Today" else "Mark for Today",
                fontSize = 20.sp
            )
        }
    }
}
