package com.tobaxiom.placebo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.tobaxiom.placebo.streaksList.StreaksListScreen
import com.tobaxiom.placebo.streakView.StreakPage

class MainActivity : ComponentActivity() {
    private val streaksViewModel: StreaksViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            PlaceboTheme {
                val navController = rememberNavController()
                val streaks = streaksViewModel.streaks

                NavHost(navController = navController, startDestination = "streakslist") {
                    composable("streakslist") {
                        StreaksListScreen(
                            streaks = streaks,
                            onStreakClicked = { streak ->
                                navController.navigate("streakview/${streak.id}")
                            },
                            onAddStreak = { name -> streaksViewModel.addStreak(name) },
                            onEditStreak = { streak, newName -> streaksViewModel.editStreak(streak, newName) },
                            onRemoveStreak = { streak -> streaksViewModel.removeStreak(streak) }
                        )
                    }
                    composable("streakview/{streakId}") { backStackEntry ->
                        val streakId = backStackEntry.arguments?.getString("streakId")
                        val streak = streaksViewModel.getStreak(streakId!!)
                        if (streak != null) {
                            StreakPage(
                                streak = streak,
                                onMarkToday = { streaksViewModel.markToday(streak) },
                                onUnmarkToday = { streaksViewModel.unmarkToday(streak) }
                            )
                        } else {
                            // Handle error: streak not found
                        }
                    }
                }
            }
        }
    }
}
