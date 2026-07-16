package org.clc.justgoup.ui.bottomBar

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.clc.justgoup.ui.theme.BoulderTheme
import org.clc.justgoup.ui.theme.icons.StatsIcon

private val StatsIconSize = 32.dp

@Composable
fun BottomBar(onOpenStats: () -> Unit) {
    BottomAppBar(
        containerColor = BoulderTheme.colors.surface,
        contentColor = BoulderTheme.colors.textPrimary
    ) {
        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            IconButton(onClick = onOpenStats) {
                Icon(
                    imageVector = StatsIcon,
                    contentDescription = "Stats",
                    tint = BoulderTheme.colors.textPrimary,
                    modifier = Modifier.size(StatsIconSize)
                )
            }
        }
    }
}
