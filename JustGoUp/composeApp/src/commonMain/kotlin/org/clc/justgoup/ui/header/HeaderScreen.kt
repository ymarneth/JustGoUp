package org.clc.justgoup.ui.header

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.clc.justgoup.ui.theme.BoulderTheme
import org.clc.justgoup.ui.theme.ThemeMode
import org.clc.justgoup.ui.theme.components.ThemeChip

@Composable
fun HeaderScreen(
    currentTheme: ThemeMode,
    onChangeTheme: (ThemeMode) -> Unit
) {
    Spacer(Modifier.height(BoulderTheme.spacing.large.dp))

    // ---- TITLE ----
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Just go up!",
            style = BoulderTheme.typography.titleLarge,
            color = BoulderTheme.colors.textPrimary
        )
    }

    Spacer(Modifier.height(BoulderTheme.spacing.large.dp))

    // ---- THEME SWITCHER SECTION ----
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        ThemeChip("System", ThemeMode.SYSTEM, currentTheme, onChangeTheme)
        ThemeChip("Light", ThemeMode.LIGHT, currentTheme, onChangeTheme)
        ThemeChip("Dark", ThemeMode.DARK, currentTheme, onChangeTheme)
    }

    Spacer(Modifier.height(BoulderTheme.spacing.large.dp))
}
