package org.clc.justgoup.ui.climbingSessionDetail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import org.clc.justgoup.climbingSession.ClimbingSession
import org.clc.justgoup.ui.helpers.durationMinutes
import org.clc.justgoup.ui.helpers.formatDuration
import org.clc.justgoup.ui.helpers.formatTime
import org.clc.justgoup.ui.theme.BoulderTheme
import org.clc.justgoup.ui.theme.components.BoulderButton
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
@Composable
fun SessionHeader(
    session: ClimbingSession,
    onEndSession: () -> Unit
) {
    val running = session.endTime == null
    val statusText = if (running) "Active session" else "Finished session"
    val durationText = if (session.endTime != null) {
        val minutes = durationMinutes(session.startTime, session.endTime)
        formatDuration(minutes)
    } else {
        "Started at ${
            session.startTime
                .toInstant(TimeZone.UTC)
                .toLocalDateTime(TimeZone.currentSystemDefault())
                .formatTime()
        }"
    }

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = session.location,
            style = BoulderTheme.typography.titleLarge,
            color = BoulderTheme.colors.textPrimary,
            modifier = Modifier.weight(1f)
        )

        Text(
            text = session.location,
            style = BoulderTheme.typography.titleLarge,
            color = BoulderTheme.colors.textPrimary
        )

        Text(
            "$statusText • $durationText",
            style = BoulderTheme.typography.body,
            color = BoulderTheme.colors.textSecondary
        )

        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            StatChip(label = "Boulders", value = session.totalBoulders.toString())
            StatChip(label = "Sends", value = session.totalSends.toString())
        }

        if (running) {
            BoulderButton(
                text = "End this session",
                onClick = onEndSession
            )
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
            label,
            style = BoulderTheme.typography.body,
            color = BoulderTheme.colors.textSecondary
        )
    }
}
