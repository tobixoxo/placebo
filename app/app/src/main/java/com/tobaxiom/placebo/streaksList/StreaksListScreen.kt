package com.tobaxiom.placebo.streaksList

import android.graphics.fonts.FontStyle
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tobaxiom.placebo.PlaceboTheme
import com.tobaxiom.placebo.data.Streak

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StreaksListScreen(
    streaks: List<Streak>,
    onStreakClicked: (Streak) -> Unit,
    onAddStreak: (String) -> Unit,
    onEditStreak: (Streak, String) -> Unit,
    onRemoveStreak: (Streak) -> Unit,
) {
    // State to control the visibility of the "Add/Edit" dialog
    var showDialog by remember { mutableStateOf(false) }
    // State to hold the streak being edited, if any.
    var streakToEdit by remember { mutableStateOf<Streak?>(null) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Placebo",
                        fontSize = 30.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                streakToEdit = null // Ensure we are in "add" mode
                showDialog = true
            }) {
                Icon(Icons.Default.Add, contentDescription = "Add a new streak")
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            if (streaks.isEmpty()) {
                Text("No streaks yet. Press '+' to add one!")
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
//                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = paddingValues
                ) {
                    items(streaks, key = { it.id }) { streak ->
                        StreakItem(
                            streak = streak,
                            onItemClick = { onStreakClicked(streak) },
                            onEditClick = {
                                streakToEdit = streak
                                showDialog = true
                            },
                            onRemoveClick = { onRemoveStreak(streak) }
                        )
                    }
                }
            }
        }
    }

    if (showDialog) {
        AddEditStreakDialog(
            streakToEdit = streakToEdit,
            onDismiss = { showDialog = false },
            onConfirm = { name ->
                if (streakToEdit == null) {
                    onAddStreak(name)
                } else {
                    onEditStreak(streakToEdit!!, name)
                }
                showDialog = false
            }
        )
    }
}

@Composable
fun StreakItem(
    streak: Streak,
    onItemClick: () -> Unit,
    onEditClick: () -> Unit,
    onRemoveClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .clickable(onClick = onItemClick)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = streak.name, modifier = Modifier.weight(1f), fontSize = 18.sp)
            IconButton(onClick = onEditClick) {
                Icon(Icons.Default.Edit, contentDescription = "Edit Streak")
            }
            IconButton(onClick = onRemoveClick) {
                Icon(Icons.Default.Delete, contentDescription = "Remove Streak")
            }
        }
    }
}

@Composable
fun AddEditStreakDialog(
    streakToEdit: Streak?,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var text by remember { mutableStateOf(streakToEdit?.name ?: "") }
    val isEditing = streakToEdit != null

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (isEditing) "Edit streak" else "Add a new streak") },
        text = {
            TextField(
                value = text,
                onValueChange = { text = it },
                label = { Text("Streak Name") },
                singleLine = true
            )
        },
        confirmButton = {
            Button(
                onClick = {
                    if (text.isNotBlank()) {
                        onConfirm(text.trim())
                    }
                },
                enabled = text.isNotBlank()
            ) {
                Text(if (isEditing) "Set Name" else "Add")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}


@Preview(showBackground = true)
@Composable
fun StreaksListScreenPreview_Empty() {
    PlaceboTheme {
        StreaksListScreen(
            streaks = emptyList(),
            onStreakClicked = {},
            onAddStreak = {},
            onEditStreak = { _, _ -> },
            onRemoveStreak = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun StreaksListScreenPreview_WithData() {
    // Use a 'remember' block to create a stable list for the preview
    val previewStreaks = remember {
        mutableStateListOf(
            Streak(id = 1, name = "Workout Daily", startDate = System.currentTimeMillis()),
            Streak(id = 2, name = "Read for 15 minutes", startDate = System.currentTimeMillis()),
            Streak(id = 3, name = "Drink 8 glasses of water", startDate = System.currentTimeMillis())
        )
    }
    PlaceboTheme {
        StreaksListScreen(
            streaks = previewStreaks,
            onStreakClicked = {},
            onAddStreak = { name -> previewStreaks.add(Streak(name = name, startDate = System.currentTimeMillis())) },
            onEditStreak = { streak, newName ->
                val index = previewStreaks.indexOf(streak)
                if (index != -1) {
                    previewStreaks[index] = streak.copy(name = newName)
                }
            },
            onRemoveStreak = { streak -> previewStreaks.remove(streak) }
        )
    }
}
