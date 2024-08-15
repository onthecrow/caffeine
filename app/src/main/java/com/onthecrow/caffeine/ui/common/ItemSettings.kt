package com.onthecrow.caffeine.ui.common

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.tooling.preview.Preview

@Composable
private fun ItemSettings(
    modifier: Modifier = Modifier,
    title: String,
    trailingContent: @Composable () -> Unit,
    divider: Boolean = false,
    subtitle: String? = null,
    image: Painter? = null,
) {
    Row(
        modifier = modifier.wrapContentSize()
//            .width(IntrinsicSize.Max)
//            .height(IntrinsicSize.Min)
    ) {
        image?.let {
            Image(painter = image, contentDescription = "")
        }
        Column(
            modifier = Modifier.weight(1f)
                .align(Alignment.CenterVertically)
        ) {
            Text(text = title)
            if (subtitle != null) {
                Text(text = subtitle)
            }
        }
        Box(
            modifier = Modifier.wrapContentSize()
            .height(IntrinsicSize.Max)
            .width(IntrinsicSize.Max)
        ) {
            trailingContent()
        }
    }
}

@Composable
fun ItemSettingsWithSwitch(
    modifier: Modifier = Modifier,
    title: String,
    checked: Boolean,
    onCheckedChange: ((Boolean) -> Unit)?,
    divider: Boolean = false,
    subtitle: String? = null,
    image: Painter? = null,
) {

}

@Composable
fun ItemSettingsWithButton(
    modifier: Modifier = Modifier,
    title: String,
    subtitle: String? = null,
) {
    ItemSettings(
        modifier = modifier,
        title = title,
        subtitle = subtitle,
        trailingContent = {
            TextButton(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(),
                onClick = { /*TODO*/ }
            ) {
                Text(text = "123")
            }
        }
    )
}

@Preview(widthDp = 320, heightDp = 640)
@Composable
private fun ItemSettingsPreview() {
    ItemSettingsWithButton(
        modifier = Modifier.fillMaxSize(),
        title = "111",
        subtitle = "222"
    )
}