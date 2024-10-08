package com.onthecrow.caffeine.ui.main

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.BiasAlignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.onthecrow.caffeine.R
import com.onthecrow.caffeine.ui.common.ItemSettingsWithSwitch
import com.onthecrow.caffeine.ui.common.RunButton
import com.onthecrow.caffeine.ui.easter.Hamsters
import com.onthecrow.caffeine.ui.footer.SignatureFooter
import com.onthecrow.caffeine.ui.header.CaffeineHeader
import com.onthecrow.caffeine.ui.onboarding.OnboardingDialog
import com.onthecrow.caffeine.ui.theme.mintGreen

@Composable
fun MainContent(
    state: MainState,
    togglePersistent: (Boolean) -> Unit,
    toggleRebootPersistent: (Boolean) -> Unit,
    toggleAutomaticTurnOff: (Boolean) -> Unit,
    onHeaderClick: () -> Unit,
    onRunButtonClick: (shouldRun: Boolean) -> Unit,
    onOnboardingButtonClick: () -> Unit,
    isActive: Boolean,
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
    ) {
        Column {
            CaffeineHeader(isActive, onHeaderClick)
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
            Spacer(modifier = Modifier.size(16.dp))
            Row(
                modifier = Modifier
                    .align(Alignment.End)
                    .padding(horizontal = 16.dp),
            ) {
                Image(
                    painterResource(R.drawable.ic_check_circle),
                    contentDescription = "",
                    colorFilter = ColorFilter.tint(mintGreen),
                )
                Spacer(modifier = Modifier.size(4.dp))
                Text(
                    modifier = Modifier.align(Alignment.CenterVertically),
                    text = "All required permissions are granted",
                    color = mintGreen,
                )
            }
            Spacer(modifier = Modifier.size(8.dp))
            OutlinedButton(
                modifier = Modifier
                    .padding(end = 16.dp)
                    .align(Alignment.End),
                onClick = { onOnboardingButtonClick() },
            ) {
                Text(text = "Repeat onboarding")
            }
            RunButton(
                modifier = Modifier
                    .padding(end = 16.dp)
                    .align(Alignment.End),
                onRunButtonClick = onRunButtonClick,
                isRunning = isActive,
            )
            Spacer(modifier = Modifier.weight(1f))
            SignatureFooter(modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.size(8.dp))
        }
        Box(modifier = Modifier.fillMaxSize()) {
            Hamsters(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(BiasAlignment(horizontalBias = 0f, verticalBias = 0.5f)),
                isRunning = state.isEasterAnimationRunning,
            )
        }
        if (state.onboardingDialogState.isShowing) {
            OnboardingDialog { onOnboardingButtonClick() }
        }
    }
}

@Preview
@Composable
fun MainContentPreview() {
    MainContent(MainState(), {}, {}, {}, {}, {}, {}, false)
}
