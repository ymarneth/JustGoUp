package org.clc.justgoup.ui.theme.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.awaitHorizontalTouchSlopOrCancellation
import androidx.compose.foundation.gestures.drag
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChange
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import kotlin.math.abs

@Composable
fun SwipeItem(
    onSwipeLeft: () -> Unit,
    onSwipeRight: () -> Unit,
    content: @Composable () -> Unit
) {
    val offsetX = remember { Animatable(0f) }
    val scope = rememberCoroutineScope()
    val threshold = 200f

    val swipeHandled = remember { mutableStateOf(false) }

    Box(
        modifier = Modifier.pointerInput(Unit) {
            awaitEachGesture {
                swipeHandled.value = false   // reset per gesture

                val down = awaitFirstDown()
                var dragAmountX = 0f

                val drag = awaitHorizontalTouchSlopOrCancellation(down.id) { change, over ->
                    if (swipeHandled.value) return@awaitHorizontalTouchSlopOrCancellation

                    dragAmountX += over
                    change.consume()

                    scope.launch {
                        offsetX.snapTo(dragAmountX)
                    }
                }

                if (drag != null) {
                    drag(drag.id) { change ->
                        if (swipeHandled.value) return@drag

                        val delta = change.positionChange().x
                        dragAmountX += delta
                        change.consume()

                        scope.launch {
                            offsetX.snapTo(dragAmountX)
                        }
                    }

                    scope.launch {
                        if (abs(dragAmountX) > threshold && !swipeHandled.value) {
                            swipeHandled.value = true

                            val isRight = dragAmountX > 0

                            if (isRight) onSwipeRight() else onSwipeLeft()

                            offsetX.animateTo(
                                if (isRight) 1000f else -1000f,
                                tween(250)
                            )

                            offsetX.snapTo(0f)
                        } else if (!swipeHandled.value) {
                            offsetX.animateTo(0f, tween(200))
                        }
                    }
                }
            }
        }
    ) {
        Box(Modifier.offset(x = offsetX.value.dp)) {
            content()
        }
    }
}
