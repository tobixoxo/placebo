package com.tobaxiom.placebo.util

import com.tobaxiom.placebo.data.Completion
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import kotlin.math.max

fun calculateStreakCounts(completions: List<Completion>): Pair<Int, Int> {
    if (completions.isEmpty()) {
        return Pair(0, 0)
    }

    val dates = completions
        .map { Instant.ofEpochMilli(it.date).atZone(ZoneId.systemDefault()).toLocalDate() }
        .toSet()
        .sorted()

    if (dates.isEmpty()) {
        return Pair(0, 0)
    }

    var longestStreak = 0
    var currentStreak = 0

    if (dates.isNotEmpty()) {
        longestStreak = 1
        currentStreak = 1
    }

    for (i in 1 until dates.size) {
        // Check if the current date is exactly one day after the previous one
        if (dates[i].isEqual(dates[i - 1].plusDays(1))) {
            currentStreak++
        } else {
            // The streak is broken
            currentStreak = 1
        }
        longestStreak = max(longestStreak, currentStreak)
    }

    val today = LocalDate.now()
    val lastCompletionDate = dates.last()

    // If the last completion wasn't today or yesterday, the current streak is 0
    if (!lastCompletionDate.isEqual(today) && !lastCompletionDate.isEqual(today.minusDays(1))) {
        currentStreak = 0
    }

    return Pair(currentStreak, longestStreak)
}
