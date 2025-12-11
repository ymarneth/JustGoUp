package org.clc.justgoup.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
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
import org.clc.justgoup.ui.theme.ThemeMode
import org.clc.justgoup.ui.theme.components.BoulderButton
import org.clc.justgoup.ui.theme.components.SessionCard
import org.clc.justgoup.ui.theme.components.ThemeChip

@Composable
fun HomeScreen(
    recentClimbingSessions: List<RecentClimbingSession>,
    onStartSession: () -> Unit,
    onOpenSession: (String) -> Unit,
    onChangeTheme: (ThemeMode) -> Unit,
    currentTheme: ThemeMode
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BoulderTheme.colors.background)
            .padding(horizontal = BoulderTheme.spacing.medium.dp)
    ) {
        Spacer(Modifier.height(BoulderTheme.spacing.large.dp))

        // Title
        Text(
            text = "Just Go Up 🧗‍♂️",
            style = BoulderTheme.typography.titleLarge,
            color = BoulderTheme.colors.textPrimary
        )

        Spacer(Modifier.height(BoulderTheme.spacing.large.dp))

        // ---- THEME SWITCHER SECTION ----
        Text(
            text = "Theme",
            style = BoulderTheme.typography.titleMedium,
            color = BoulderTheme.colors.textPrimary
        )

        Spacer(Modifier.height(BoulderTheme.spacing.small.dp))

        // Start new session
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

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            ThemeChip("System", ThemeMode.SYSTEM, currentTheme, onChangeTheme)
            ThemeChip("Light", ThemeMode.LIGHT, currentTheme, onChangeTheme)
            ThemeChip("Dark", ThemeMode.DARK, currentTheme, onChangeTheme)
        }

        Spacer(Modifier.height(BoulderTheme.spacing.extraLarge.dp))

        // Recent sessions
        Text(
            text = "Recent Sessions",
            style = BoulderTheme.typography.titleMedium,
            color = BoulderTheme.colors.textPrimary
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
                )
            }
        }
    }
}
