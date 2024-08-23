package com.onthecrow.caffeine.ui.easter

import androidx.compose.animation.core.Transition
import androidx.compose.animation.core.animateInt
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.snap
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.core.content.ContextCompat.getDrawable
import com.google.accompanist.drawablepainter.rememberDrawablePainter
import com.onthecrow.caffeine.R

private const val HAMSTER_SIZE_SMALL = 200
private const val HAMSTER_SIZE_MEDIUM = 400
private const val HAMSTER_SIZE_BIG = 600
private const val BASE_DURATION_ANIMATION = 10000

@Composable
fun Hamsters(
    isRunning: Boolean,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val density = LocalDensity.current
    val screenWidth = remember { context.resources.displayMetrics.widthPixels }

    val transition = updateTransition(isRunning, label = "hamster_transition")
    val smallOffsetAnimation = transition.createAnimation(isRunning, screenWidth, HAMSTER_SIZE_SMALL, BASE_DURATION_ANIMATION, 0)
    val mediumOffsetAnimation = transition.createAnimation(isRunning, screenWidth, HAMSTER_SIZE_MEDIUM, 6600, 4500)
    val bigOffsetAnimation = transition.createAnimation(isRunning, screenWidth, HAMSTER_SIZE_BIG, 5000, 3000)

    if (isRunning) {
        Box(modifier) {
            HamsterImage(density, HAMSTER_SIZE_SMALL, smallOffsetAnimation)
            HamsterImage(density, HAMSTER_SIZE_MEDIUM, mediumOffsetAnimation)
            HamsterImage(density, HAMSTER_SIZE_BIG, bigOffsetAnimation)
        }
    }
}

@Composable
private fun BoxScope.HamsterImage(density: Density, hamsterSize: Int, offset: State<Int>) {
    Image(
        modifier = Modifier
            .size(remember { with(density) { hamsterSize.toDp() } })
            .align(Alignment.CenterEnd)
            .graphicsLayer {
                translationX = offset.value.toFloat()
            },
        painter = rememberDrawablePainter(
            drawable = getDrawable(
                LocalContext.current,
                R.drawable.anim_hamster
            )
        ),
        contentDescription = "",
    )
}

@Composable
private fun Transition<Boolean>.createAnimation(isRunning: Boolean, screenWidth: Int, hamsterSize: Int, duration: Int, delay: Int): State<Int> {
    return animateInt(
        transitionSpec = {
            if (isRunning) {
                keyframes {
                    this.durationMillis = duration
                    this.delayMillis = delay
                }
            } else {
                snap()
            }
        },
        label = "hamster_animation_for_size_$hamsterSize"
    ) { state: Boolean ->
        if (state) {
            screenWidth * -1 - hamsterSize
        } else {
            hamsterSize
        }
    }
}