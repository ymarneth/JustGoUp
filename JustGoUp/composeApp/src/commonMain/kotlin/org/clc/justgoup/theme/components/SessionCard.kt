package org.clc.justgoup.theme.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import kotlinx.datetime.LocalDateTime
import org.clc.justgoup.helpers.asShortDate
import org.clc.justgoup.theme.BoulderTheme

@Composable
fun SessionCard(
    title: String,
    location: String,
    date: LocalDateTime,
    boulderCount: Int,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .background(BoulderTheme.colors.surface, RoundedCornerShape(20.dp))
            .padding(16.dp)
    ) {
        Text(title, style = BoulderTheme.typography.titleMedium, color = BoulderTheme.colors.textPrimary)
        Spacer(Modifier.height(4.dp))
        Text(date.asShortDate(), style = BoulderTheme.typography.body, color = BoulderTheme.colors.textSecondary)
        Text(location, style = BoulderTheme.typography.body, color = BoulderTheme.colors.textSecondary)
        Spacer(Modifier.height(10.dp))
        Text(
            "$boulderCount boulders",
            style = BoulderTheme.typography.label.copy(color = BoulderTheme.colors.primary)
        )
    }
}
