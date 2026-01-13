package org.clc.justgoup.ui.header

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.clc.justgoup.ui.theme.BoulderTheme
import org.clc.justgoup.ui.theme.ThemeMode

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
                .background(
                    BoulderTheme.colors.surface,
                    RoundedCornerShape(8.dp)
                )
        ) {
            ThemeChip("System", ThemeMode.SYSTEM, currentTheme, onChangeTheme, Modifier.weight(1f))
            Divider()
            ThemeChip("Light", ThemeMode.LIGHT, currentTheme, onChangeTheme, Modifier.weight(1f))
            Divider()
            ThemeChip("Dark", ThemeMode.DARK, currentTheme, onChangeTheme, Modifier.weight(1f))
        }

        Spacer(Modifier.height(BoulderTheme.spacing.large.dp))
    }
}


@Composable
private fun Divider() {
    Box(
        modifier = Modifier
            .width(2.dp)
            .fillMaxHeight()
            .background(BoulderTheme.colors.background)
    )
}
