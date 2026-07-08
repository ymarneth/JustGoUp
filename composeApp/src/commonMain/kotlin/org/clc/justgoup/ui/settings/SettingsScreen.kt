package org.clc.justgoup.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.clc.justgoup.ui.theme.BoulderTheme
import org.clc.justgoup.ui.theme.ThemeMode
import org.clc.justgoup.ui.theme.components.ChipDivider
import org.clc.justgoup.ui.theme.components.SelectableChip

@Composable
fun SettingsScreen(
    currentTheme: ThemeMode,
    onChangeTheme: (ThemeMode) -> Unit,
    onBack: () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        // ---- TOP BAR ----
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = BoulderTheme.spacing.extraLarge.dp)
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = BoulderTheme.colors.textPrimary
                )
            }

            Text(
                text = "Settings",
                style = BoulderTheme.typography.titleLarge,
                color = BoulderTheme.colors.textPrimary
            )
        }

        Spacer(Modifier.height(BoulderTheme.spacing.large.dp))

        // ---- THEME ----
        Text(
            text = "Theme",
            style = BoulderTheme.typography.body,
            color = BoulderTheme.colors.textSecondary
        )

        Spacer(Modifier.height(BoulderTheme.spacing.small.dp))

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
    }
}
