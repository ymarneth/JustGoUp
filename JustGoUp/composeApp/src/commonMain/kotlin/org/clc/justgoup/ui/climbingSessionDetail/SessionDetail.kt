package org.clc.justgoup.ui.climbingSessionDetail

import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import org.clc.justgoup.ui.theme.components.SwipeItem

@Composable
fun SessionDetailScreen(
    sessionId: String,
    onAddBoulder: (String) -> Unit,
) {
    val repository = provideClimbingSessionRepository()
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

@Composable
fun ConfirmationCard(
    onCancel: () -> Unit,
    onConfirm: () -> Unit,
    height: Dp = Dp.Unspecified,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .widthIn(max = 400.dp)
            .height(height)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            Text(
                text = "Delete this boulder?",
                style = BoulderTheme.typography.body,
                color = BoulderTheme.colors.textPrimary
            )

            Spacer(modifier = Modifier.height(BoulderTheme.spacing.medium.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(BoulderTheme.spacing.small.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                BoulderButton(
                    text = "Cancel",
                    onClick = onCancel,
                    modifier = Modifier.weight(1f)
                )
                BoulderButton(
                    text = "Delete",
                    onClick = onConfirm,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}
