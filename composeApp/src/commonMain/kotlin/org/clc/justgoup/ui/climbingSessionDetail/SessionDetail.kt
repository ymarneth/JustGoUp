package org.clc.justgoup.ui.climbingSessionDetail

import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.clc.justgoup.climbingSession.ClimbingSessionRepository
import org.clc.justgoup.ui.theme.BoulderTheme
import org.clc.justgoup.ui.theme.components.BoulderButton
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import org.clc.justgoup.ui.theme.components.ConfirmationCard
import org.clc.justgoup.ui.theme.components.SwipeItem
import org.koin.compose.koinInject

@Composable
fun SessionDetailScreen(
    sessionId: String,
    onAddBoulder: (String) -> Unit,
) {
    val repository = koinInject<ClimbingSessionRepository>()
    val viewModel = remember { SessionDetailViewModel(repository, sessionId) }
    val session by viewModel.session.collectAsState(initial = null)

    val localBoulders = remember(session?.boulders) {
        session?.boulders?.toMutableStateList() ?: mutableStateListOf()
    }

    val pendingDeleteId = remember { mutableStateOf<String?>(null) }
    val density = LocalDensity.current

    session?.let {
        Column(modifier = Modifier.fillMaxSize()) {
            SessionHeader(session = it)

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
                items(
                    items = localBoulders,
                    key = { b -> b.id }
                ) { boulder ->
                    val isPending = pendingDeleteId.value == boulder.id
                    val cardHeight = remember { mutableStateOf(0.dp) }

                    if (isPending) {
                        ConfirmationCard(
                            message = "Delete this boulder?",
                            height = cardHeight.value,
                            onCancel = { pendingDeleteId.value = null },
                            onConfirm = {
                                pendingDeleteId.value = null
                                localBoulders.remove(boulder)
                                viewModel.deleteBoulder(boulder.id)
                            }
                        )
                    } else {
                        SwipeItem(
                            onSwipeLeft = { pendingDeleteId.value = boulder.id },
                            onSwipeRight = { pendingDeleteId.value = boulder.id }
                        ) {
                            BoulderCard(
                                boulder = boulder,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .animateItem(
                                        placementSpec = tween(
                                            durationMillis = 1000,
                                            easing = FastOutSlowInEasing
                                        )
                                    )
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
}
