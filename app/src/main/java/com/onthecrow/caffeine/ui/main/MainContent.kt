package com.onthecrow.caffeine.ui.main

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContent
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.BiasAlignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.onthecrow.caffeine.BuildConfig
import com.onthecrow.caffeine.R
import com.onthecrow.caffeine.data.permission.OnboardingPermission
import com.onthecrow.caffeine.ui.common.ItemSettingsWithSwitch
import com.onthecrow.caffeine.ui.common.RunButton
import com.onthecrow.caffeine.ui.easter.Hamsters
import com.onthecrow.caffeine.ui.footer.SignatureFooter
import com.onthecrow.caffeine.ui.header.CaffeineHeader
import com.onthecrow.caffeine.ui.onboarding.OnboardingDialog
import com.onthecrow.caffeine.ui.theme.mintGreen

// Hosted on GitHub Pages from docs/privacy-policy.html (Settings → Pages → main, /docs folder).
private const val PRIVACY_POLICY_URL = "https://onthecrow.github.io/caffeine/privacy-policy.html"

@Composable
fun MainContent(
    state: MainState,
    togglePersistent: (Boolean) -> Unit,
    toggleRebootPersistent: (Boolean) -> Unit,
    toggleAutomaticTurnOff: (Boolean) -> Unit,
    onHeaderClick: () -> Unit,
    onRunButtonClick: (shouldRun: Boolean) -> Unit,
    onOnboardingButtonClick: () -> Unit,
    onRequestPermission: (OnboardingPermission) -> Unit,
    isActive: Boolean,
) {
    val context = LocalContext.current
    Surface(
        modifier = Modifier.fillMaxSize(),
    ) {
        Column(
            modifier = Modifier.windowInsetsPadding(WindowInsets.safeDrawing),
        ) {
            CaffeineHeader(isActive, onHeaderClick)
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                text = "Caffeine keeps your screen awake for as long as you need. Your settings " +
                        "are stored only on this device — the app doesn't collect or share any personal data.",
                fontSize = 14.sp,
                lineHeight = 20.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(modifier = Modifier.size(16.dp))
            ItemSettingsWithSwitch(
                modifier = Modifier.fillMaxWidth(),
                title = "Persistent",
                description = "Keeps the screen awake without a time limit. Android normally drops an " +
                        "app's keep-awake overlay after about 24 hours — with this on, Caffeine " +
                        "periodically re-applies it, so the screen stays on until you stop Caffeine yourself.",
                checked = state.caffeineSettings.isPersistent,
                onCheckedChange = { togglePersistent(it) },
                onClick = { togglePersistent(!state.caffeineSettings.isPersistent) }
            )
            ItemSettingsWithSwitch(
                modifier = Modifier.fillMaxWidth(),
                title = "Restart on reboot",
                description = "If Caffeine is running when you restart your device, it turns itself back " +
                        "on automatically after the reboot, so you don't have to open the app and start it again.",
                checked = state.caffeineSettings.isRebootPersistent,
                onCheckedChange = { toggleRebootPersistent(it) },
                onClick = { toggleRebootPersistent(!state.caffeineSettings.isRebootPersistent) }
            )
            ItemSettingsWithSwitch(
                modifier = Modifier.fillMaxWidth(),
                title = "Automatic turn off",
                subtitle = "Turn Caffeine off on screen off",
                description = "Automatically turns Caffeine off when the screen goes off (for example when " +
                        "you press the power button), instead of keeping it running. Leave this off if you " +
                        "want Caffeine to stay active even after the screen turns off.",
                checked = state.caffeineSettings.isAutomaticallyTurnOff,
                onCheckedChange = { toggleAutomaticTurnOff(it) },
                onClick = { toggleAutomaticTurnOff(!state.caffeineSettings.isAutomaticallyTurnOff) }
            )
            Spacer(modifier = Modifier.size(16.dp))
            val requiredPermissionsGranted = state.onboardingDialogState.slides
                .firstOrNull { it.permission == OnboardingPermission.OVERLAY }?.isGranted == true
            if (requiredPermissionsGranted) {
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
            }
            OutlinedButton(
                modifier = Modifier
                    .padding(end = 16.dp)
                    .align(Alignment.End),
                onClick = { onOnboardingButtonClick() },
            ) {
                Text(text = "Repeat onboarding")
            }
            TextButton(
                modifier = Modifier
                    .padding(end = 16.dp)
                    .align(Alignment.End),
                onClick = {
                    context.startActivity(
                        Intent(Intent.ACTION_VIEW, Uri.parse(PRIVACY_POLICY_URL))
                    )
                },
            ) {
                Text(text = "Privacy Policy")
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
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = "Version ${BuildConfig.VERSION_NAME}",
                textAlign = TextAlign.Center,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
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
            OnboardingDialog(
                state = state.onboardingDialogState,
                onRequestPermission = onRequestPermission,
                onDismissRequest = { onOnboardingButtonClick() },
            )
        }
    }
}

@Preview
@Composable
fun MainContentPreview() {
    MainContent(MainState(), {}, {}, {}, {}, {}, {}, {}, false)
}
