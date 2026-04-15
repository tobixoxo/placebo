package com.tobaxiom.placebo.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface StreakDao {
    @Insert
    suspend fun insert(streak: Streak)

    @Update
    suspend fun update(streak: Streak)

    @Delete
    suspend fun delete(streak: Streak)

    @Query("SELECT * FROM streaks WHERE isArchived = 0 ORDER BY startDate DESC")
    fun getActiveStreaks(): Flow<List<Streak>>

    @Query("SELECT * FROM streaks WHERE isArchived = 1 ORDER BY startDate DESC")
    fun getArchivedStreaks(): Flow<List<Streak>>

    @Query("SELECT * FROM streaks WHERE id = :id")
    suspend fun getStreakById(id: Int): Streak?

    @Query("SELECT * FROM streaks WHERE id = :id")
    fun getStreakByIdFlow(id: Int): Flow<Streak?>
}
