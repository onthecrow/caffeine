package com.onthecrow.caffeine.ui.header

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCancellationBehavior
import com.airbnb.lottie.compose.LottieClipSpec
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieAnimatable
import com.airbnb.lottie.compose.rememberLottieComposition
import com.onthecrow.caffeine.R

@Preview(heightDp = 110)
@Composable
fun CaffeineHeader(isCaffeineActive: Boolean = false, onHeaderClick: (() -> Unit)? = null) {

    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.anim_coffee_cup))
    val lottieAnimatable = rememberLottieAnimatable()

    LaunchedEffect(composition) {
        lottieAnimatable.snapTo(composition, progress = .5f, resetLastFrameNanos = false)
    }

    if (isCaffeineActive) {
        LaunchedEffect(Unit) {
            lottieAnimatable.animate(
                composition,
                iterations = LottieConstants.IterateForever,
                continueFromPreviousAnimate = false,
                initialProgress = .5f,
                clipSpec = LottieClipSpec.Progress(0f, 1f),
                cancellationBehavior = LottieCancellationBehavior.Immediately
            )
        }
    } else if (lottieAnimatable.isPlaying) {
        LaunchedEffect(Unit) {
            if (lottieAnimatable.progress >= .5f) {
                lottieAnimatable.animate(
                    composition,
                    continueFromPreviousAnimate = true,
                    iterations = 0,
                    clipSpec = LottieClipSpec.Progress(lottieAnimatable.progress, 1f),
                    cancellationBehavior = LottieCancellationBehavior.OnIterationFinish,
                )
                lottieAnimatable.animate(
                    composition,
                    continueFromPreviousAnimate = true,
                    iterations = 0,
                    clipSpec = LottieClipSpec.Progress(0f, .5f),
                    cancellationBehavior = LottieCancellationBehavior.OnIterationFinish,
                )
            } else {
                lottieAnimatable.animate(
                    composition,
                    continueFromPreviousAnimate = true,
                    iterations = 0,
                    clipSpec = LottieClipSpec.Progress(lottieAnimatable.progress, .5f),
                    cancellationBehavior = LottieCancellationBehavior.OnIterationFinish,
                )
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(height = 110.dp)
            .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) { onHeaderClick?.invoke() }
    ) {
        // todo animation is stuck if the caffeine is active and it's cold start
        LottieAnimation(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .fillMaxHeight()
                .aspectRatio(1f),
            composition = lottieAnimatable.composition,
            progress = { lottieAnimatable.progress },
            contentScale = ContentScale.Crop,
            clipToCompositionBounds = false,
            maintainOriginalImageBounds = false,
        )
        Box(
            modifier = Modifier
                .align(Alignment.Center)
        ) {
            Row(modifier = Modifier.align(Alignment.Center)) {
                Text(
                    modifier = Modifier.align(alignment = Alignment.CenterVertically),
                    text = "It's",
                    fontSize = 32.sp,
                    style = TextStyle(
                        fontWeight = FontWeight.Medium,
                        platformStyle = PlatformTextStyle(
                            includeFontPadding = false
                        ),
                    )
                )
                Spacer(modifier = Modifier.size(8.dp))
                AnimatedContent(
                    modifier = Modifier.align(alignment = Alignment.CenterVertically),
                    targetState = isCaffeineActive,
                    transitionSpec = {
                        if (isCaffeineActive) {
                            ContentTransform(
                                slideInVertically { height -> height } + fadeIn(),
                                slideOutVertically { height -> -height } + fadeOut(),
                                sizeTransform = SizeTransform(clip = false)
                            )
                        } else {
                            ContentTransform(
                                slideInVertically { height -> -height } + fadeIn(),
                                slideOutVertically { height -> height } + fadeOut(),
                                sizeTransform = SizeTransform(clip = false)
                            )
                        }
                    }, label = "caffeine title animated content"
                ) {
                    if (it) {
                        Text(
                            text = "working",
                            color = MaterialTheme.colorScheme.primary,
                            style = TextStyle(
                                fontWeight = FontWeight.ExtraBold,
                                platformStyle = PlatformTextStyle(
                                    includeFontPadding = false
                                ),
                            ),
                            fontSize = 32.sp,
                        )
                    } else {
                        Text(
                            text = "caffeine",
                            color = MaterialTheme.colorScheme.tertiary,
                            style = TextStyle(
                                fontWeight = FontWeight.ExtraBold,
                                platformStyle = PlatformTextStyle(
                                    includeFontPadding = false
                                ),
                            ),
                            fontSize = 32.sp,
                            maxLines = 1,
                        )
                    }
                }
                Text(
                    modifier = Modifier.align(alignment = Alignment.CenterVertically),
                    text = "!",
                    style = TextStyle(
                        fontWeight = FontWeight.Medium,
                        platformStyle = PlatformTextStyle(
                            includeFontPadding = false
                        ),
                    ),
                    fontSize = 32.sp,
                )
            }
        }
    }
}