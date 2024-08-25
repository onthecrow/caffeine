package com.onthecrow.caffeine.ui.main

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.BiasAlignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.onthecrow.caffeine.ui.common.ItemSettingsWithSwitch
import com.onthecrow.caffeine.ui.common.RunButton
import com.onthecrow.caffeine.ui.easter.Hamsters
import com.onthecrow.caffeine.ui.footer.SignatureFooter
import com.onthecrow.caffeine.ui.header.CaffeineHeader

@Composable
fun MainContent(
    state: MainState,
    togglePersistent: (Boolean) -> Unit,
    toggleRebootPersistent: (Boolean) -> Unit,
    toggleAutomaticTurnOff: (Boolean) -> Unit,
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
    ) {
        var enabled by remember { mutableStateOf(false) }

        Column {
            CaffeineHeader(enabled)
            ItemSettingsWithSwitch(
                modifier = Modifier.fillMaxWidth(),
                title = "Persistent",
                checked = state.caffeineSettings.isPersistent,
                onCheckedChange = { togglePersistent(it) },
                onClick = { togglePersistent(!state.caffeineSettings.isPersistent) }
            )
            ItemSettingsWithSwitch(
                modifier = Modifier.fillMaxWidth(),
                title = "Restart on reboot",
                checked = state.caffeineSettings.isRebootPersistent,
                onCheckedChange = { toggleRebootPersistent(it) },
                onClick = { toggleRebootPersistent(!state.caffeineSettings.isRebootPersistent) }
            )
            ItemSettingsWithSwitch(
                modifier = Modifier.fillMaxWidth(),
                title = "Automatic turn off",
                subtitle = "Turn Caffeine off on screen off",
                checked = state.caffeineSettings.isAutomaticallyTurnOff,
                onCheckedChange = { toggleAutomaticTurnOff(it) },
                onClick = { toggleAutomaticTurnOff(!state.caffeineSettings.isAutomaticallyTurnOff) }
            )
            OutlinedButton(
                modifier = Modifier
                    .padding(end = 8.dp)
                    .align(Alignment.End),
                onClick = { enabled = !enabled },
            ) {
                Text(text = "Repeat onboarding")
            }
            RunButton(enabled)
            Spacer(modifier = Modifier.weight(1f))
            SignatureFooter(modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.size(8.dp))
        }
        Box(modifier = Modifier.fillMaxSize()) {
            Hamsters(
                modifier = Modifier.fillMaxWidth().align(BiasAlignment(horizontalBias = 0f, verticalBias = 0.5f)),
                isRunning = enabled,
            )
        }
    }
}

@Preview
@Composable
fun MainContentPreview() {
    MainContent(MainState(), {}, {}, {})
}
