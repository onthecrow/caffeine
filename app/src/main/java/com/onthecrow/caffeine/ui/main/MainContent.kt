package com.onthecrow.caffeine.ui.main

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.airbnb.lottie.RenderMode
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCancellationBehavior
import com.airbnb.lottie.compose.LottieClipSpec
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieAnimatable
import com.airbnb.lottie.compose.rememberLottieComposition
import com.onthecrow.caffeine.R
import com.onthecrow.caffeine.ui.common.ItemSettingsWithSwitch
import com.onthecrow.caffeine.ui.easter.Hamsters
import com.onthecrow.caffeine.ui.footer.SignatureFooter
import kotlinx.coroutines.launch

@Composable
fun MainContent(
    state: MainState,
    togglePersistent: (Boolean) -> Unit,
    toggleRebootPersistent: (Boolean) -> Unit,
    toggleAutomaticTurnOff: (Boolean) -> Unit,
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
    ) {
        val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.anim_coffee_cup))
        val lottieAnimatable = rememberLottieAnimatable()
        val compositionScope = rememberCoroutineScope()
        var enabled by remember { mutableStateOf(false) }

        LaunchedEffect(Unit) {
            lottieAnimatable.snapTo(composition, progress = .5f)
        }

        if (enabled/*state.isActive*/) {
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
                    modifier = Modifier
                        .aspectRatio(1f)
                        .weight(1f, false),
                    composition = lottieAnimatable.composition,
                    progress = { lottieAnimatable.progress },
                    contentScale = ContentScale.Crop,
                    clipToCompositionBounds = false,
                    maintainOriginalImageBounds = false,
                    renderMode = RenderMode.AUTOMATIC,

                    )
                Box(
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                ) {
                    Row(modifier = Modifier.align(Alignment.Center)) {
                        Text(
                            modifier = Modifier.align(alignment = Alignment.CenterVertically),
                            text = "It's",
                            fontSize = 32.sp,
                            style = androidx.compose.ui.text.TextStyle(fontWeight = FontWeight.ExtraBold)
                        )
                        Spacer(modifier = Modifier.size(8.dp))
                        AnimatedContent(
                            modifier = Modifier.align(alignment = Alignment.CenterVertically),
                            targetState = state.isActive,
                            transitionSpec = {
                                if (state.isActive) {
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
                                    maxLines = 1,
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
                }
                Spacer(
                    modifier = Modifier
                        .weight(1f, false)
                )
            }
            ItemSettingsWithSwitch(
                modifier = Modifier.fillMaxWidth(),
                title = "Persistent",
                checked = state.caffeineSettings.isPersistent,
                onCheckedChange = { togglePersistent(it) },
                onClick = { togglePersistent(!state.caffeineSettings.isPersistent) }
            )
            ItemSettingsWithSwitch(
                modifier = Modifier.fillMaxWidth(),
                title = "Restart on reboot",
                checked = state.caffeineSettings.isRebootPersistent,
                onCheckedChange = { toggleRebootPersistent(it) },
                onClick = { toggleRebootPersistent(!state.caffeineSettings.isRebootPersistent) }
            )
            ItemSettingsWithSwitch(
                modifier = Modifier.fillMaxWidth(),
                title = "Automatic turn off",
                subtitle = "Turn Caffeine off on screen off",
                checked = state.caffeineSettings.isAutomaticallyTurnOff,
                onCheckedChange = { toggleAutomaticTurnOff(it) },
                onClick = { toggleAutomaticTurnOff(!state.caffeineSettings.isAutomaticallyTurnOff) }
            )

            Button(
                modifier = Modifier
                    .padding(end = 8.dp)
                    .align(Alignment.End),
                onClick = { enabled = !enabled },
            ) {
                Text(text = "Click me!")
            }
            Hamsters(
                modifier = Modifier.fillMaxWidth(),
                isRunning = enabled,
            )
            Spacer(modifier = Modifier.weight(1f))
            SignatureFooter(modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.size(8.dp))
        }
    }
}

@Preview
@Composable
fun MainContentPreview() {
    MainContent(MainState(), {}, {}, {})
}
