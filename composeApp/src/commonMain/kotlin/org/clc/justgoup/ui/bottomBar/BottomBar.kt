package org.clc.justgoup.ui.bottomBar

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import org.clc.justgoup.ui.theme.BoulderTheme

@Composable
fun BottomBar(onOpenStats: () -> Unit) {
    BottomAppBar(
        containerColor = BoulderTheme.colors.surface,
        contentColor = BoulderTheme.colors.textPrimary
    ) {
        IconButton(onClick = onOpenStats) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.List,
                contentDescription = "Stats",
                tint = BoulderTheme.colors.textPrimary
            )
        }
    }
}
