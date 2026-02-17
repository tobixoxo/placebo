package com.tobaxiom.placebo.data

import kotlinx.coroutines.flow.Flow

class StreaksRepository(private val streakDao: StreakDao, private val completionDao: CompletionDao) {

    val activeStreaks: Flow<List<Streak>> = streakDao.getActiveStreaks()
    val archivedStreaks: Flow<List<Streak>> = streakDao.getArchivedStreaks()

    fun getCompletionsForStreak(streakId: Int): Flow<List<Completion>> {
        return completionDao.getCompletionsForStreak(streakId)
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
}
