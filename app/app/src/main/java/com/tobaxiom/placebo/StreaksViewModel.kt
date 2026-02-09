package com.tobaxiom.placebo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tobaxiom.placebo.data.Completion
import com.tobaxiom.placebo.data.Streak
import com.tobaxiom.placebo.data.StreaksRepository
import com.tobaxiom.placebo.util.calculateStreakCounts
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.ZoneOffset

class StreaksViewModel(private val repository: StreaksRepository) : ViewModel() {

    val streaks: StateFlow<List<Streak>> = repository.allStreaks
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _currentStreakCount = MutableStateFlow(0)
    val currentStreakCount: StateFlow<Int> = _currentStreakCount

    private val _longestStreakCount = MutableStateFlow(0)
    val longestStreakCount: StateFlow<Int> = _longestStreakCount

    fun loadCompletionsForStreak(streakId: Int) {
        repository.getCompletionsForStreak(streakId)
            .onEach { completions ->
                val (current, longest) = calculateStreakCounts(completions)
                _currentStreakCount.value = current
                _longestStreakCount.value = longest
            }
            .launchIn(viewModelScope)
    }

    fun getCompletionsForStreak(streakId: Int): Flow<List<Completion>> {
        return repository.getCompletionsForStreak(streakId)
    }

    fun getStreak(id: Int): Streak? {
        return streaks.value.find { it.id == id }
    }

    fun addStreak(name: String) {
        viewModelScope.launch {
            repository.insert(Streak(name = name, startDate = 0))
        }
    }

    fun editStreak(streak: Streak, newName: String) {
        viewModelScope.launch {
            repository.update(streak.copy(name = newName))
        }
    }

    fun removeStreak(streak: Streak) {
        viewModelScope.launch {
            repository.delete(streak)
        }
    }

    fun markToday(streakId: Int) {
        viewModelScope.launch {
            val today = LocalDate.now().atStartOfDay().toEpochSecond(ZoneOffset.UTC) * 1000
            repository.insert(Completion(streakId = streakId, date = today))
        }
    }

    fun unmarkToday(streakId: Int) {
        viewModelScope.launch {
            val today = LocalDate.now().atStartOfDay().toEpochSecond(ZoneOffset.UTC) * 1000
            repository.delete(Completion(streakId = streakId, date = today))
        }
    }
}
