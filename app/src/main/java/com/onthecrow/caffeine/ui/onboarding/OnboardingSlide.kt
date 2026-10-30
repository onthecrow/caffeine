package com.onthecrow.caffeine.ui.onboarding

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun OnboardingSlide(
    headline: String,
    text: String,
    modifier: Modifier = Modifier,
    buttonText: String? = null,
    onButtonClick: (() -> Unit)? = null,
    noteImagePainter: Painter? = null,
    noteText: String? = null,
    noteColor: Color? = null,
) {
    Column(modifier) {
        Text(
            fontSize = 24.sp,
            text = headline,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            lineHeight = 32.sp,
            fontWeight = FontWeight.W400
        )
        Spacer(Modifier.size(16.dp))
        Text(
            text = text,
            fontWeight = FontWeight.W400,
            fontSize = 14.sp,
            lineHeight = 20.sp,
        )
        Spacer(Modifier.weight(1f))
        if (buttonText != null && onButtonClick != null) {
            Button(
                modifier = Modifier.align(Alignment.End),
                onClick = { onButtonClick() },
            ) {
                Text(buttonText)
            }
        } else if (noteImagePainter != null && noteText != null && noteColor != null) {
            Row(
                modifier = Modifier
                    .align(Alignment.End),
            ) {
                Image(
                    noteImagePainter,
                    contentDescription = "",
                    colorFilter = ColorFilter.tint(noteColor),
                )
                Spacer(modifier = Modifier.size(4.dp))
                Text(
                    modifier = Modifier.align(Alignment.CenterVertically),
                    text = noteText,
                    color = noteColor,
                )
            }
        }
    }
}

@Composable
@Preview(widthDp = 320, heightDp = 640)
private fun OnboardingSlidePreview() {
    OnboardingSlide("Headline", "Preview text")
}