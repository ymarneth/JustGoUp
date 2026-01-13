package org.clc.justgoup.ui.climbingSessionDetail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
    onAddBoulder: (String) -> Unit,
) {
    val repository = provideClimbingSessionRepository()
    val viewModel = remember { SessionDetailViewModel(repository, sessionId) }

    val session by viewModel.session.collectAsState(initial = null)

    session?.let { session ->
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            SessionHeader(session = session)

            Spacer(Modifier.height(BoulderTheme.spacing.medium.dp))

            BoulderButton(
                text = "Add Boulder",
                onClick = { onAddBoulder(sessionId) },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(BoulderTheme.spacing.large.dp))

            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(BoulderTheme.spacing.medium.dp),
                contentPadding = PaddingValues(bottom = BoulderTheme.spacing.extraLarge.dp)
            ) {
                items(session.boulders) { boulder ->
                    BoulderCard(
                        boulder = boulder,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    } ?: run {
        Box(
            Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Loading session…",
                color = BoulderTheme.colors.textSecondary
            )
        }
    }
}
