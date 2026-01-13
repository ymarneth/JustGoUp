package org.clc.justgoup.ui.climbingSessionDetail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.clc.justgoup.climbingSession.ClimbingSession
import org.clc.justgoup.ui.helpers.asShortDateTime
import org.clc.justgoup.ui.theme.BoulderTheme

@Composable
fun SessionHeader(
    session: ClimbingSession
) {
    Column {
        Text(
            text = session.location,
            style = BoulderTheme.typography.titleLarge,
            color = BoulderTheme.colors.textPrimary,
        )

        Spacer(Modifier.height(BoulderTheme.spacing.tiny.dp))

        Text(
            text = "Started on ${session.startTime.asShortDateTime()}",
            style = BoulderTheme.typography.body,
            color = BoulderTheme.colors.textSecondary,
        )

        Spacer(Modifier.height(BoulderTheme.spacing.medium.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            StatChip(label = "Boulders", value = session.totalBoulders.toString())
            StatChip(label = "Sends", value = session.totalSends.toString())
            StatChip(label = "Flashes", value = session.totalFlashes.toString())
        }
    }
}

@Composable
private fun StatChip(label: String, value: String) {
    Column {
        Text(
            value,
            style = BoulderTheme.typography.titleMedium,
            color = BoulderTheme.colors.textPrimary
        )
        Text(
            text = label,
            style = BoulderTheme.typography.body,
            color = BoulderTheme.colors.textSecondary
        )
    }
}
