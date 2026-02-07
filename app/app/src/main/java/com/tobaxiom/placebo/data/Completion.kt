package com.tobaxiom.placebo.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "completions",
    foreignKeys = [
        ForeignKey(
            entity = Streak::class,
            parentColumns = ["id"],
            childColumns = ["streakId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class Completion(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val streakId: Int,
    val date: Long // Timestamp of the completed day
)
