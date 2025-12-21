package com.tobaxiom.placebo

import java.time.LocalDate
import java.util.UUID

class Streak(var name: String) {
    var id: UUID = UUID.randomUUID()

    // TODO: what DS do we store these days in? a SortedSet maybe, we don't need duplicates and its good if its sorted
    var markedDays = mutableListOf<LocalDate>()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Streak

        return id != other.id
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }

    fun markForToday() {
        markedDays.add(LocalDate.now())
    }

    fun unmarkForToday() {
        // TODO: remove from sortedset
    }

    fun getLongestStreak(): Int {
        if (markedDays.isEmpty()) return 0
        if (markedDays.size == 1) return 1

        var maxStreakLen = 0;
        var streakLen = 1
        var prevDay: LocalDate = markedDays[0]
        for (day in markedDays.sortedDescending()){
            if (day.minusDays(1).equals(prevDay)) {
                streakLen++
                prevDay = day
                if (streakLen > maxStreakLen) maxStreakLen = streakLen
            } else {
                streakLen = 1
            }
        }
        return maxStreakLen;
    }

    fun getCurrentStreak(): Int {
        if (markedDays.isEmpty()) return 0

        var streakLen = 0
        // TODO: figure out if the current day counted in the current streak?????
        var lastDate = LocalDate.now().plusDays(1)
        for (day in markedDays.sortedDescending()){
            if (lastDate.minusDays(1).equals(day)) {
                streakLen++
                lastDate = day
            } else {
                break;
            }
        }
        return streakLen
    }
}
