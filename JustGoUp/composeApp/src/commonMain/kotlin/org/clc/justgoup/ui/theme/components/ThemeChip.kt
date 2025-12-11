package org.clc.justgoup.ui.theme.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.unit.dp
import org.clc.justgoup.ui.theme.BoulderTheme
import org.clc.justgoup.ui.theme.ThemeMode

@Composable
fun ThemeChip(
    label: String,
    mode: ThemeMode,
    current: ThemeMode,
    onChangeTheme: (ThemeMode) -> Unit
) {
    val selected = current == mode

    val bg = if (selected) BoulderTheme.colors.primary else BoulderTheme.colors.surface
    val textColor =
        if (selected) BoulderTheme.colors.textPrimary else BoulderTheme.colors.textSecondary

    Box(
        modifier = Modifier
            .shadow(2.dp, RoundedCornerShape(20.dp))
            .background(bg, RoundedCornerShape(20.dp))
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable { onChangeTheme(mode) }
    ) {
        Text(
            text = label,
            style = BoulderTheme.typography.body,
            color = textColor
        )
    }
}
