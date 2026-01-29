package com.tobaxiom.placebo

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import java.time.LocalDate
import kotlin.random.Random

class StreaksViewModel : ViewModel() {
    val streaks = mutableStateListOf<Streak>()

    init {
        if (streaks.isEmpty()) {
            seedInitialData()
        }
    }

    private fun seedInitialData() {
        val today = LocalDate.now()

        val streak1 = Streak("Daily Workout").apply {
//            for (i in 0..30) {
//                if (Random.nextBoolean()) {
//                    markedDays.add(today.minusDays(i.toLong()))
//                }
//            }
            // Ensure some streaks for demo
            markedDays.add(today.minusDays(1))
            markedDays.add(today.minusDays(2))
            markedDays.add(today.minusDays(4))
            markedDays.add(today.minusDays(5))
            markedDays.add(today.minusDays(6))
            markedDays.sort()
        }

        val streak2 = Streak("Read for 15 mins").apply {
            for (i in 0..15) {
                if (Random.nextBoolean()) {
                    markedDays.add(today.minusDays(i.toLong()))
                }
            }
            markedDays.sort()
        }

        val streak3 = Streak("Drink 8 glasses of water").apply {
            markedDays.add(today)
            markedDays.add(today.minusDays(1))
            markedDays.sort()
        }

        streaks.addAll(listOf(streak1, streak2, streak3))
    }

    fun getStreak(id: String): Streak? {
        return streaks.find { it.id.toString() == id }
    }

    fun addStreak(name: String) {
        streaks.add(Streak(name))
    }

    fun editStreak(streak: Streak, newName: String) {
        streak.name = newName
        val index = streaks.indexOf(streak)
        if (index != -1) {
            streaks[index] = streak
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
