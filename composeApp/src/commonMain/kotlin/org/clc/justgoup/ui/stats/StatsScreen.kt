package org.clc.justgoup.ui.stats

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.clc.justgoup.boulder.GradingSystem
import org.clc.justgoup.boulder.toDisplayString
import org.clc.justgoup.climbingSession.ClimbingSessionRepository
import org.clc.justgoup.climbingSession.GymStats
import org.clc.justgoup.climbingSession.GymTrend
import org.clc.justgoup.ui.theme.BoulderTheme
import org.clc.justgoup.ui.theme.components.StatBar
import org.koin.compose.koinInject

@Composable
fun StatsScreen() {
    val repository = koinInject<ClimbingSessionRepository>()
    val viewModel = remember { StatsViewModel(repository) }

    val gymStats by viewModel.gymStats.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Stats",
            style = BoulderTheme.typography.titleMedium,
            color = BoulderTheme.colors.textPrimary
        )

        Spacer(Modifier.height(BoulderTheme.spacing.medium.dp))

        when {
            isLoading -> Unit // first frame only; avoids a flash of the empty-state copy before data arrives
            gymStats.isEmpty() -> EmptyStatsMessage()
            else -> LazyColumn(
                modifier = Modifier.fillMaxHeight(),
                verticalArrangement = Arrangement.spacedBy(BoulderTheme.spacing.medium.dp),
                contentPadding = PaddingValues(bottom = BoulderTheme.spacing.extraLarge.dp)
            ) {
                items(items = gymStats, key = { it.gym }) { stats ->
                    GymStatsCard(stats = stats, modifier = Modifier.fillMaxWidth())
                }
            }
        }
    }
}

@Composable
private fun EmptyStatsMessage() {
    Text(
        text = "Log a session to see your stats here.",
        style = BoulderTheme.typography.body,
        color = BoulderTheme.colors.textSecondary
    )
}

@Composable
private fun GymStatsCard(stats: GymStats, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .background(BoulderTheme.colors.surface, RoundedCornerShape(4.dp))
            .padding(BoulderTheme.spacing.medium.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stats.gym,
                style = BoulderTheme.typography.titleMedium,
                color = BoulderTheme.colors.textPrimary
            )
            stats.trend?.let { TrendBadge(it) }
        }

        Spacer(Modifier.height(BoulderTheme.spacing.tiny.dp))

        Text(
            text = "${stats.sessionCount} sessions • ${stats.boulderCount} boulders",
            style = BoulderTheme.typography.body,
            color = BoulderTheme.colors.textSecondary
        )

        Spacer(Modifier.height(BoulderTheme.spacing.small.dp))

        StatBar(label = "Send rate", ratio = stats.sendRate?.toFloat(), modifier = Modifier.fillMaxWidth())

        Spacer(Modifier.height(BoulderTheme.spacing.small.dp))

        StatBar(label = "Flash rate", ratio = stats.flashRate?.toFloat(), modifier = Modifier.fillMaxWidth())

        if (stats.hardestSentBySystem.isNotEmpty()) {
            Spacer(Modifier.height(BoulderTheme.spacing.small.dp))
            Text(
                text = "Hardest sent: " + stats.hardestSentBySystem.entries.joinToString("  •  ") { (system, grade) ->
                    "${system.label()} ${grade.toDisplayString()}"
                },
                style = BoulderTheme.typography.label.copy(color = BoulderTheme.colors.primary)
            )
        }
    }
}

@Composable
private fun TrendBadge(trend: GymTrend) {
    val (glyph, color) = when (trend) {
        GymTrend.UP -> "▲ improving" to BoulderTheme.colors.success
        GymTrend.DOWN -> "▼ declining" to BoulderTheme.colors.error
        GymTrend.STEADY -> "▬ steady" to BoulderTheme.colors.textSecondary
    }
    Text(text = glyph, style = BoulderTheme.typography.label.copy(color = color))
}

private fun GradingSystem.label(): String = when (this) {
    GradingSystem.FRENCH -> "French"
    GradingSystem.V_SCALE -> "V-Scale"
}
