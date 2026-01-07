package org.clc.justgoup.ui.climbingSessionDetail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.clc.justgoup.di.provideClimbingSessionRepository
import org.clc.justgoup.ui.theme.BoulderTheme
import org.clc.justgoup.ui.theme.components.BoulderButton

@Composable
fun SessionDetailScreen(
    sessionId: String,
) {
    val repository = provideClimbingSessionRepository()
    val viewModel = remember { SessionDetailViewModel(repository, sessionId) }

    val session by viewModel.session.collectAsState(initial = null)

    session?.let { session ->
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(BoulderTheme.spacing.large.dp),
            contentPadding = PaddingValues(
                bottom = BoulderTheme.spacing.extraLarge.dp
            )
        ) {
            item {
                SessionHeader(
                    session = session,
                    onEndSession = { viewModel.endClimbingSession(sessionId) }
                )
            }
            item {
                BoulderButton(
                    text = "+ Add Boulder",
                    onClick = { viewModel.addBoulderToSession(sessionId) }
                )
            }
            items(session.boulders) { boulder ->
                BoulderCard(boulder = boulder)
            }
        }
    } ?: run {
        Box(
            Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("Loading session…", color = BoulderTheme.colors.textSecondary)
        }
    }
}
