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
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.ZoneOffset

class StreaksViewModel(private val repository: StreaksRepository) : ViewModel() {

    val activeStreaks: StateFlow<List<Streak>> = repository.activeStreaks
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val archivedStreaks: StateFlow<List<Streak>> = repository.archivedStreaks
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // The trigger for the detail screen data flow
    private val _viewedStreakId = MutableStateFlow<Int?>(null)

    // The single, authoritative stream of completion data
    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    private val viewedStreakCompletions: Flow<List<Completion>> = _viewedStreakId.flatMapLatest { streakId ->
        streakId?.let { repository.getCompletionsForStreak(it) } ?: flowOf(emptyList())
    }

    // The completions for the currently viewed streak
    val completions: StateFlow<List<Completion>> = viewedStreakCompletions
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // The calculated streak counts for the currently viewed streak
    val streakCounts: StateFlow<Pair<Int, Int>> = viewedStreakCompletions
        .map { calculateStreakCounts(it) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = Pair(0, 0)
        )

    /**
     * Sets the currently viewed streak, triggering the data flow for the detail screen.
     * Call with null to stop the flow.
     */
    fun setViewedStreak(streakId: Int?) {
        _viewedStreakId.value = streakId
    }

    fun getStreak(id: Int): Streak? {
        return activeStreaks.value.find { it.id == id } ?: archivedStreaks.value.find { it.id == id }
    }

    fun addStreak(name: String, iconName: String) {
        viewModelScope.launch {
            repository.insert(Streak(name = name, iconName = iconName, startDate = 0))
        }
    }

    fun editStreak(streak: Streak, newName: String, newIconName: String) {
        viewModelScope.launch {
            repository.update(streak.copy(name = newName, iconName = newIconName))
        }
    }

    fun onArchiveStreak(streak: Streak) {
        viewModelScope.launch {
            repository.archiveStreak(streak)
        }
    }

    fun onUnarchiveStreak(streak: Streak) {
        viewModelScope.launch {
            repository.unarchiveStreak(streak)
        }
    }

    fun onDeletePermanently(streak: Streak) {
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
