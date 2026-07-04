package com.tobaxiom.placebo.streakView

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun MonthlyCalendar(
    currentMonth: YearMonth,
    completedDates: Set<LocalDate>,
    onMonthChange: (YearMonth) -> Unit,
    modifier: Modifier = Modifier
) {
    val haptic = LocalHapticFeedback.current

    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = {
                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                onMonthChange(currentMonth.minusMonths(1))
            }) {
                Icon(Icons.Default.ChevronLeft, contentDescription = "Previous Month")
            }

            Text(
                text = "${currentMonth.month.getDisplayName(TextStyle.FULL, Locale.getDefault())} ${currentMonth.year}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            IconButton(onClick = {
                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                onMonthChange(currentMonth.plusMonths(1))
            }) {
                Icon(Icons.Default.ChevronRight, contentDescription = "Next Month")
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Day of Week Header
        Row(modifier = Modifier.fillMaxWidth()) {
            val daysOfWeek = listOf("S", "M", "T", "W", "T", "F", "S")
            daysOfWeek.forEach { day ->
                Text(
                    text = day,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                )
            }
        }

        CalendarGrid(currentMonth = currentMonth, completedDates = completedDates)
    }
}

@Composable
fun CalendarGrid(
    currentMonth: YearMonth,
    completedDates: Set<LocalDate>
) {
    val firstDayOfMonth = currentMonth.atDay(1)
    val daysInMonth = currentMonth.lengthOfMonth()
    
    // 0 = Sunday, 1 = Monday, ..., 6 = Saturday
    val firstDayOfWeek = (firstDayOfMonth.dayOfWeek.value % 7)
    
    val gridItems = mutableListOf<LocalDate?>()
    repeat(firstDayOfWeek) { gridItems.add(null) }
    for (day in 1..daysInMonth) {
        gridItems.add(currentMonth.atDay(day))
    }

    LazyVerticalGrid(
        columns = GridCells.Fixed(7),
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(max = 300.dp), // Fixed max height to allow parent scroll
        userScrollEnabled = false,
        contentPadding = PaddingValues(vertical = 4.dp)
    ) {
        items(gridItems) { date ->
            if (date != null) {
                CalendarDayItem(
                    date = date,
                    isCompleted = date in completedDates,
                    isToday = date == LocalDate.now()
                )
            } else {
                Box(modifier = Modifier.aspectRatio(1f))
            }
        }
    }
}

@Composable
fun CalendarDayItem(
    date: LocalDate,
    isCompleted: Boolean,
    isToday: Boolean
) {
    val isFuture = date.isAfter(LocalDate.now())
    val theme = MaterialTheme.colorScheme

    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .padding(4.dp)
            .clip(CircleShape)
            .background(
                color = if (isCompleted) theme.primary else Color.Transparent,
                shape = CircleShape
            )
            .then(
                if (isToday && !isCompleted) {
                    Modifier.border(1.5.dp, theme.primary, CircleShape)
                } else Modifier
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = date.dayOfMonth.toString(),
            color = when {
                isCompleted -> theme.onPrimary
                isFuture -> theme.onSurface.copy(alpha = 0.3f)
                else -> theme.onSurface
            },
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = if (isToday || isCompleted) FontWeight.Bold else FontWeight.Medium
        )
    }
}
