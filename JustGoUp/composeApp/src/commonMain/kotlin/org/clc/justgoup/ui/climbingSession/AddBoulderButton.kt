package org.clc.justgoup.ui.climbingSession

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
fun AddBoulderButton(onAdd: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(BoulderTheme.colors.primary)
            .clickable { onAdd() }
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            "+ Add Boulder",
            style = BoulderTheme.typography.titleMedium,
            color = BoulderTheme.colors.textPrimary
        )
    }
}
