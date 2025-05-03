package com.muhammetkonukcu.moviefinder.zoom

import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScrollModifierNode
import androidx.compose.ui.input.pointer.PointerEvent
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.SuspendingPointerInputModifierNode
import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.layout.MeasureResult
import androidx.compose.ui.layout.MeasureScope
import androidx.compose.ui.node.DelegatingNode
import androidx.compose.ui.node.LayoutModifierNode
import androidx.compose.ui.node.ModifierNodeElement
import androidx.compose.ui.node.PointerInputModifierNode
import androidx.compose.ui.platform.InspectorInfo
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.toSize
import kotlinx.coroutines.launch

fun Modifier.pinchToZoomSnapBack(
    zoomState: ZoomState,
    zoomEnabled: Boolean = true,
): Modifier = this then ZoomElement(
    zoomState = zoomState,
    zoomEnabled = zoomEnabled,
    enableOneFingerZoom = false,
    scrollGesturePropagation = ScrollGesturePropagation.NotZoomed,
    enableNestedScroll = false,
)

private data class ZoomElement(
    val zoomState: ZoomState,
    val zoomEnabled: Boolean,
    val enableOneFingerZoom: Boolean,
    val scrollGesturePropagation: ScrollGesturePropagation,
    val enableNestedScroll: Boolean,
) : ModifierNodeElement<ZoomableNode>() {
    override fun create(): ZoomableNode = ZoomableNode(
        zoomState,
        zoomEnabled,
        enableOneFingerZoom,
        scrollGesturePropagation,
        enableNestedScroll
    )

    override fun update(node: ZoomableNode) {
        node.update(
            zoomState,
            zoomEnabled,
            enableOneFingerZoom,
            scrollGesturePropagation,
        )
    }

    override fun InspectorInfo.inspectableProperties() {
        name = "zoomable"
        properties["zoomState"] = zoomState
        properties["zoomEnabled"] = zoomEnabled
        properties["enableOneFingerZoom"] = enableOneFingerZoom
        properties["scrollGesturePropagation"] = scrollGesturePropagation
        properties["enableNestedScroll"] = enableNestedScroll
    }
}

private class ZoomableNode(
    var zoomState: ZoomState,
    var zoomEnabled: Boolean,
    var enableOneFingerZoom: Boolean,
    var scrollGesturePropagation: ScrollGesturePropagation,
    enableNestedScroll: Boolean,
) : PointerInputModifierNode, LayoutModifierNode, DelegatingNode() {
    var measuredSize = Size.Zero

    fun update(
        zoomState: ZoomState,
        zoomEnabled: Boolean,
        enableOneFingerZoom: Boolean,
        scrollGesturePropagation: ScrollGesturePropagation,
    ) {
        if (this.zoomState != zoomState) {
            zoomState.setLayoutSize(measuredSize)
            this.zoomState = zoomState
        }
        this.zoomEnabled = zoomEnabled
        this.enableOneFingerZoom = enableOneFingerZoom
        this.scrollGesturePropagation = scrollGesturePropagation
    }

    init {
        if (enableNestedScroll) {
            delegate(
                nestedScrollModifierNode(
                    connection = object : NestedScrollConnection {
                        override fun onPostScroll(
                            consumed: Offset,
                            available: Offset,
                            source: NestedScrollSource,
                        ): Offset {
                            return zoomState.applyPan(
                                pan = available,
                                coroutineScope = coroutineScope
                            )
                        }
                    },
                    dispatcher = null,
                )
            )
        }
    }

    val pointerInputNode = delegate(
        SuspendingPointerInputModifierNode {
            detectZoomableGestures(
                onGestureStart = {
                    resetConsumeGesture()
                    zoomState.startGesture()
                },
                canConsumeGesture = { pan, zoom ->
                    zoomEnabled && canConsumeGesture(pan, zoom)
                },
                onGesture = { centroid, pan, zoom, timeMillis ->
                    if (zoomEnabled) {
                        coroutineScope.launch {
                            zoomState.applyGesture(
                                pan = pan,
                                zoom = zoom,
                                position = centroid,
                                timeMillis = timeMillis,
                            )
                        }
                    }
                },
                onGestureEnd = {
                    coroutineScope.launch {
                        if (zoomState.scale != 1f) {
                            zoomState.changeScale(1f, Offset.Zero)
                        } else {
                            zoomState.startFling()
                        }
                    }
                },
                enableOneFingerZoom = { enableOneFingerZoom },
            )
        }
    )

    private var consumeGesture: Boolean? = null

    private fun resetConsumeGesture() {
        consumeGesture = null
    }

    private fun canConsumeGesture(pan: Offset, zoom: Float): Boolean {
        val currentValue = consumeGesture
        if (currentValue != null) {
            return currentValue
        }

        val newValue = when {
            zoom != 1f -> true
            zoomState.scale == 1f -> false
            scrollGesturePropagation == ScrollGesturePropagation.NotZoomed -> true
            else -> zoomState.willChangeOffset(pan)
        }
        consumeGesture = newValue
        return newValue
    }

    override fun onPointerEvent(
        pointerEvent: PointerEvent,
        pass: PointerEventPass,
        bounds: IntSize,
    ) {
        pointerInputNode.onPointerEvent(pointerEvent, pass, bounds)
    }

    override fun onCancelPointerInput() {
        pointerInputNode.onCancelPointerInput()
    }

    override fun MeasureScope.measure(
        measurable: Measurable,
        constraints: Constraints,
    ): MeasureResult {
        val placeable = measurable.measure(constraints)
        measuredSize = IntSize(placeable.measuredWidth, placeable.measuredHeight).toSize()
        zoomState.setLayoutSize(measuredSize)
        return layout(placeable.width, placeable.height) {
            placeable.placeWithLayer(x = 0, y = 0) {
                scaleX = zoomState.scale
                scaleY = zoomState.scale
                translationX = zoomState.offsetX
                translationY = zoomState.offsetY
            }
        }
    }
}