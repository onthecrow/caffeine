package com.onthecrow.caffeine.ui.footer

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview

@Preview(widthDp = 360)
@Composable
fun SignatureFooter(modifier: Modifier = Modifier) {
    Box(modifier) {
        Text(
            modifier = Modifier.align(Alignment.Center),
            text = buildAnnotatedString {
                withStyle(SpanStyle(fontWeight = FontWeight.Light)) {
                    append("Developed with ❤️ by ")
                }
                withStyle(SpanStyle(fontWeight = FontWeight.SemiBold)) {
                    append("@onthecrow")
                }
            },
        )
    }
}