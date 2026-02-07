package com.tobaxiom.placebo

import android.app.Application
import com.tobaxiom.placebo.data.AppDatabase
import com.tobaxiom.placebo.data.StreaksRepository

class StreaksApplication : Application() {
    val database by lazy { AppDatabase.getDatabase(this) }
    val repository by lazy { StreaksRepository(database.streakDao(), database.completionDao()) }
}
