package org.clc.justgoup.ui.theme.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.clc.justgoup.ui.theme.BoulderTheme
import kotlin.math.roundToInt

@Composable
fun StatBar(
    label: String,
    ratio: Float?,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = label,
                style = BoulderTheme.typography.label,
                color = BoulderTheme.colors.textSecondary
            )
            Text(
                text = ratio?.let { "${(it * 100).roundToInt()}%" } ?: "—",
                style = BoulderTheme.typography.label,
                color = BoulderTheme.colors.textPrimary
            )
        }

        Spacer(Modifier.height(BoulderTheme.spacing.tiny.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .background(BoulderTheme.colors.background, RoundedCornerShape(4.dp))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(fraction = (ratio ?: 0f).coerceIn(0f, 1f))
                    .background(BoulderTheme.colors.primary, RoundedCornerShape(4.dp))
            )
        }
    }
}
