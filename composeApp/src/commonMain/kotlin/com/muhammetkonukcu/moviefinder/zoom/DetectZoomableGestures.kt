package com.muhammetkonukcu.moviefinder.zoom

import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.calculateCentroid
import androidx.compose.foundation.gestures.calculatePan
import androidx.compose.foundation.gestures.calculateZoom
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.AwaitPointerEventScope
import androidx.compose.ui.input.pointer.PointerEvent
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.PointerEventTimeoutCancellationException
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.PointerInputScope
import androidx.compose.ui.input.pointer.positionChanged
import androidx.compose.ui.util.fastAny
import androidx.compose.ui.util.fastForEach

internal suspend fun PointerInputScope.detectZoomableGestures(
    enableOneFingerZoom: () -> Boolean,
    canConsumeGesture: (pan: Offset, zoom: Float) -> Boolean,
    onGesture: (centroid: Offset, pan: Offset, zoom: Float, timeMillis: Long) -> Unit,
    onGestureStart: () -> Unit = {},
    onGestureEnd: () -> Unit = {},
    onTap: ((position: Offset) -> Unit)? = null,
    onDoubleTap: ((position: Offset) -> Unit)? = null,
    onLongPress: ((position: Offset) -> Unit)? = null,
) = awaitEachGesture {
    val firstDown = awaitFirstDown(requireUnconsumed = false)
    if (onTap != null || onDoubleTap != null || onLongPress != null || enableOneFingerZoom()) {
        firstDown.consume()
    }
    onGestureStart()
    detectGesture(
        canConsumeGesture = canConsumeGesture,
        onGesture = onGesture,
        onTap = onTap,
        onDoubleTap = onDoubleTap,
        onLongPress = onLongPress,
        enableOneFingerZoom = enableOneFingerZoom,
    )
    onGestureEnd()
}

private suspend fun AwaitPointerEventScope.detectGesture(
    enableOneFingerZoom: () -> Boolean,
    canConsumeGesture: (pan: Offset, zoom: Float) -> Boolean,
    onGesture: (centroid: Offset, pan: Offset, zoom: Float, timeMillis: Long) -> Unit,
    onTap: ((position: Offset) -> Unit)?,
    onDoubleTap: ((position: Offset) -> Unit)?,
    onLongPress: ((position: Offset) -> Unit)?,
) {
    val startPosition = currentEvent.changes[0].position
    var event = try {
        withTimeout(viewConfiguration.longPressTimeoutMillis) {
            awaitTouchSlop()
        } ?: return
    } catch (_: PointerEventTimeoutCancellationException) {
        onLongPress?.invoke(startPosition)
        consumeAllEventsUntilReleased()
        return
    }

    var hasMoved = false
    while (event.isPressed) {
        val zoomChange = event.calculateZoom()
        val panChange = event.calculatePan()
        if (zoomChange != 1f || panChange != Offset.Zero) {
            val centroid = event.calculateCentroid(useCurrent = true)
            val timeMillis = event.changes[0].uptimeMillis
            if (canConsumeGesture(panChange, zoomChange)) {
                onGesture(centroid, panChange, zoomChange, timeMillis)
                event.consumePositionChanges()
            }
        }
        hasMoved = true
        if (event.isPointerReducedToOne) {
            break
        }
        event = awaitEvent() ?: return
    }
    if (hasMoved) {
        return
    }
    val firstUp = event.changes[0]

    if (onDoubleTap == null && !enableOneFingerZoom()) {
        onTap?.invoke(firstUp.position)
        return
    }

    val secondDown = awaitSecondDown(firstUp)
    if (secondDown == null) {
        onTap?.invoke(firstUp.position)
        return
    }
    secondDown.consume()

    event = awaitTouchSlop() ?: return
    if (!event.isPressed) {
        val pressedTime = event.changes[0].uptimeMillis - secondDown.uptimeMillis
        if (pressedTime < viewConfiguration.longPressTimeoutMillis) {
            onDoubleTap?.invoke(event.changes[0].position)
        }
        return
    }

    if (!enableOneFingerZoom()) return

    while (event.isPressed) {
        val panChange = event.calculatePan()
        val zoomChange = 1f + panChange.y * 0.004f
        if (zoomChange != 1f) {
            val centroid = event.calculateCentroid(useCurrent = true)
            val timeMillis = event.changes[0].uptimeMillis
            if (canConsumeGesture(Offset.Zero, zoomChange)) {
                onGesture(centroid, Offset.Zero, zoomChange, timeMillis)
                event.consumePositionChanges()
            }
        }
        event = awaitEvent() ?: return
    }
}

private suspend fun AwaitPointerEventScope.awaitTouchSlop(): PointerEvent? {
    val touchSlop = TouchSlop(viewConfiguration.touchSlop)
    while (true) {
        val mainEvent = awaitPointerEvent(pass = PointerEventPass.Main)
        if (mainEvent.changes.fastAny { it.isConsumed }) {
            return null
        }

        if (mainEvent.changes.none { it.pressed }) {
            return mainEvent
        }

        if (touchSlop.isPast(mainEvent)) {
            return mainEvent
        }

        val finalEvent = awaitPointerEvent(pass = PointerEventPass.Final)
        if (finalEvent.changes.fastAny { it.isConsumed }) {
            return null
        }
    }
}

private suspend fun AwaitPointerEventScope.awaitEvent(): PointerEvent? {
    val mainEvent = awaitPointerEvent(pass = PointerEventPass.Main)
    if (mainEvent.changes.fastAny { it.isConsumed }) {
        return null
    }

    return mainEvent
}

private suspend fun AwaitPointerEventScope.consumeAllEventsUntilReleased() {
    do {
        val event = awaitEvent() ?: return
        event.changes.fastForEach { it.consume() }
    } while (event.isPressed)
}

private val PointerEvent.isPressed
    get() = changes.fastAny { it.pressed }

private val PointerEvent.isPointerReducedToOne
    get() = changes.count { it.previousPressed } > 1 && changes.count { it.pressed } == 1

private suspend fun AwaitPointerEventScope.awaitSecondDown(
    firstUp: PointerInputChange,
): PointerInputChange? = withTimeoutOrNull(viewConfiguration.doubleTapTimeoutMillis) {
    val minUptime = firstUp.uptimeMillis + viewConfiguration.doubleTapMinTimeMillis
    var change: PointerInputChange
    do {
        change = awaitFirstDown()
    } while (change.uptimeMillis < minUptime)
    change
}

private fun PointerEvent.consumePositionChanges() {
    changes.fastForEach {
        if (it.positionChanged()) {
            it.consume()
        }
    }
}

private class TouchSlop(private val threshold: Float) {
    private var pan = Offset.Zero
    private var past = false

    fun isPast(event: PointerEvent): Boolean {
        if (past) {
            return true
        }

        if (event.changes.size > 1) {
            past = true
        } else {
            pan += event.calculatePan()
            past = pan.getDistance() > threshold
        }

        return past
    }
}