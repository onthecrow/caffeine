package com.onthecrow.caffeine.ui.common

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
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
private fun ItemSettings(
    modifier: Modifier = Modifier,
    title: String,
    onClick: () -> Unit,
    trailingContent: @Composable () -> Unit,
    subtitle: String? = null,
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
        Row(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .wrapContentHeight()
                    .align(Alignment.CenterVertically)
            ) {
                Text(
                    text = title,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    fontSize = 16.sp,
                )
                if (subtitle != null) {
                    Text(
                        text = subtitle,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Light,
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

@Composable
fun ItemSettingsWithSwitch(
    modifier: Modifier = Modifier,
    title: String,
    checked: Boolean,
    onClick: () -> Unit,
    onCheckedChange: ((Boolean) -> Unit)?,
    subtitle: String? = null,
) {
    ItemSettings(
        modifier = modifier,
        title = title,
        subtitle = subtitle,
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
            checked = true,
            onClick = {},
            onCheckedChange = {},
        )
    }
}