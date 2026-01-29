package com.tobaxiom.placebo.streakView

import android.content.res.Resources
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.ToggleButton
import androidx.compose.foundation.gestures.snapping.SnapPosition
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.Fragment
import com.tobaxiom.placebo.PlaceboTheme
import com.tobaxiom.placebo.R
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
            .padding(48.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = streak.name,
            fontSize = 34.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom=48.dp)
        ) // heading

        // Streak Info Section
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(32.dp)
        ) {
            // Current Streak Row
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "Current Streak:", fontSize = 20.sp)
                Spacer(modifier = Modifier.width(32.dp))
                Text(text = "$currentStreak", fontSize = 20.sp)
            }

            // Longest Streak Row
            Row(verticalAlignment = Alignment.CenterVertically) {
                // We use a Box with a specific width or weight if you want
                // perfect alignment like your XML constraints
                Text(
                    text = "Longest Streak:",
                    fontSize = 20.sp,
                    modifier = Modifier.width(145.dp) // Adjust to match label width
                )
                Spacer(modifier = Modifier.width(32.dp))
                Text(text = "$longestStreak", fontSize = 20.sp)
            }
        }

        // Spacer pushes the button to the bottom
        Spacer(modifier = Modifier.weight(1f))

        // ToggleButton Equivalent (Material 3 Button)
        Button(
            onClick = {
                if (!isMarked) onMarkToday()
                else onUnmarkToday()
            },
            modifier = Modifier.padding(bottom = 48.dp)
        ) {
            Text(
                text = if (isMarked) "Marked" else "Mark for Today",
                fontSize = 20.sp
            )
        }
    }
}
