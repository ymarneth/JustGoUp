package org.clc.justgoup.ui.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import org.clc.justgoup.di.provideClimbingSessionRepository
import org.clc.justgoup.ui.theme.BoulderTheme
import org.clc.justgoup.ui.theme.components.BoulderButton
import org.clc.justgoup.ui.theme.components.ConfirmationCard
import org.clc.justgoup.ui.theme.components.SwipeItem

@Composable
fun Home(
    onStartSession: () -> Unit,
    onOpenSession: (String) -> Unit
) {
    val repository = provideClimbingSessionRepository()
    val viewModel = remember { HomeViewModel(repository) }

    val recentSessions by viewModel.recentSessions.collectAsState(initial = emptyList())

    val localSessions = remember(recentSessions) { recentSessions.toMutableStateList() }
    val pendingDeleteId = remember { mutableStateOf<String?>(null) }
    val density = LocalDensity.current

    Column {
        // ---- START NEW SESSION ----
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            BoulderButton(
                text = "Start Session",
                onClick = { onStartSession() },
                modifier = Modifier.fillMaxWidth()
            )
        }

        Spacer(Modifier.height(BoulderTheme.spacing.large.dp))

        // ---- RECENT SESSIONS ----
        Text(
            text = "Recent Sessions",
            style = BoulderTheme.typography.titleMedium,
            color = BoulderTheme.colors.textPrimary
        )

        Spacer(Modifier.height(BoulderTheme.spacing.medium.dp))

        LazyColumn(
            modifier = Modifier.fillMaxHeight(),
            verticalArrangement = Arrangement.spacedBy(BoulderTheme.spacing.medium.dp),
            contentPadding = PaddingValues(bottom = BoulderTheme.spacing.extraLarge.dp)
        ) {
            items(
                items = localSessions,
                key = { session -> session.id }
            ) { session ->
                val isPending = pendingDeleteId.value == session.id
                val cardHeight = remember { mutableStateOf(0.dp) }

                if (isPending) {
                    ConfirmationCard(
                        message = "Delete this session?",
                        height = cardHeight.value,
                        onCancel = { pendingDeleteId.value = null },
                        onConfirm = {
                            pendingDeleteId.value = null
                            localSessions.remove(session)
                            viewModel.deleteSession(session.id)
                        }
                    )
                } else {
                    SwipeItem(
                        onSwipeLeft = { pendingDeleteId.value = session.id },
                        onSwipeRight = { pendingDeleteId.value = session.id }
                    ) {
                        SessionCard(
                            session = session,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onOpenSession(session.id) }
                                .onGloballyPositioned { coordinates ->
                                    cardHeight.value = with(density) { coordinates.size.height.toDp() }
                                }
                        )
                    }
                }
            }
        }
    }
}
