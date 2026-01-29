package com.tobaxiom.placebo

import java.time.LocalDate
import java.util.UUID
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import java.io.Serializable

class Streak(var name: String) : Serializable {
    var id: UUID = UUID.randomUUID()
    var markedDays: SnapshotStateList<LocalDate> = mutableStateListOf()

    override fun toString(): String {
        return "Streak{name=$name, id=$id, markedDays=$markedDays}"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Streak

        return id == other.id
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }

    fun markForToday() {
        if(LocalDate.now() !in markedDays) {
            markedDays.add(LocalDate.now())
            markedDays.sort();
        }

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
        if(markedDays.isEmpty()) return 0

        val today = LocalDate.now();
        val yesterday = today.minusDays(1);

        val latestDay = markedDays.lastOrNull() ?: return 0

        if(latestDay != today && latestDay != yesterday)
            return 0;
        var streakLen = 1;

        for(i in (markedDays.size - 2) downTo 0){
            val currentDay = markedDays[i + 1]
            val previousDay = markedDays[i]
            if(currentDay.minusDays(1) == previousDay)
                streakLen++;
            else
                break;
        }

        return streakLen

    }
}
