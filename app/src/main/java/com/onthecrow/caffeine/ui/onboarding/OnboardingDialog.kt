package com.onthecrow.caffeine.ui.onboarding

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.onthecrow.caffeine.R
import com.onthecrow.caffeine.data.permission.OnboardingPermission
import com.onthecrow.caffeine.ui.main.OnboardingDialogState
import com.onthecrow.caffeine.ui.main.OnboardingSlideState
import com.onthecrow.caffeine.ui.theme.mintGreen
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OnboardingDialog(
    state: OnboardingDialogState,
    onDismissRequest: () -> Unit,
    onRequestPermission: (OnboardingPermission) -> Unit,
) {
    val coroutineScope = rememberCoroutineScope()
    val slides = state.slides
    if (slides.isEmpty()) return
    Dialog(onDismissRequest = { onDismissRequest() }) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(400.dp),
            shape = RoundedCornerShape(28.dp),
        ) {
            Box(Modifier.fillMaxSize()) {
                val pagerState = rememberPagerState(
                    initialPage = state.initialPage.coerceIn(0, slides.lastIndex),
                    pageCount = { slides.size },
                )
                HorizontalPager(
                    modifier = Modifier.fillMaxSize(),
                    state = pagerState,
                ) { page ->
                    val slide = slides[page]
                    val content = onboardingContentFor(slide.permission)
                    val isGranted = slide.isGranted
                    OnboardingSlide(
                        headline = content.headline,
                        text = content.text,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(top = 24.dp, start = 24.dp, end = 24.dp, bottom = 88.dp),
                        buttonText = if (!isGranted) content.buttonText else null,
                        onButtonClick = if (!isGranted) {
                            { onRequestPermission(slide.permission) }
                        } else null,
                        noteColor = if (isGranted) mintGreen else null,
                        noteText = if (isGranted) content.grantedNote else null,
                        noteImagePainter = if (isGranted) painterResource(R.drawable.ic_check_circle) else null,
                    )
                }
                PagerIndicator(
                    pagerState = pagerState,
                    modifier = Modifier
                        .padding(bottom = 32.dp)
                        .align(Alignment.BottomCenter),
                )
                BackButton(
                    isVisible = pagerState.currentPage != 0,
                    onBackClick = {
                        if (pagerState.currentPage > 0) {
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(
                                    pagerState.currentPage - 1
                                )
                            }
                        }
                    },
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(24.dp),
                )
                ForwardButton(
                    isDone = pagerState.currentPage >= slides.lastIndex,
                    onForwardClick = {
                        if (pagerState.currentPage + 1 < pagerState.pageCount) {
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(
                                    pagerState.currentPage + 1
                                )
                            }
                        }
                    },
                    onDoneClick = { onDismissRequest() },
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(16.dp),
                )
            }
        }
    }
}

private data class OnboardingContent(
    val headline: String,
    val text: String,
    val buttonText: String,
    val grantedNote: String,
)

@Composable
private fun onboardingContentFor(permission: OnboardingPermission): OnboardingContent {
    val grantedNote = stringResource(R.string.onboarding_granted_note)
    return when (permission) {
        OnboardingPermission.OVERLAY -> OnboardingContent(
            headline = stringResource(R.string.onboarding_overlay_headline),
            text = stringResource(R.string.onboarding_overlay_text),
            buttonText = stringResource(R.string.onboarding_grant_button),
            grantedNote = grantedNote,
        )

        OnboardingPermission.NOTIFICATIONS -> OnboardingContent(
            headline = stringResource(R.string.onboarding_notifications_headline),
            text = stringResource(R.string.onboarding_notifications_text),
            buttonText = stringResource(R.string.onboarding_grant_button),
            grantedNote = grantedNote,
        )

        OnboardingPermission.BATTERY_OPTIMIZATION -> OnboardingContent(
            headline = stringResource(R.string.onboarding_battery_headline),
            text = stringResource(R.string.onboarding_battery_text),
            buttonText = stringResource(R.string.onboarding_battery_button),
            grantedNote = grantedNote,
        )

        OnboardingPermission.QUICK_TILE -> OnboardingContent(
            headline = stringResource(R.string.onboarding_tile_headline),
            text = stringResource(R.string.onboarding_tile_text),
            buttonText = stringResource(R.string.onboarding_tile_button),
            grantedNote = grantedNote,
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PagerIndicator(
    pagerState: PagerState,
    modifier: Modifier = Modifier,
) {
    val coroutineScope = rememberCoroutineScope()
    Row(
        modifier
            .height(32.dp)
            .wrapContentWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surface)
            .padding(horizontal = 8.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(pagerState.pageCount) { iteration ->
            val color = if (pagerState.currentPage == iteration) {
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.surfaceVariant
            }
            Box(
                modifier = Modifier
                    .graphicsLayer {
                        if (pagerState.currentPage == iteration) {
                            this.translationX =
                                pagerState.currentPageOffsetFraction * 10.dp.toPx()
                        }
                        if (pagerState.currentPageOffsetFraction > 0 && pagerState.currentPage + 1 == iteration) {
                            this.alpha = 1 - pagerState.currentPageOffsetFraction * 2
                        }
                        if (pagerState.currentPageOffsetFraction < 0 && pagerState.currentPage - 1 == iteration) {
                            this.alpha = 1 - pagerState.currentPageOffsetFraction * -2
                        }
                    }
                    .padding(2.dp)
                    .clip(CircleShape)
                    .background(color)
                    .size(8.dp)
                    .clickable(
                        interactionSource = MutableInteractionSource(),
                        indication = null
                    ) {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(iteration)
                        }
                    }
            )
        }
    }
}

@Composable
fun BackButton(
    isVisible: Boolean,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    AnimatedVisibility(
        modifier = modifier,
        visible = isVisible,
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        IconButton(
            onClick = { onBackClick() },
        ) {
            Image(
                painter = painterResource(R.drawable.ic_arrow_back),
                contentDescription = "",
                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary),
            )
        }
    }
}

@Composable
fun ForwardButton(
    isDone: Boolean,
    onForwardClick: () -> Unit,
    onDoneClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Crossfade(
        modifier = modifier,
        targetState = isDone,
        animationSpec = tween(300),
        label = "Forward button crossfade",
    ) {
        if (it) {
            SmallFloatingActionButton(
                onClick = { onDoneClick() },
                modifier = Modifier.padding(8.dp),
            ) {
                Image(painterResource(R.drawable.ic_done), "")
            }
        } else {
            IconButton(
                onClick = { onForwardClick() },
                modifier = Modifier.padding(8.dp),
            ) {
                Image(
                    painter = painterResource(R.drawable.ic_arrow_forward),
                    contentDescription = "",
                    colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary),
                )
            }
        }
    }
}

@Composable
@Preview(widthDp = 320, heightDp = 640)
private fun OnboardingDialogPreview() {
    OnboardingDialog(
        state = OnboardingDialogState(
            isShowing = true,
            slides = listOf(
                OnboardingSlideState(OnboardingPermission.OVERLAY, isGranted = false),
                OnboardingSlideState(OnboardingPermission.NOTIFICATIONS, isGranted = true),
            ),
        ),
        onDismissRequest = {},
        onRequestPermission = {},
    )
}