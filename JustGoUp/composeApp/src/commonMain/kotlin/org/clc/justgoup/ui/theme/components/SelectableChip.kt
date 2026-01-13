package org.clc.justgoup.ui.theme.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.clc.justgoup.ui.theme.BoulderTheme

@Composable
fun <T> SelectableChip(
    label: String,
    value: T,
    selectedValue: T,
    onSelect: (T) -> Unit,
    enabled: Boolean = true,
    modifier: Modifier = Modifier
) {
    val selected = value == selectedValue

    val bg = when {
        !enabled -> BoulderTheme.colors.surface.copy(alpha = 0.5f)
        selected -> BoulderTheme.colors.primary
        else -> BoulderTheme.colors.surface
    }

    val textColor = when {
        !enabled -> BoulderTheme.colors.textSecondary.copy(alpha = 0.5f)
        selected -> BoulderTheme.colors.textPrimary
        else -> BoulderTheme.colors.textSecondary
    }

    Box(
        modifier = modifier
            .fillMaxHeight()
            .background(bg, RoundedCornerShape(4.dp))
            .let { if (enabled) it.clickable { onSelect(value) } else it }
            .padding(vertical = 12.dp, horizontal = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            style = BoulderTheme.typography.body,
            color = textColor
        )
    }
}

@Composable
fun ChipDivider() {
    Box(
        modifier = Modifier
            .width(2.dp)
            .fillMaxHeight()
            .background(BoulderTheme.colors.background)
    )
}
