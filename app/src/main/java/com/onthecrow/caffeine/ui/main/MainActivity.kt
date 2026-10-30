package com.onthecrow.caffeine.ui.main

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import com.onthecrow.caffeine.service.caffeine.CaffeineServiceConnectionManager
import com.onthecrow.caffeine.service.caffeine.CaffeineServiceState
import com.onthecrow.caffeine.ui.theme.CaffeineTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.map
import javax.inject.Inject


@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var caffeineServiceConnectionManager: CaffeineServiceConnectionManager

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
            CaffeineTheme {
                val isActive: State<Boolean> = caffeineServiceConnectionManager.serviceState
                    .map { it == CaffeineServiceState.ACTIVE }
                    .collectAsState(false)
                MainContent(
                    viewModel.state.collectAsState().value,
                    { viewModel.setIsPersistent(it) },
                    { viewModel.setIsRebootPersistent(it) },
                    { viewModel.setIsAutomaticTurnOff(it) },
                    { viewModel.onHeaderTap() },
                    { viewModel.onRunButtonClick(it) },
                    { viewModel.onOnboardingButtonClick() },
                    { viewModel.onRequestPermission(it) },
                    isActive.value,
                )
            }
        }
    }
}
