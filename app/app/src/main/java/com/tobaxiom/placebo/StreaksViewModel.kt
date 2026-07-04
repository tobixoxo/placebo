package com.tobaxiom.placebo

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tobaxiom.placebo.data.Completion
import com.tobaxiom.placebo.data.Streak
import com.tobaxiom.placebo.data.StreaksRepository
import com.tobaxiom.placebo.util.ReminderManager
import com.tobaxiom.placebo.util.calculateStreakCounts
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDate
import java.time.YearMonth
import java.time.ZoneId
import java.time.ZoneOffset
import kotlin.random.Random

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

    // Reactive "Today" completion set
    val completedTodayIds: StateFlow<Set<Int>> = repository.getCompletionsForDate(
        LocalDate.now().atStartOfDay().toEpochSecond(ZoneOffset.UTC) * 1000
    ).map { it.toSet() }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptySet()
        )

    private val _viewedStreakId = MutableStateFlow<Int?>(null)
    private val _viewedMonth = MutableStateFlow(YearMonth.now())
    
    val viewedMonth: StateFlow<YearMonth> = _viewedMonth

    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    private val viewedStreakFlow: Flow<Streak?> = _viewedStreakId.flatMapLatest { streakId ->
        streakId?.let { repository.getStreakByIdFlow(it) } ?: flowOf(null)
    }

    val viewedStreak: StateFlow<Streak?> = viewedStreakFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    // Optimized flow for fetching only the viewed month's data
    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    private val monthlyCompletionsFlow: Flow<List<Completion>> = combine(_viewedStreakId, _viewedMonth) { id, month ->
        id to month
    }.flatMapLatest { (id, month) ->
        if (id == null) return@flatMapLatest flowOf(emptyList())
        val startOfMonth = month.atDay(1).atStartOfDay().toEpochSecond(ZoneOffset.UTC) * 1000
        val endOfMonth = month.atEndOfMonth().atTime(23, 59, 59).toEpochSecond(ZoneOffset.UTC) * 1000
        repository.getCompletionsForStreakInRange(id, startOfMonth, endOfMonth)
    }

    val monthlyCompletions: StateFlow<Set<LocalDate>> = monthlyCompletionsFlow
        .map { list ->
            list.map { Instant.ofEpochMilli(it.date).atZone(ZoneId.systemDefault()).toLocalDate() }.toSet()
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptySet()
        )

    // We still need all completions for overall streak stats calculation
    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    private val allCompletionsFlow: Flow<List<Completion>> = _viewedStreakId.flatMapLatest { streakId ->
        streakId?.let { repository.getCompletionsForStreak(it) } ?: flowOf(emptyList())
    }

    val streakCounts: StateFlow<Pair<Int, Int>> = allCompletionsFlow
        .map { calculateStreakCounts(it) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = Pair(0, 0)
        )

    fun setViewedStreak(streakId: Int?) {
        _viewedStreakId.value = streakId
    }

    fun setViewedMonth(month: YearMonth) {
        _viewedMonth.value = month
    }

    fun seedData() {
        if (!BuildConfig.DEBUG) return
        
        viewModelScope.launch {
            // Check if we already have data
            val existing = repository.activeStreaks.first()
            if (existing.isNotEmpty()) return@launch

            val habits = listOf(
                "Workout" to "Fitness",
                "Reading" to "Book",
                "Coding" to "Code",
                "Hydration" to "Hydrate"
            )

            habits.forEach { (name, icon) ->
                val startDate = LocalDate.now().minusDays(90)
                val streak = Streak(
                    name = name,
                    iconName = icon,
                    startDate = startDate.atStartOfDay().toEpochSecond(ZoneOffset.UTC) * 1000
                )
                repository.insert(streak)
            }
            
            // Get the newly inserted streaks and seed completions
            val streaks = repository.activeStreaks.first()
            streaks.forEach { s ->
                for (i in 0..90) {
                    // 70% chance of completion
                    if (Random.nextFloat() > 0.3f) {
                        val date = LocalDate.now().minusDays(i.toLong())
                        repository.insert(Completion(
                            streakId = s.id,
                            date = date.atStartOfDay().toEpochSecond(ZoneOffset.UTC) * 1000
                        ))
                    }
                }
            }
        }
    }

    fun addStreak(name: String, iconName: String) {
        viewModelScope.launch {
            repository.insert(Streak(name = name, iconName = iconName, startDate = System.currentTimeMillis()))
        }
    }

    fun editStreak(streak: Streak, newName: String, newIconName: String) {
        viewModelScope.launch {
            repository.update(streak.copy(name = newName, iconName = newIconName))
        }
    }

    fun updateReminder(context: Context, streakId: Int, isEnabled: Boolean, reminderTime: Long?) {
        viewModelScope.launch {
            repository.updateReminder(streakId, isEnabled, reminderTime)
            if (isEnabled && reminderTime != null) {
                ReminderManager.scheduleReminder(context, streakId, reminderTime)
            } else {
                ReminderManager.cancelReminder(context, streakId)
            }
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
