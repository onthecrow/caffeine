package com.onthecrow.caffeine.ui.common

import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.onthecrow.caffeine.ui.theme.stopButtonColors

@Composable
fun RunButton(
    isRunning: Boolean,
    modifier: Modifier = Modifier,
    onRunButtonClick: (shouldRun: Boolean) -> Unit,
) {
    Button(
        modifier = modifier,
        onClick = { onRunButtonClick(!isRunning) },
        colors = if (isRunning) stopButtonColors else ButtonDefaults.buttonColors(),
    ) {
        Text(text = if (isRunning) "Stop" else "Run")
    }
}