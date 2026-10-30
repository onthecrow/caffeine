package com.onthecrow.caffeine.ui.common

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntRect
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupPositionProvider
import androidx.compose.ui.window.PopupProperties
import com.onthecrow.caffeine.R

@Composable
private fun ItemSettings(
    modifier: Modifier = Modifier,
    title: String,
    onClick: () -> Unit,
    trailingContent: @Composable () -> Unit,
    subtitle: String? = null,
    description: String? = null,
    titleColor: Color = Color.Unspecified,
) {
    Box(
        modifier
            .height(48.dp)
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(
                horizontal = 16.dp,
            )
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // Title/subtitle plus the optional info button. Wrapped in a weighted row so the
            // button sits right after the text while the trailing content is pushed to the end.
            Row(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(
                    modifier = Modifier
                        .weight(1f, fill = false)
                        .wrapContentHeight()
                ) {
                    Text(
                        text = title,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        fontSize = 16.sp,
                        color = titleColor,
                    )
                    if (subtitle != null) {
                        Text(
                            text = subtitle,
                            maxLines = 1,
                            style = TextStyle(
                                platformStyle = PlatformTextStyle(
                                    includeFontPadding = false
                                )
                            ),
                            overflow = TextOverflow.Ellipsis,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Light,
                        )
                    }
                }
                if (description != null) {
                    InfoButton(
                        description = description,
                        contentDescription = title,
                        modifier = Modifier
                            // 16dp gap from the text = 4dp start padding + 12dp icon inset within the 48dp target
                            .padding(start = 4.dp)
                            .fillMaxHeight()
                            .width(48.dp),
                    )
                }
            }
            Spacer(modifier = Modifier.size(16.dp))
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(IntrinsicSize.Min)
            ) {
                Box(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .wrapContentSize()
                ) {
                    trailingContent()
                }
            }
        }
    }
}

/**
 * 48dp circular touch target with a 24dp info icon. Tapping it shows [description] in a popup
 * centered on the button. The nested clickable consumes the tap, so the row's toggle doesn't fire.
 */
@Composable
private fun InfoButton(
    description: String,
    contentDescription: String,
    modifier: Modifier = Modifier,
) {
    var showPopup by remember { mutableStateOf(false) }
    Box(
        modifier = modifier
            .clip(CircleShape)
            .clickable { showPopup = true },
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_info),
            contentDescription = contentDescription,
            modifier = Modifier.size(24.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        if (showPopup) {
            InfoPopup(text = description, onDismiss = { showPopup = false })
        }
    }
}

@Composable
private fun InfoPopup(text: String, onDismiss: () -> Unit) {
    val gapPx = with(LocalDensity.current) { 16.dp.roundToPx() }
    Popup(
        popupPositionProvider = remember(gapPx) { CenteredBelowPositionProvider(gapPx) },
        onDismissRequest = onDismiss,
        properties = PopupProperties(focusable = true),
    ) {
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = MaterialTheme.colorScheme.surfaceVariant,
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
            modifier = Modifier.widthIn(max = 260.dp),
        ) {
            Text(
                text = text,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                fontSize = 14.sp,
                lineHeight = 20.sp,
            )
        }
    }
}

/**
 * Positions the popup horizontally centered on the anchor (the info button) and [gapPx] below it,
 * flipping above when there isn't enough room, clamped to stay on screen.
 */
private class CenteredBelowPositionProvider(private val gapPx: Int) : PopupPositionProvider {
    override fun calculatePosition(
        anchorBounds: IntRect,
        windowSize: IntSize,
        layoutDirection: LayoutDirection,
        popupContentSize: IntSize,
    ): IntOffset {
        val x = anchorBounds.left + (anchorBounds.width - popupContentSize.width) / 2
        val clampedX = x.coerceIn(0, (windowSize.width - popupContentSize.width).coerceAtLeast(0))
        val below = anchorBounds.bottom + gapPx
        val y = if (below + popupContentSize.height <= windowSize.height) {
            below
        } else {
            (anchorBounds.top - gapPx - popupContentSize.height).coerceAtLeast(0)
        }
        return IntOffset(clampedX, y)
    }
}

@Composable
fun ItemSettingsWithSwitch(
    modifier: Modifier = Modifier,
    title: String,
    checked: Boolean,
    onClick: () -> Unit,
    onCheckedChange: ((Boolean) -> Unit)?,
    subtitle: String? = null,
    description: String? = null,
) {
    ItemSettings(
        modifier = modifier,
        title = title,
        subtitle = subtitle,
        description = description,
        onClick = onClick,
        trailingContent = {
            Switch(checked, onCheckedChange)
        }
    )
}

@Composable
fun ItemSettingsWithButton(
    modifier: Modifier = Modifier,
    title: String,
    buttonText: String,
    onClick: () -> Unit,
    subtitle: String? = null,
) {
    ItemSettings(
        modifier = modifier,
        title = title,
        subtitle = subtitle,
        onClick = onClick,
        trailingContent = {
            Text(
                text = buttonText,
                color = MaterialTheme.colorScheme.primary,
            )
        }
    )
}

/** A full-width settings row styled like [ItemSettingsWithSwitch] but with no trailing control. */
@Composable
fun ItemSettingsClickable(
    modifier: Modifier = Modifier,
    title: String,
    onClick: () -> Unit,
    subtitle: String? = null,
    description: String? = null,
    titleColor: Color = Color.Unspecified,
) {
    ItemSettings(
        modifier = modifier,
        title = title,
        subtitle = subtitle,
        description = description,
        onClick = onClick,
        titleColor = titleColor,
        trailingContent = {},
    )
}

@Preview(widthDp = 320, heightDp = 640)
@Composable
private fun ItemSettingsPreview() {
    Column(
        modifier = Modifier
            .background(Color.White)
            .fillMaxHeight()
            .fillMaxWidth()
    ) {
        ItemSettingsWithButton(
            modifier = Modifier.fillMaxWidth(),
            title = "Some title",
            buttonText = "123",
            onClick = {},
        )
        ItemSettingsWithSwitch(
            modifier = Modifier.fillMaxWidth(),
            title = "Some title",
            description = "An explanation of what this setting does.",
            checked = false,
            onClick = {},
            onCheckedChange = {},
        )
        ItemSettingsWithButton(
            modifier = Modifier.fillMaxWidth(),
            title = "Some settings title asdf sakld;fjq;welkr asldkfj qwelrj sdf lkj",
            buttonText = "123",
            onClick = {},
        )
        ItemSettingsWithSwitch(
            modifier = Modifier.fillMaxWidth(),
            title = "Some very-very long-long title title",
            subtitle = "Some very-very long-long subtitle subtitle subtitle",
            description = "An explanation of what this setting does.",
            checked = true,
            onClick = {},
            onCheckedChange = {},
        )
    }
}
