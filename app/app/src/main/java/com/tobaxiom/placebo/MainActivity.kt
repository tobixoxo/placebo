package com.tobaxiom.placebo

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
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
                val context = LocalContext.current

                // Permission launcher for notifications (Android 13+)
                val permissionLauncher = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.RequestPermission()
                ) { isGranted ->
                    // Handle permission result if needed
                }

                // Check and request permission if on Android 13+
                LaunchedEffect(Unit) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        if (ContextCompat.checkSelfPermission(
                                context,
                                Manifest.permission.POST_NOTIFICATIONS
                            ) != PackageManager.PERMISSION_GRANTED
                        ) {
                            permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                        }
                    }
                }

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

                            val streak by streaksViewModel.viewedStreak.collectAsStateWithLifecycle()
                            val completions by streaksViewModel.completions.collectAsState()
                            val streakCounts by streaksViewModel.streakCounts.collectAsState()

                            if (streak != null) {
                                StreakPage(
                                    streak = streak!!,
                                    completions = completions,
                                    currentStreak = streakCounts.first,
                                    longestStreak = streakCounts.second,
                                    onMarkToday = { streaksViewModel.markToday(streakId) },
                                    onUnmarkToday = { streaksViewModel.unmarkToday(streakId) },
                                    onToggleReminder = { isEnabled, reminderTime ->
                                        streaksViewModel.updateReminder(context, streakId, isEnabled, reminderTime)
                                    },
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
