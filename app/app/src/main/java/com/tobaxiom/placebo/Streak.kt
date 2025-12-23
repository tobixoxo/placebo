package com.tobaxiom.placebo

import android.util.Log
import java.time.LocalDate
import java.util.NavigableSet
import java.util.UUID

class Streak(var name: String) {
    var id: UUID = UUID.randomUUID()
    var markedDays: NavigableSet<LocalDate> = sortedSetOf()

    override fun toString(): String {
        return "Streak{name=$name, id=$id, markedDays=$markedDays}"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Streak

        Log.i("abc", "$this.equals($other)")
        return id == other.id
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }

    fun markForToday() {
        Log.i("abc", "before adding: $markedDays, ${LocalDate.now()}")
        markedDays.add(LocalDate.now())
        Log.i("abc", "after adding: $markedDays ")

    }

    fun unmarkForToday() {
        markedDays.remove(LocalDate.now())
    }

    fun getLongestStreak(): Int {
        if (markedDays.isEmpty()) return 0
        if (markedDays.size == 1) return 1

        var maxStreakLen = 0;
        var streakLen = 0

        val days = markedDays.iterator()

        var firstDay = days.next()
        while (days.hasNext()) {
            val day = days.next()
            if (firstDay.plusDays(1) == day) {
                streakLen++
                if (streakLen > maxStreakLen) {
                    maxStreakLen = streakLen
                }
            } else {
                streakLen = 0
            }
        }
        return maxStreakLen
    }

    fun getCurrentStreak(): Int {
        // no streaks present
        if (markedDays.isEmpty()) {
            return 0
        }

        val today = LocalDate.now()
        val yesterday = today.minusDays(1)

        val days = markedDays.descendingIterator()

        var latestDay = days.next()

        // no existing streak is 'current', i.e. extends until today/yesterday
        if (latestDay != today && latestDay != yesterday) {
            Log.i("abc", "latestDay=$latestDay, today=$today, yesterday=$yesterday")
            return 0
        }

        var streakLen = 1 // today/tomorrow is already accounted for

        Log.i("abc", "streakLen=$streakLen")
        // go backwards from the latest day until the streak ends
        while (days.hasNext()) {
            val day = days.next()
            if (latestDay.minusDays(1) == day) {
                streakLen++
                Log.i("abc", "streakLen is now=$streakLen")
                latestDay = day
            } else {
                break
            }
        }
        return streakLen
    }
}
