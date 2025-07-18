package com.bwc.translator.ui.screens

import android.Manifest
import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bwc.translator.ui.components.ControlsBar
import com.bwc.translator.ui.components.HistoryDialog
import com.bwc.translator.ui.components.chat.InitialPlaceholder
import com.bwc.translator.viewmodel.TranslatorUiState
import com.bwc.translator.viewmodel.TranslatorViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun TranslatorScreen(
    viewModel: TranslatorViewModel = viewModel(),
    onNavigateToHistory: () -> Unit
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val recordAudioPermission = rememberPermissionState(Manifest.permission.RECORD_AUDIO)

    LaunchedEffect(uiState.error) {
        uiState.error?.let {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
            viewModel.clearError()
        }
    }

    val showHistoryDialog by viewModel.showHistoryDialog.collectAsState()
    if (showHistoryDialog) {
        HistoryDialog(
            sessions = uiState.sessions,
            onDismiss = { viewModel.toggleHistoryDialog(false) },
            onSessionClick = { viewModel.loadSession(it) },
            onDeleteClick = { viewModel.deleteSession(it) },
            onNewChatClick = { viewModel.startNewSession() }
        )
    }

    Scaffold(
        bottomBar = {
            ControlsBar(
                isListening = uiState.isListening,
                isInputEnglish = uiState.isInputEnglish,
                inputMode = uiState.inputMode,
                isMicEnabled = recordAudioPermission.status.isGranted,
                onMicPress = {
                    if (recordAudioPermission.status.isGranted) {
                        viewModel.startListening()
                    } else {
                        recordAudioPermission.launchPermissionRequest()
                    }
                },
                onMicRelease = { viewModel.stopListening() },
                onMicClick = {
                    if (recordAudioPermission.status.isGranted) {
                        viewModel.toggleListening()
                    } else {
                        recordAudioPermission.launchPermissionRequest()
                    }
                },
                onSwapLanguage = { viewModel.swapLanguage() },
                onModeChange = { viewModel.setInputMode(it) },
                onHistoryClick = { viewModel.toggleHistoryDialog(true) }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (val state = uiState) {
                is TranslatorUiState.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        InitialPlaceholder(text = "Loading session...")
                    }
                }
                is TranslatorUiState.Success -> {
                    if (state.currentEntries.isEmpty() && state.interimText.isBlank()) {
                        InitialPlaceholder(text = "Tap or hold the mic to start.")
                    } else {
                        ChatList(
                            entries = state.currentEntries,
                            interimText = state.interimText,
                            isInputEnglish = state.isInputEnglish,
                            streamingTranslation = state.streamingTranslation,
                            onSpeak = { text, isEnglish -> viewModel.speak(text, isEnglish) }
                        )
                    }
                }
            }
        }
    }
}