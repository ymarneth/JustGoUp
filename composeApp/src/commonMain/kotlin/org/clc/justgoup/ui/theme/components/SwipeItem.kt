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
import androidx.compose.ui.input.pointer.util.VelocityTracker
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.roundToInt

@Composable
fun SwipeItem(
    onSwipeLeft: () -> Unit,
    onSwipeRight: () -> Unit,
    content: @Composable () -> Unit
) {
    val density = LocalDensity.current
    val offsetX = remember { mutableStateOf(0f) }
    val scope = rememberCoroutineScope()
    val thresholdPx = with(density) { 96.dp.toPx() }
    val flingVelocityPx = with(density) { 800.dp.toPx() }
    val flyOutPx = with(density) { 600.dp.toPx() }

    Box(
        modifier = Modifier.pointerInput(Unit) {
            awaitEachGesture {
                offsetX.value = 0f
                val velocityTracker = VelocityTracker()

                val down = awaitFirstDown()
                velocityTracker.addPosition(down.uptimeMillis, down.position)
                var isHorizontal = false

                val drag = awaitHorizontalTouchSlopOrCancellation(down.id) { change, _ ->
                    isHorizontal = true
                    change.consume()
                }

                if (drag != null && isHorizontal) {
                    drag(drag.id) { change ->
                        velocityTracker.addPosition(change.uptimeMillis, change.position)
                        offsetX.value += change.positionChange().x
                        change.consume()
                    }

                    val velocity = velocityTracker.calculateVelocity().x
                    val isFling = abs(velocity) > flingVelocityPx
                    val isSwipe = abs(offsetX.value) > thresholdPx || isFling
                    val isRight = if (isFling) velocity > 0 else offsetX.value > 0

                    scope.launch {
                        val anim = Animatable(offsetX.value)
                        if (isSwipe) {
                            anim.animateTo(
                                targetValue = if (isRight) flyOutPx else -flyOutPx,
                                animationSpec = tween(200)
                            ) { offsetX.value = value }
                            if (isRight) onSwipeRight() else onSwipeLeft()
                        } else {
                            anim.animateTo(0f, tween(200)) { offsetX.value = value }
                        }
                    }
                }
            }
        }
    ) {
        Box(Modifier.offset { IntOffset(offsetX.value.roundToInt(), 0) }) {
            content()
        }
    }
}
