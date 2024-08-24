package com.onthecrow.caffeine.ui.footer

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.onthecrow.caffeine.R

@Preview(widthDp = 360)
@Composable
fun SignatureFooter(modifier: Modifier = Modifier) {

    val context = LocalContext.current
    val telegramIntent = remember {
        Intent(Intent.ACTION_VIEW).apply {
            setData(Uri.parse("https://t.me/onthecrow"))
        }
    }
    val gitHubIntent = remember {
        Intent(Intent.ACTION_VIEW).apply {
            setData(Uri.parse("https://github.com/onthecrow"))
        }
    }
    val instagramIntent = remember {
        Intent(Intent.ACTION_VIEW).apply {
            setData(Uri.parse("https://www.instagram.com/onthecrow"))
        }
    }

    Box(modifier) {
        Column {
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = buildAnnotatedString {
                    withStyle(SpanStyle(fontWeight = FontWeight.Light)) {
                        append("Developed with ❤️ by ")
                    }
                    withStyle(SpanStyle(fontWeight = FontWeight.SemiBold)) {
                        append("@onthecrow")
                    }
                },
                textAlign = TextAlign.Center,
                fontSize = 14.sp,
                maxLines = 1,
            )
            Box(modifier = Modifier.fillMaxWidth()) {
                Row(modifier = Modifier.align(Alignment.Center)) {
                    Image(
                        modifier = Modifier
                            .size(48.dp)
                            .clickable { context.startActivity(telegramIntent) }
                            .padding(8.dp),
                        painter = painterResource(R.drawable.ic_telegram),
                        contentDescription = "",
                        colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onBackground),
                    )
                    Image(
                        modifier = Modifier
                            .size(48.dp)
                            .clickable { context.startActivity(gitHubIntent) }
                            .padding(8.dp),
                        painter = painterResource(R.drawable.ic_github),
                        contentDescription = "",
                        colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onBackground),
                    )
                    Image(
                        modifier = Modifier
                            .size(48.dp)
                            .clickable { context.startActivity(instagramIntent) }
                            .padding(8.dp),
                        painter = painterResource(R.drawable.ic_instagram),
                        contentDescription = "",
                        colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onBackground),
                    )
                }
            }
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = "Feel free to reach me if you have any questions",
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Light,
                fontSize = 12.sp,
                maxLines = 1,
            )
        }
    }
}

