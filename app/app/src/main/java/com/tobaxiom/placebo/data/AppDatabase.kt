package com.tobaxiom.placebo.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(entities = [Streak::class, Completion::class], version = 2, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun streakDao(): StreakDao
    abstract fun completionDao(): CompletionDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        // Simple migration from version 1 to 2 that adds the completions table.
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("CREATE TABLE IF NOT EXISTS `completions` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `streakId` INTEGER NOT NULL, `date` INTEGER NOT NULL, FOREIGN KEY(`streakId`) REFERENCES `streaks`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE )")
            }
        }

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "streaks_database"
                )
                .addMigrations(MIGRATION_1_2)
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
