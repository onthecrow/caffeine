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
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.onthecrow.caffeine.R
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OnboardingDialog(onDismissRequest: () -> Unit) {
    val coroutineScope = rememberCoroutineScope()
    Dialog(onDismissRequest = { onDismissRequest() }) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(400.dp),
            shape = RoundedCornerShape(28.dp),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(PaddingValues(24.dp)),
            ) {
                val pagerState = rememberPagerState(pageCount = {
                    5
                })
                HorizontalPager(
                    modifier = Modifier.fillMaxSize(),
                    state = pagerState,
                ) { page ->
                    Text(
                        text = "Page: $page",
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
                Row(
                    Modifier
                        .height(32.dp)
                        .wrapContentWidth()
                        .align(Alignment.TopCenter)
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
                BackButton(pagerState.currentPage != 0)
                ForwardButton(pagerState.currentPage >= 4)
            }
        }
    }
}

@Composable
fun BoxScope.BackButton(isVisible: Boolean) {
    AnimatedVisibility(
        modifier = Modifier.align(Alignment.BottomStart),
        visible = isVisible,
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        SmallFloatingActionButton(
            onClick = {},
        ) {
            Image(painterResource(R.drawable.ic_arrow_back), "")
        }
    }
}

@Composable
fun BoxScope.ForwardButton(isDone: Boolean) {
    SmallFloatingActionButton(
        modifier = Modifier.align(Alignment.BottomEnd),
        onClick = {},
    ) {
        Crossfade(
            isDone,
            animationSpec = tween(300),
            label = "Forward button crossfade",
        ) {
            if (it) {
                Image(painterResource(R.drawable.ic_done), "")
            } else {
                Image(painterResource(R.drawable.ic_arrow_forward), "")
            }
        }
    }
}

@Composable
@Preview(widthDp = 320, heightDp = 640)
private fun OnboardingDialogPreview() {
    OnboardingDialog { }
}