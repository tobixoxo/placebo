package com.tobaxiom.placebo.archive

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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Unarchive
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tobaxiom.placebo.data.Streak
import com.tobaxiom.placebo.util.getIconVector

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArchiveScreen(
    archivedStreaks: List<Streak>,
    onUnarchiveStreak: (Streak) -> Unit,
    onDeletePermanently: (Streak) -> Unit,
    onBackClicked: () -> Unit
) {
    var showDeleteDialog by remember { mutableStateOf<Streak?>(null) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Archived Streaks",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClicked) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            if (archivedStreaks.isEmpty()) {
                Text("No archived streaks.")
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = paddingValues
                ) {
                    items(archivedStreaks, key = { it.id }) { streak ->
                        ArchivedStreakItem(
                            streak = streak,
                            onUnarchiveClick = { onUnarchiveStreak(streak) },
                            onDeleteClick = { showDeleteDialog = streak }
                        )
                    }
                }
            }
        }
    }

    showDeleteDialog?.let { streak ->
        AlertDialog(
            onDismissRequest = { showDeleteDialog = null },
            title = { Text("Delete Streak?") },
            text = { Text("Are you sure you want to permanently delete '${streak.name}'? This action cannot be undone.") },
            confirmButton = {
                Button(onClick = {
                    onDeletePermanently(streak)
                    showDeleteDialog = null
                }) {
                    Text("Delete")
                }
            },
            dismissButton = {
                Button(onClick = { showDeleteDialog = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun ArchivedStreakItem(
    streak: Streak,
    onUnarchiveClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .alpha(0.7f)
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
            IconButton(onClick = onUnarchiveClick) {
                Icon(Icons.Default.Unarchive, contentDescription = "Unarchive Streak")
            }
            IconButton(onClick = onDeleteClick) {
                Icon(Icons.Default.DeleteForever, contentDescription = "Delete Permanently")
            }
        }
    }
}
