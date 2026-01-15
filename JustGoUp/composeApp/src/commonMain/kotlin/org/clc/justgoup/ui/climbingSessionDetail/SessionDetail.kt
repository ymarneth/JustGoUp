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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import org.clc.justgoup.di.provideClimbingSessionRepository
import org.clc.justgoup.ui.theme.BoulderTheme
import org.clc.justgoup.ui.theme.components.BoulderButton
import kotlin.math.abs
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.awaitHorizontalTouchSlopOrCancellation
import androidx.compose.foundation.gestures.drag
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.input.pointer.positionChange

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

    val pendingDelete = remember { mutableStateMapOf<String, Boolean>() }

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
                    val isPending = pendingDelete[boulder.id] == true

                    if (isPending) {
                        ConfirmationCard(
                            onCancel = { pendingDelete.remove(boulder.id) },
                            onConfirm = {
                                pendingDelete.remove(boulder.id)
                                localBoulders.remove(boulder)       // remove first for animation
                                viewModel.deleteBoulder(boulder.id)
                            }
                        )
                    } else {
                        SwipeItem(
                            onSwipeLeft = { pendingDelete[boulder.id] = true },
                            onSwipeRight = { pendingDelete[boulder.id] = true }
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
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SwipeItem(
    onSwipeLeft: () -> Unit,
    onSwipeRight: () -> Unit,
    content: @Composable () -> Unit
) {
    val offsetX = remember { Animatable(0f) }
    val scope = rememberCoroutineScope()
    val threshold = 200f

    Box(
        modifier = Modifier.pointerInput(Unit) {
            awaitEachGesture {
                val down = awaitFirstDown()
                var dragAmountX = 0f

                // Wait until it is confirmed to be a horizontal drag
                val drag = awaitHorizontalTouchSlopOrCancellation(down.id) { change, over ->
                    dragAmountX += over
                    change.consume()

                    scope.launch {
                        offsetX.snapTo(dragAmountX)
                    }
                }

                if (drag != null) {
                    drag(drag.id) { change ->
                        val delta = change.positionChange().x
                        dragAmountX += delta
                        change.consume()

                        scope.launch {
                            offsetX.snapTo(dragAmountX)
                        }
                    }

                    // Gesture finished
                    scope.launch {
                        if (abs(dragAmountX) > threshold) {
                            val isRight = dragAmountX > 0

                            offsetX.animateTo(
                                targetValue = if (isRight) 1000f else -1000f,
                                animationSpec = tween(250)
                            )

                            if (isRight) {
                                onSwipeRight()
                            } else {
                                onSwipeLeft()
                            }

                            offsetX.snapTo(0f)
                        } else {
                            offsetX.animateTo(0f, tween(200))
                        }
                    }
                }
                // If drag == null → it was vertical, LazyColumn scrolls
            }
        }
    ) {
        Box(
            modifier = Modifier.offset(x = offsetX.value.dp)
        ) {
            content()
        }
    }
}

@Composable
fun ConfirmationCard(
    onCancel: () -> Unit,
    onConfirm: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(BoulderTheme.colors.surface, shape = RoundedCornerShape(8.dp))
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Delete this boulder?",
                style = BoulderTheme.typography.body,
                color = BoulderTheme.colors.textPrimary
            )

            Row(horizontalArrangement = Arrangement.spacedBy(BoulderTheme.spacing.small.dp)) {
                BoulderButton(
                    text = "Cancel",
                    onClick = onCancel,
                    modifier = Modifier
                )
                BoulderButton(
                    text = "Delete",
                    onClick = onConfirm,
                    modifier = Modifier
                )
            }
        }
    }
}
