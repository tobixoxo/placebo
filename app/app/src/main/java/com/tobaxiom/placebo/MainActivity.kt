package com.tobaxiom.placebo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
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
                        val streaks = streaksViewModel.streaks.collectAsStateWithLifecycle()
                        StreaksListScreen(
                            streaks = streaks.value,
                            onStreakClicked = { streak ->
                                navController.navigate("streakview/${streak.id}")
                            },
                            onAddStreak = { name, iconName -> streaksViewModel.addStreak(name, iconName) },
                            onEditStreak = { streak, newName, newIconName -> streaksViewModel.editStreak(streak, newName, newIconName) },
                            onRemoveStreak = { streak -> streaksViewModel.removeStreak(streak) }
                        )
                    }
                    composable("streakview/{streakId}") { backStackEntry ->
                        val streakId = backStackEntry.arguments?.getString("streakId")?.toIntOrNull()
                        if (streakId != null) {
                            LaunchedEffect(streakId) {
                                streaksViewModel.loadCompletionsForStreak(streakId)
                            }

                            val streak = streaksViewModel.getStreak(streakId)
                            val currentStreak by streaksViewModel.currentStreakCount.collectAsState()
                            val longestStreak by streaksViewModel.longestStreakCount.collectAsState()
                            val completions = streaksViewModel.getCompletionsForStreak(streakId).collectAsState(initial = emptyList())

                            if (streak != null) {
                                StreakPage(
                                    streak = streak,
                                    completions = completions.value,
                                    currentStreak = currentStreak,
                                    longestStreak = longestStreak,
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
