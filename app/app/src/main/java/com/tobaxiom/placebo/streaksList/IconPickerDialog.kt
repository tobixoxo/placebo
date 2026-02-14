package com.tobaxiom.placebo.streaksList

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.tobaxiom.placebo.util.availableIcons

@Composable
fun IconPickerDialog(
    onIconSelected: (String) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Select an Icon") },
        text = {
            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = 48.dp),
                modifier = Modifier.padding(top = 16.dp)
            ) {
                items(availableIcons) { icon ->
                    IconButton(onClick = { onIconSelected(icon.name) }) {
                        Icon(icon.vector, contentDescription = icon.name)
                    }
                }
            }
        },
        confirmButton = { /* No confirm button needed */ },
        dismissButton = { /* No dismiss button needed */ }
    )
}
