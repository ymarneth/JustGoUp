package org.clc.justgoup.ui.theme.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import org.clc.justgoup.ui.theme.BoulderTheme

@Composable
fun BoulderButton(
    text: String,
    enabled: Boolean = true,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val backgroundColor =
        if (enabled) BoulderTheme.colors.primary
        else BoulderTheme.colors.primary.copy(alpha = 0.4f)

    val textColor =
        if (enabled) BoulderTheme.colors.textPrimary
        else BoulderTheme.colors.textPrimary.copy(alpha = 0.6f)

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .clip(RoundedCornerShape(4.dp))
            .background(backgroundColor)
            .padding(vertical = 8.dp, horizontal = 16.dp)
            .clickable(
                enabled = enabled,
                onClick = onClick
            )
    ) {
        Text(
            text = text,
            style = BoulderTheme.typography.titleMedium,
            color = textColor
        )
    }
}
