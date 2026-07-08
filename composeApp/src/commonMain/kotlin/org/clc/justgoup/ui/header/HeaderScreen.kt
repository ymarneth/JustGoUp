package org.clc.justgoup.ui.header

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.clc.justgoup.ui.theme.BoulderTheme
import org.clc.justgoup.ui.theme.ThemeMode
import org.clc.justgoup.ui.theme.components.ChipDivider
import org.clc.justgoup.ui.theme.components.SelectableChip

@Composable
fun HeaderScreen(
    currentTheme: ThemeMode,
    onChangeTheme: (ThemeMode) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                top = BoulderTheme.spacing.extraLarge.dp,
            )
    ) {

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

        // ---- THEME SWITCHER ----
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(42.dp)
                .background(BoulderTheme.colors.surface)
        ) {
            SelectableChip(
                label = "System",
                value = ThemeMode.SYSTEM,
                selectedValue = currentTheme,
                onSelect = onChangeTheme,
                modifier = Modifier.weight(1f)
            )
            ChipDivider()
            SelectableChip(
                label = "Light",
                value = ThemeMode.LIGHT,
                selectedValue = currentTheme,
                onSelect = onChangeTheme,
                modifier = Modifier.weight(1f)
            )
            ChipDivider()
            SelectableChip(
                label = "Dark",
                value = ThemeMode.DARK,
                selectedValue = currentTheme,
                onSelect = onChangeTheme,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(Modifier.height(BoulderTheme.spacing.large.dp))
    }
}
