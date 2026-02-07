package com.tobaxiom.placebo.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface CompletionDao {
    @Insert
    suspend fun insert(completion: Completion)

    @Query("DELETE FROM completions WHERE streakId = :streakId AND date = :date")
    suspend fun delete(streakId: Int, date: Long)

    @Query("SELECT * FROM completions WHERE streakId = :streakId")
    fun getCompletionsForStreak(streakId: Int): Flow<List<Completion>>
}
