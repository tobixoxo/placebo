package com.tobaxiom.placebo.streaksList

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Archive
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Inventory2
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
import com.tobaxiom.placebo.util.availableIcons
import com.tobaxiom.placebo.util.getIconVector

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StreaksListScreen(
    streaks: List<Streak>,
    onStreakClicked: (Streak) -> Unit,
    onAddStreak: (String, String) -> Unit,
    onEditStreak: (Streak, String, String) -> Unit,
    onArchiveStreak: (Streak) -> Unit,
    onRemoveStreak: (Streak) -> Unit,
    onNavigateToArchive: () -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }
    var streakToEdit by remember { mutableStateOf<Streak?>(null) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Placebo",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                },
                actions = {
                    IconButton(onClick = onNavigateToArchive) {
                        Icon(Icons.Default.Inventory2, contentDescription = "Archived Streaks")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                streakToEdit = null
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
                Text("No active streaks. Press '+' to add one!")
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
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
                            onArchiveClick = { onArchiveStreak(streak) },
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
            onConfirm = { name, iconName ->
                if (streakToEdit == null) {
                    onAddStreak(name, iconName)
                } else {
                    onEditStreak(streakToEdit!!, name, iconName)
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
    onArchiveClick: () -> Unit,
    onRemoveClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clickable(onClick = onItemClick)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(imageVector = getIconVector(streak.iconName), contentDescription = null)
            Spacer(modifier = Modifier.width(16.dp))
            Text(text = streak.name, modifier = Modifier.weight(1f), fontSize = 18.sp)
            IconButton(onClick = onEditClick) {
                Icon(Icons.Default.Edit, contentDescription = "Edit Streak")
            }
            IconButton(onClick = onArchiveClick) {
                Icon(Icons.Default.Archive, contentDescription = "Archive Streak")
            }
        }
    }
}

@Composable
fun AddEditStreakDialog(
    streakToEdit: Streak?,
    onDismiss: () -> Unit,
    onConfirm: (String, String) -> Unit
) {
    var text by remember { mutableStateOf(streakToEdit?.name ?: "") }
    var selectedIconName by remember { mutableStateOf(streakToEdit?.iconName ?: "Star") }
    var showIconPicker by remember { mutableStateOf(false) }
    val isEditing = streakToEdit != null

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (isEditing) "Edit streak" else "Add a new streak") },
        text = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { showIconPicker = true }) {
                    Icon(getIconVector(selectedIconName), contentDescription = "Select Icon")
                }
                Spacer(modifier = Modifier.width(8.dp))
                TextField(
                    value = text,
                    onValueChange = { text = it },
                    label = { Text("Streak Name") },
                    singleLine = true
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (text.isNotBlank()) {
                        onConfirm(text.trim(), selectedIconName)
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

    if (showIconPicker) {
        IconPickerDialog(
            onIconSelected = { iconName ->
                selectedIconName = iconName
                showIconPicker = false
            },
            onDismiss = { showIconPicker = false }
        )
    }
}


@Preview(showBackground = true)
@Composable
fun StreaksListScreenPreview_Empty() {
    PlaceboTheme {
        StreaksListScreen(
            streaks = emptyList(),
            onStreakClicked = {},
            onAddStreak = { _, _ -> },
            onEditStreak = { _, _, _ -> },
            onArchiveStreak = {},
            onRemoveStreak = {},
            onNavigateToArchive = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun StreaksListScreenPreview_WithData() {
    val previewStreaks = remember {
        val streaks = availableIcons.mapIndexed { index, streakIcon ->
            Streak(
                id = index,
                name = "Example for ${streakIcon.name}",
                startDate = System.currentTimeMillis(),
                iconName = streakIcon.name
            )
        }
        mutableStateOf(streaks)
    }
    PlaceboTheme {
        StreaksListScreen(
            streaks = previewStreaks.value,
            onStreakClicked = {},
            onAddStreak = { _, _ -> },
            onEditStreak = { _, _, _ -> },
            onArchiveStreak = {},
            onRemoveStreak = { streak -> previewStreaks.value = previewStreaks.value.filterNot { it.id == streak.id } },
            onNavigateToArchive = {}
        )
    }
}
