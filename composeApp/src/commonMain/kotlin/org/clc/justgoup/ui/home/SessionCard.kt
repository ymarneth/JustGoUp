package org.clc.justgoup.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.clc.justgoup.climbingSession.RecentClimbingSession
import org.clc.justgoup.ui.helpers.asShortDateTime
import org.clc.justgoup.ui.theme.BoulderTheme

@Composable
fun SessionCard(
    session: RecentClimbingSession,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .background(
                color = BoulderTheme.colors.surface,
                shape = RoundedCornerShape(4.dp)
            )
            .padding(BoulderTheme.spacing.medium.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = session.title,
                style = BoulderTheme.typography.titleMedium,
                color = BoulderTheme.colors.textPrimary
            )

            Spacer(Modifier.height(BoulderTheme.spacing.tiny.dp))

            Text(
                text = session.date.asShortDateTime(),
                style = BoulderTheme.typography.body,
                color = BoulderTheme.colors.textSecondary
            )
        }

        Spacer(Modifier.height(BoulderTheme.spacing.tiny.dp))

        Text(
            text = session.location,
            style = BoulderTheme.typography.body,
            color = BoulderTheme.colors.textSecondary
        )

        Spacer(Modifier.height(BoulderTheme.spacing.small.dp))

        Text(
            text = "${session.boulders} boulders",
            style = BoulderTheme.typography.label.copy(color = BoulderTheme.colors.primary)
        )
    }
}
