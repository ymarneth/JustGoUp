package org.clc.justgoup.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.unit.dp
import org.clc.justgoup.climbingSession.RecentClimbingSession
import org.clc.justgoup.ui.theme.BoulderTheme
import org.clc.justgoup.ui.theme.components.BoulderButton
import org.clc.justgoup.ui.theme.components.SessionCard

@Composable
fun HomeScreenContent(
    recentClimbingSessions: List<RecentClimbingSession>,
    onStartSession: () -> Unit,
    onOpenSession: (String) -> Unit
) {
    Column {
        // ---- START NEW SESSION ----
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            BoulderButton(
                text = "Start Session",
                onClick = onStartSession,
                modifier = Modifier
                    .shadow(6.dp, RoundedCornerShape(20.dp))
            )
        }

        Spacer(Modifier.height(BoulderTheme.spacing.extraLarge.dp))

        // ---- RECENT SESSIONS ----
        Text(
            text = "Recent Sessions",
            style = BoulderTheme.typography.titleMedium,
            color =
                BoulderTheme.colors.textPrimary
        )

        Spacer(Modifier.height(BoulderTheme.spacing.medium.dp))

        LazyColumn(
            modifier = Modifier.fillMaxHeight(),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            items(recentClimbingSessions) { session ->
                SessionCard(
                    title = session.title,
                    location = session.location,
                    date = session.date,
                    boulderCount = session.boulders,
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(2.dp, RoundedCornerShape(20.dp))
                        .background(BoulderTheme.colors.surface)
                        .padding(4.dp)
                        .clickable {
                            onOpenSession(session.id)
                        }
                )
            }
        }
    }
}
