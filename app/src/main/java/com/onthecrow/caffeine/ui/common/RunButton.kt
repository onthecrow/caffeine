package com.onthecrow.caffeine.ui.common

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.onthecrow.caffeine.ui.theme.stopButtonColors

@Composable
fun ColumnScope.RunButton(isRunning: Boolean) {
    if (isRunning) {
        Button(
            modifier = Modifier
                .padding(end = 8.dp)
                .align(Alignment.End),
            onClick = {},
            colors = stopButtonColors,
        ) {
            Text("Stop")
        }
    } else {
        Button(
            modifier = Modifier
                .padding(end = 8.dp)
                .align(Alignment.End),
            onClick = {}
        ) {
            Text("Run")
        }
    }
}