package com.tobaxiom.placebo.data

import kotlinx.coroutines.flow.Flow

class StreaksRepository(private val streakDao: StreakDao, private val completionDao: CompletionDao) {

    val activeStreaks: Flow<List<Streak>> = streakDao.getActiveStreaks()
    val archivedStreaks: Flow<List<Streak>> = streakDao.getArchivedStreaks()

    fun getCompletionsForStreak(streakId: Int): Flow<List<Completion>> {
        return completionDao.getCompletionsForStreak(streakId)
    }

    fun getCompletionsForDate(date: Long): Flow<List<Int>> {
        return completionDao.getCompletionsForDate(date)
    }

    suspend fun getStreakById(id: Int): Streak? {
        return streakDao.getStreakById(id)
    }

    fun getStreakByIdFlow(id: Int): Flow<Streak?> {
        return streakDao.getStreakByIdFlow(id)
    }

    suspend fun insert(streak: Streak) {
        streakDao.insert(streak)
    }

    suspend fun insert(completion: Completion) {
        completionDao.insert(completion)
    }

    suspend fun update(streak: Streak) {
        streakDao.update(streak)
    }

    suspend fun delete(streak: Streak) {
        streakDao.delete(streak)
    }

    suspend fun delete(completion: Completion) {
        completionDao.delete(completion.streakId, completion.date)
    }

    suspend fun archiveStreak(streak: Streak) {
        streakDao.update(streak.copy(isArchived = true))
    }

    suspend fun unarchiveStreak(streak: Streak) {
        streakDao.update(streak.copy(isArchived = false))
    }

    suspend fun updateReminder(streakId: Int, isEnabled: Boolean, reminderTime: Long?) {
        val streak = streakDao.getStreakById(streakId)
        if (streak != null) {
            streakDao.update(streak.copy(isReminderEnabled = isEnabled, reminderTime = reminderTime))
        }
    }
}
