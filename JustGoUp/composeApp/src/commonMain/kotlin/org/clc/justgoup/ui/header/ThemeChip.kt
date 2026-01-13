package org.clc.justgoup.ui.header

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import org.clc.justgoup.ui.theme.BoulderTheme
import org.clc.justgoup.ui.theme.ThemeMode

@Composable
fun ThemeChip(
    label: String,
    mode: ThemeMode,
    current: ThemeMode,
    onChangeTheme: (ThemeMode) -> Unit,
    modifier: Modifier = Modifier
) {
    val selected = current == mode

    val bg = if (selected) BoulderTheme.colors.primary else BoulderTheme.colors.surface
    val textColor =
        if (selected) BoulderTheme.colors.textPrimary else BoulderTheme.colors.textSecondary

    Box(
        modifier = modifier
            .fillMaxHeight()
            .background(bg)
            .clickable { onChangeTheme(mode) }
            .padding(vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            style = BoulderTheme.typography.body,
            color = textColor
        )
    }
}
