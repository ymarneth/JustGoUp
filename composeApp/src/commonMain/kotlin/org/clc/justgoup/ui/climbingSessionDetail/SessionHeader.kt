package org.clc.justgoup.ui.climbingSessionDetail

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.clc.justgoup.boulder.toDisplayString
import org.clc.justgoup.climbingSession.ClimbingSession
import org.clc.justgoup.climbingSession.SessionComparison
import org.clc.justgoup.climbingSession.SessionStats
import org.clc.justgoup.ui.helpers.asShortDateTime
import org.clc.justgoup.ui.theme.BoulderTheme
import org.clc.justgoup.ui.theme.components.StatBar

@Composable
fun SessionHeader(
    session: ClimbingSession,
    stats: SessionStats?
) {
    var statsExpanded by remember { mutableStateOf(false) }

    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = session.location,
                style = BoulderTheme.typography.titleLarge,
                color = BoulderTheme.colors.textPrimary,
            )
            stats?.comparisonToUsual?.let { ComparisonBadge(it) }
        }

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

        if (stats != null) {
            Spacer(Modifier.height(BoulderTheme.spacing.small.dp))

            Text(
                text = if (statsExpanded) "▴ Hide session stats" else "▾ Show session stats",
                style = BoulderTheme.typography.label.copy(color = BoulderTheme.colors.primary),
                modifier = Modifier.clickable { statsExpanded = !statsExpanded }
            )

            if (statsExpanded) {
                Spacer(Modifier.height(BoulderTheme.spacing.small.dp))

                StatBar(label = "Send rate", ratio = stats.sendRate?.toFloat(), modifier = Modifier.fillMaxWidth())

                Spacer(Modifier.height(BoulderTheme.spacing.small.dp))

                StatBar(label = "Flash rate", ratio = stats.flashRate?.toFloat(), modifier = Modifier.fillMaxWidth())

                if (stats.hardestSentBySystem.isNotEmpty()) {
                    Spacer(Modifier.height(BoulderTheme.spacing.small.dp))
                    Text(
                        text = "Hardest sent: " + stats.hardestSentBySystem.values.joinToString(" / ") { grade ->
                            grade.toDisplayString()
                        },
                        style = BoulderTheme.typography.label.copy(color = BoulderTheme.colors.primary)
                    )
                }
            }
        }
    }
}

@Composable
private fun ComparisonBadge(comparison: SessionComparison) {
    val text = when (comparison) {
        SessionComparison.BETTER -> "▲ above usual"
        SessionComparison.WORSE -> "▼ below usual"
        SessionComparison.TYPICAL -> "▬ steady"
    }
    Text(text = text, style = BoulderTheme.typography.label.copy(color = BoulderTheme.colors.textSecondary))
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
