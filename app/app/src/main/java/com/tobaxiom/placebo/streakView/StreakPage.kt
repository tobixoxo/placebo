package com.tobaxiom.placebo.streakView

import android.R
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.tobaxiom.placebo.data.Completion
import com.tobaxiom.placebo.data.Streak
import java.time.LocalDate
import java.time.ZoneOffset

@Composable
fun StreakPage(
    streak: Streak,
    completions: List<Completion>,
    onMarkToday: () -> Unit,
    onUnmarkToday: () -> Unit
) {
    val today = LocalDate.now().atStartOfDay().toEpochSecond(ZoneOffset.UTC) * 1000
    val isTodayMarked = completions.any { it.date == today }

    Scaffold(
        bottomBar = {
            BottomAppBar(
                containerColor = Color.White
            ) {
                Row(

                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,

                ) {
                    if (isTodayMarked) {
                        Button(
                            onClick = onUnmarkToday,
                            modifier = Modifier.height(48.dp),

                            )
                        {
                            Text("Unmark Today")

                        }
                    } else {
                        Button(
                            onClick = onMarkToday,
                            modifier = Modifier.height(48.dp)
                        )
                        {
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
            Text(text = streak.name)
            StreakCalendar(completions = completions)
        }
    }
}
