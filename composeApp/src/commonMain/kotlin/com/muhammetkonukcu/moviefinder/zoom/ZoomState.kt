package com.muhammetkonukcu.moviefinder.zoom

import androidx.annotation.FloatRange
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.DecayAnimationSpec
import androidx.compose.animation.core.exponentialDecay
import androidx.compose.animation.core.spring
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.geometry.isUnspecified
import androidx.compose.ui.input.pointer.util.VelocityTracker
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.max

@Stable
class ZoomState(
    @FloatRange(from = 1.0) val maxScale: Float = 5f,
    private var contentSize: Size = Size.Zero,
    private val velocityDecay: DecayAnimationSpec<Float> = exponentialDecay(),
    @FloatRange(from = 1.0) private val initialScale: Float = 1f,
) {
    init {
        require(maxScale >= 1.0f) { "maxScale must be at least 1.0." }
        require(initialScale >= 1.0f) { "initialScale must be at least 1.0." }
    }

    private var _scale = Animatable(initialScale).apply {
        updateBounds(0.9f, maxScale)
    }

    val scale: Float
        get() = _scale.value

    private var _offsetX = Animatable(0f)

    val offsetX: Float
        get() = _offsetX.value

    private var _offsetY = Animatable(0f)

    val offsetY: Float
        get() = _offsetY.value

    private var layoutSize = Size.Zero

    fun setLayoutSize(size: Size) {
        layoutSize = if (size.isUnspecified) Size.Zero else size
        updateFitContentSize()
    }

    private var fitContentSize = Size.Zero
    private fun updateFitContentSize() {
        if (layoutSize == Size.Zero) {
            fitContentSize = Size.Zero
            return
        }

        if (contentSize == Size.Zero) {
            fitContentSize = layoutSize
            return
        }

        val contentAspectRatio = contentSize.width / contentSize.height
        val layoutAspectRatio = layoutSize.width / layoutSize.height

        fitContentSize = if (contentAspectRatio > layoutAspectRatio) {
            contentSize * (layoutSize.width / contentSize.width)
        } else {
            contentSize * (layoutSize.height / contentSize.height)
        }
    }

    private val velocityTracker = VelocityTracker()

    internal fun startGesture() {
        velocityTracker.resetTracking()
    }

    internal fun willChangeOffset(pan: Offset): Boolean {
        var willChange = true
        val ratio = (abs(pan.x) / abs(pan.y))
        if (ratio > 3) {
            if ((pan.x < 0) && (_offsetX.value == _offsetX.lowerBound)) {
                willChange = false
            }
            if ((pan.x > 0) && (_offsetX.value == _offsetX.upperBound)) {
                willChange = false
            }
        } else if (ratio < 0.33) {
            if ((pan.y < 0) && (_offsetY.value == _offsetY.lowerBound)) {
                willChange = false
            }
            if ((pan.y > 0) && (_offsetY.value == _offsetY.upperBound)) {
                willChange = false
            }
        }
        return willChange
    }

    internal suspend fun applyGesture(
        pan: Offset,
        zoom: Float,
        position: Offset,
        timeMillis: Long,
        enableBounce: Boolean = true,
    ) = coroutineScope {
        val minScale = if (enableBounce) 0.9f else 1f
        val newScale = (scale * zoom).coerceIn(minScale, maxScale)
        val newOffset = calculateNewOffset(newScale, position, pan)
        val newBounds = calculateNewBounds(newScale)

        _offsetX.updateBounds(newBounds.left, newBounds.right)
        launch {
            _offsetX.snapTo(newOffset.x)
        }

        _offsetY.updateBounds(newBounds.top, newBounds.bottom)
        launch {
            _offsetY.snapTo(newOffset.y)
        }

        launch {
            _scale.snapTo(newScale)
        }

        if (zoom == 1f) {
            velocityTracker.addPosition(timeMillis, position)
        } else {
            velocityTracker.resetTracking()
        }
    }

    internal fun applyPan(pan: Offset, coroutineScope: CoroutineScope): Offset {
        val bounds = calculateNewBounds(scale)
        val newOffsetX = (_offsetX.value + pan.x).coerceIn(bounds.left, bounds.right)
        val newOffsetY = (_offsetY.value + pan.y).coerceIn(bounds.top, bounds.bottom)
        val consumedX = newOffsetX - _offsetX.value
        val consumedY = newOffsetY - _offsetY.value
        coroutineScope.launch {
            _offsetX.snapTo(newOffsetX)
        }
        coroutineScope.launch {
            _offsetY.snapTo(newOffsetY)
        }
        return Offset(consumedX, consumedY)
    }

    suspend fun changeScale(
        targetScale: Float,
        position: Offset,
        animationSpec: AnimationSpec<Float> = spring(),
    ): Unit = coroutineScope {
        val newScale = targetScale.coerceIn(1f, maxScale)
        val newOffset = calculateNewOffset(newScale, position, Offset.Zero)
        val newBounds = calculateNewBounds(newScale)

        val x = newOffset.x.coerceIn(newBounds.left, newBounds.right)
        launch {
            _offsetX.updateBounds(null, null)
            _offsetX.animateTo(x, animationSpec)
            _offsetX.updateBounds(newBounds.left, newBounds.right)
        }

        val y = newOffset.y.coerceIn(newBounds.top, newBounds.bottom)
        launch {
            _offsetY.updateBounds(null, null)
            _offsetY.animateTo(y, animationSpec)
            _offsetY.updateBounds(newBounds.top, newBounds.bottom)
        }

        launch {
            _scale.animateTo(newScale, animationSpec)
        }
    }

    private fun calculateNewOffset(newScale: Float, position: Offset, pan: Offset): Offset {
        val size = fitContentSize * scale
        val newSize = fitContentSize * newScale
        val deltaWidth = newSize.width - size.width
        val deltaHeight = newSize.height - size.height

        val xInContent = position.x - offsetX + (size.width - layoutSize.width) * 0.5f
        val yInContent = position.y - offsetY + (size.height - layoutSize.height) * 0.5f
        val deltaX = (deltaWidth * 0.5f) - (deltaWidth * xInContent / size.width)
        val deltaY = (deltaHeight * 0.5f) - (deltaHeight * yInContent / size.height)

        val x = offsetX + pan.x + deltaX
        val y = offsetY + pan.y + deltaY

        return Offset(x, y)
    }

    private fun calculateNewBounds(newScale: Float): Rect {
        val newSize = fitContentSize * newScale
        val boundX = max((newSize.width - layoutSize.width), 0f) * 0.5f
        val boundY = max((newSize.height - layoutSize.height), 0f) * 0.5f
        return Rect(-boundX, -boundY, boundX, boundY)
    }

    internal suspend fun startFling() = coroutineScope {
        val velocity = velocityTracker.calculateVelocity()
        if (velocity.x != 0f) {
            launch {
                _offsetX.animateDecay(velocity.x, velocityDecay)
            }
        }
        if (velocity.y != 0f) {
            launch {
                _offsetY.animateDecay(velocity.y, velocityDecay)
            }
        }
    }
}

@Composable
fun rememberZoomState(
    @FloatRange(from = 1.0) maxScale: Float = 5f,
    contentSize: Size = Size.Zero,
    velocityDecay: DecayAnimationSpec<Float> = exponentialDecay(),
    @FloatRange(from = 1.0) initialScale: Float = 1f,
): ZoomState = remember {
    ZoomState(maxScale, contentSize, velocityDecay, initialScale)
}