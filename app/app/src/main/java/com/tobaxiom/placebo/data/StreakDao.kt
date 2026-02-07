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

    @Query("SELECT * FROM streaks ORDER BY startDate DESC")
    fun getAllStreaks(): Flow<List<Streak>>
}
