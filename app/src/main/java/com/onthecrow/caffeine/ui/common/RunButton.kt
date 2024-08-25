package com.onthecrow.caffeine.ui.common

import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.onthecrow.caffeine.ui.theme.stopButtonColors

@Composable
fun RunButton(isRunning: Boolean, modifier: Modifier = Modifier) {
    // todo simplify to one Button
    if (isRunning) {
        Button(
            modifier = modifier,
            onClick = {},
            colors = stopButtonColors,
        ) {
            Text("Stop")
        }
    } else {
        Button(
            modifier = modifier,
            onClick = {}
        ) {
            Text("Run")
        }
    }
}