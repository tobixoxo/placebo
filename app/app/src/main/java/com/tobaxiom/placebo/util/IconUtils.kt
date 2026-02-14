package com.tobaxiom.placebo.util

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Brush
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.PedalBike
import androidx.compose.material.icons.filled.SportsBasketball
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Theaters
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * A data class to hold the metadata for an icon that a user can select.
 */
data class StreakIcon(val name: String, val vector: ImageVector)

/**
 * A predefined list of all icons available for the user to choose from.
 */
val availableIcons = listOf(
    StreakIcon("Star", Icons.Default.Star),
    StreakIcon("Sports", Icons.Default.SportsBasketball),
    StreakIcon("Book", Icons.Default.Book),
    StreakIcon("Fitness", Icons.Default.FitnessCenter),
    StreakIcon("Code", Icons.Default.Code),
    StreakIcon("Theaters", Icons.Default.Theaters),
    StreakIcon("PedalBike", Icons.Default.PedalBike),
    StreakIcon("Music", Icons.Default.MusicNote),
    StreakIcon("Art", Icons.Default.Brush),
    StreakIcon("Hydrate", Icons.Default.WaterDrop)
)

// A private map for efficient lookup of an icon by its name.
private val iconMap = availableIcons.associateBy { it.name }

/**
 * Returns the ImageVector corresponding to the given icon name.
 * Defaults to a Star icon if the name is not found.
 */
fun getIconVector(iconName: String): ImageVector {
    return iconMap[iconName]?.vector ?: Icons.Default.Star
}
