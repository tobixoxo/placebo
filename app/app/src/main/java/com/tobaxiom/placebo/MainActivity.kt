package com.tobaxiom.placebo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.tobaxiom.placebo.archive.ArchiveScreen
import com.tobaxiom.placebo.streaksList.StreaksListScreen
import com.tobaxiom.placebo.streakView.StreakPage

class MainActivity : ComponentActivity() {
    private val streaksViewModel: StreaksViewModel by viewModels {
        StreaksViewModelFactory((application as StreaksApplication).repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            PlaceboTheme {
                val navController = rememberNavController()

                NavHost(navController = navController, startDestination = "streakslist") {
                    composable("streakslist") {
                        val streaks by streaksViewModel.activeStreaks.collectAsStateWithLifecycle()
                        StreaksListScreen(
                            streaks = streaks,
                            onStreakClicked = { streak ->
                                navController.navigate("streakview/${streak.id}")
                            },
                            onAddStreak = { name, iconName -> streaksViewModel.addStreak(name, iconName) },
                            onEditStreak = { streak, newName, newIconName -> streaksViewModel.editStreak(streak, newName, newIconName) },
                            onArchiveStreak = { streak -> streaksViewModel.onArchiveStreak(streak) },
                            onRemoveStreak = { streak -> streaksViewModel.onDeletePermanently(streak) },
                            onNavigateToArchive = { navController.navigate("archive") }
                        )
                    }
                    composable("archive") {
                        val archivedStreaks by streaksViewModel.archivedStreaks.collectAsStateWithLifecycle()
                        ArchiveScreen(
                            archivedStreaks = archivedStreaks,
                            onUnarchiveStreak = { streak -> streaksViewModel.onUnarchiveStreak(streak) },
                            onDeletePermanently = { streak -> streaksViewModel.onDeletePermanently(streak) },
                            onBackClicked = { navController.popBackStack() }
                        )
                    }
                    composable("streakview/{streakId}") { backStackEntry ->
                        val streakId = backStackEntry.arguments?.getString("streakId")?.toIntOrNull()
                        if (streakId != null) {
                            // Use DisposableEffect to set/clear the viewed streak ID
                            DisposableEffect(streakId) {
                                streaksViewModel.setViewedStreak(streakId)
                                onDispose {
                                    streaksViewModel.setViewedStreak(null)
                                }
                            }

                            val streak = streaksViewModel.getStreak(streakId)
                            val completions by streaksViewModel.completions.collectAsState()
                            val streakCounts by streaksViewModel.streakCounts.collectAsState()

                            if (streak != null) {
                                StreakPage(
                                    streak = streak,
                                    completions = completions,
                                    currentStreak = streakCounts.first,
                                    longestStreak = streakCounts.second,
                                    onMarkToday = { streaksViewModel.markToday(streakId) },
                                    onUnmarkToday = { streaksViewModel.unmarkToday(streakId) },
                                    onBackClicked = { navController.popBackStack() }
                                )
                            } else {
                                // Handle error: streak not found
                            }
                        } else {
                            // Handle error: invalid streakId
                        }
                    }
                }
            }
        }
    }
}
