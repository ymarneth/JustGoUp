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
    val offsetX = remember { mutableStateOf(0f) }
    val scope = rememberCoroutineScope()
    val threshold = 200f
    val swipeHandled = remember { mutableStateOf(false) }

    Box(
        modifier = Modifier.pointerInput(Unit) {
            awaitEachGesture {
                swipeHandled.value = false
                offsetX.value = 0f

                val down = awaitFirstDown()
                var dragAmountX = 0f
                var isHorizontal = false

                val drag = awaitHorizontalTouchSlopOrCancellation(down.id) { change, _ ->
                    isHorizontal = true
                    change.consume()
                }

                if (drag != null && isHorizontal) {
                    drag(drag.id) { change ->
                        if (swipeHandled.value) return@drag

                        val delta = change.positionChange().x
                        dragAmountX += delta
                        offsetX.value = dragAmountX
                        change.consume()
                    }

                    if (!swipeHandled.value) {
                        swipeHandled.value = true
                        val isSwipe = abs(dragAmountX) > threshold
                        val isRight = dragAmountX > 0

                        if (isSwipe) {
                            if (isRight) onSwipeRight() else onSwipeLeft()

                            scope.launch {
                                val anim = Animatable(dragAmountX)
                                anim.animateTo(
                                    targetValue = if (isRight) 1000f else -1000f,
                                    animationSpec = tween(250)
                                )
                                offsetX.value = 0f
                            }
                        } else {
                            scope.launch {
                                val anim = Animatable(dragAmountX)
                                anim.animateTo(0f, tween(200))
                                offsetX.value = 0f
                            }
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
