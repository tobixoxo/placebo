package com.tobaxiom.placebo

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import java.time.LocalDate

class StreaksViewModel : ViewModel() {
    val streaks = mutableStateListOf<Streak>()

    fun getStreak(id: String): Streak? {
        return streaks.find { it.id.toString() == id }
    }

    fun addStreak(name: String) {
        streaks.add(Streak(name))
    }

    fun editStreak(streak: Streak, newName: String) {
        streak.name = newName
        // Force recomposition for the item by re-adding it.
        val index = streaks.indexOf(streak)
        if (index != -1) {
            streaks.removeAt(index)
            streaks.add(index, streak)
        }
    }

    fun removeStreak(streak: Streak) {
        streaks.remove(streak)
    }

    fun markToday(streak: Streak) {
        if (LocalDate.now() !in streak.markedDays) {
            streak.markForToday()
        }
    }

    fun unmarkToday(streak: Streak) {
        streak.unmarkForToday()
    }
}
