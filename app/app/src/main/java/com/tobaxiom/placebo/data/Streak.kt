package com.tobaxiom.placebo.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "streaks")
data class Streak(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val startDate: Long,
    val lastResetDate: Long? = null,
    val iconName: String = "Star",
    val isArchived: Boolean = false
)
