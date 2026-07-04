package com.tobaxiom.placebo.details

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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
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
    minMonth: YearMonth,
    maxMonth: YearMonth,
    completedDates: Set<LocalDate>,
    onMonthChange: (YearMonth) -> Unit,
    modifier: Modifier = Modifier
) {
    val haptic = LocalHapticFeedback.current

    Column(modifier = modifier.fillMaxWidth()) {
        // CalendarHeader Component
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            val canGoBack = currentMonth.isAfter(minMonth)
            IconButton(
                onClick = {
                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    onMonthChange(currentMonth.minusMonths(1))
                },
                enabled = canGoBack
            ) {
                Icon(
                    imageVector = Icons.Default.ChevronLeft,
                    contentDescription = "Previous Month",
                    modifier = Modifier.alpha(if (canGoBack) 1f else 0.3f)
                )
            }

            Text(
                text = "${currentMonth.month.getDisplayName(TextStyle.FULL, Locale.getDefault())} ${currentMonth.year}",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            val canGoForward = currentMonth.isBefore(maxMonth)
            IconButton(
                onClick = {
                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    onMonthChange(currentMonth.plusMonths(1))
                },
                enabled = canGoForward
            ) {
                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = "Next Month",
                    modifier = Modifier.alpha(if (canGoForward) 1f else 0.3f)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Day of Week Header
        Row(modifier = Modifier.fillMaxWidth()) {
            listOf("S", "M", "T", "W", "T", "F", "S").forEach { day ->
                Text(
                    text = day,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                )
            }
        }

        // CalendarGrid Component
        val firstDayOfMonth = currentMonth.atDay(1)
        val firstDayOfWeek = (firstDayOfMonth.dayOfWeek.value % 7) // Sunday = 0
        val daysInMonth = currentMonth.lengthOfMonth()
        
        val gridItems = List(firstDayOfWeek) { null } + List(daysInMonth) { currentMonth.atDay(it + 1) }

        LazyVerticalGrid(
            columns = GridCells.Fixed(7),
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 500.dp),
            userScrollEnabled = false,
            contentPadding = PaddingValues(vertical = 8.dp)
        ) {
            items(gridItems) { date ->
                if (date != null) {
                    // CalendarDayItem Component
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
}

@Composable
private fun CalendarDayItem(
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
                    Modifier.border(2.dp, theme.primary, CircleShape)
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
