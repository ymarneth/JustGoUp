package org.clc.justgoup.ui.theme.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import org.clc.justgoup.ui.theme.BoulderTheme

@Composable
fun BoulderButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .background(
                color = BoulderTheme.colors.primary,
                shape = RoundedCornerShape(18.dp)
            )
            .clickable { onClick() }
            .padding(
                horizontal = BoulderTheme.spacing.medium.dp,
                vertical = BoulderTheme.spacing.small.dp
            )
    ) {
        Text(
            text = text,
            style = BoulderTheme.typography.label,
            color = Color.White
        )
    }
}
