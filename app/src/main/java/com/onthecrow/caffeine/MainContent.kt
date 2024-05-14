package com.onthecrow.caffeine

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
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
import kotlinx.coroutines.launch

@Composable
fun MainContent(isActive: MutableState<Boolean> = mutableStateOf(false)) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = /*MaterialTheme.colorScheme.background*/Color.White
    ) {
        val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.anim_coffee_cup))
        val lottieAnimatable = rememberLottieAnimatable()
        val compositionScope = rememberCoroutineScope()
        val density = LocalDensity.current

        LaunchedEffect(Unit) {
            lottieAnimatable.snapTo(composition, progress = .5f)
        }

        if (isActive.value) {
            LaunchedEffect(Unit) {
                compositionScope.launch {
                    lottieAnimatable.animate(
                        composition,
                        iterations = LottieConstants.IterateForever,
                        continueFromPreviousAnimate = false,
                        initialProgress = .5f,
                        clipSpec = LottieClipSpec.Progress(0f, 1f),
                        cancellationBehavior = LottieCancellationBehavior.Immediately
                    )
                }
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

        Column {
            Row {
                LottieAnimation(
                    modifier = Modifier.size(100.dp),
                    composition = lottieAnimatable.composition,
                    progress = { lottieAnimatable.progress }
                )
                Text(
                    modifier = Modifier.align(alignment = Alignment.CenterVertically),
                    text = "It's",
                    fontSize = 32.sp,
                    style = androidx.compose.ui.text.TextStyle(fontWeight = FontWeight.ExtraBold)
                )
                Spacer(modifier = Modifier.size(8.dp))
                AnimatedContent(
                    modifier = Modifier.align(alignment = Alignment.CenterVertically),
                    targetState = isActive.value,
                    transitionSpec = {
                        if (isActive.value) {
                            // If the target number is larger, it slides up and fades in
                            // while the initial (smaller) number slides up and fades out.
                            ContentTransform(
                                slideInVertically { height -> height } + fadeIn(),
                                slideOutVertically { height -> -height } + fadeOut(),
                                sizeTransform = SizeTransform(clip = false)
                            )
//                        slideInVertically { height -> height } + fadeIn() with
//                                slideOutVertically { height -> -height } + fadeOut()
                        } else {
                            // If the target number is smaller, it slides down and fades in
                            // while the initial number slides down and fades out.
                            ContentTransform(
                                slideInVertically { height -> -height } + fadeIn(),
                                slideOutVertically { height -> height } + fadeOut(),
                                sizeTransform = SizeTransform(clip = false)
                            )
                        }
                    }
                ) {
                    if (it) {
                        Text(
                            text = "working",
                            color = MaterialTheme.colorScheme.primary,
                            style = androidx.compose.ui.text.TextStyle(fontWeight = FontWeight.ExtraBold),
                            fontSize = 32.sp,
                        )
                    } else {
                        Text(
                            text = "caffeine",
                            color = MaterialTheme.colorScheme.tertiary,
                            style = androidx.compose.ui.text.TextStyle(fontWeight = FontWeight.ExtraBold),
                            fontSize = 32.sp,
                        )
                    }
                }
                Text(
                    modifier = Modifier.align(alignment = Alignment.CenterVertically),
                    text = "!",
                    style = androidx.compose.ui.text.TextStyle(fontWeight = FontWeight.ExtraBold),
                    fontSize = 32.sp,
                )
            }
            Button(onClick = { isActive.value = isActive.value.not() }) {
                Text(text = "Click me!")
            }
        }
    }
}

@Preview
@Composable
fun MainContentPreview() {
    MainContent()
}
