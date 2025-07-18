package com.bwc.translator.ui.components.chat

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.bwc.translator.data.model.ChatState
import com.bwc.translator.ui.theme.TextSecondary


@Composable
fun ChatList(
    state: ChatState,
    modifier: Modifier = Modifier,
    onSpeakEnglish: (text: String) -> Unit = {},
    onSpeakThai: (text: String) -> Unit = {}
) {
    val listState = rememberLazyListState()
    val clipboardManager = LocalClipboardManager.current

    // Track copied state per message
    val copiedStates = remember { mutableStateMapOf<String, Boolean>() }

    LaunchedEffect(state.entries.size, state.interimText, state.streamingTranslation) {
        if (listState.layoutInfo.totalItemsCount > 0) {
            listState.animateScrollToItem(listState.layoutInfo.totalItemsCount - 1)
        }
    }

    LazyColumn(
        state = listState,
        modifier = modifier,
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(state.entries) { entry ->
            val sourceText = if (entry.isFromEnglish) entry.englishText else entry.thaiText
            val translatedText = if (entry.isFromEnglish) entry.thaiText else entry.englishText

            TranslationEntryItem(
                entry = entry,
                onSpeakSource = {
                    if (entry.isFromEnglish) onSpeakEnglish(sourceText)
                    else onSpeakThai(sourceText)
                },
                onSpeakTranslation = {
                    if (entry.isFromEnglish) onSpeakThai(translatedText)
                    else onSpeakEnglish(translatedText)
                },
                onCopySource = {
                    clipboardManager.setText(AnnotatedString(sourceText))
                    copiedStates[sourceText] = true
                },
                onCopyTranslation = {
                    clipboardManager.setText(AnnotatedString(translatedText))
                    copiedStates[translatedText] = true
                },
                showCopiedSource = copiedStates[sourceText] == true,
                showCopiedTranslation = copiedStates[translatedText] == true
            )
        }

        if (state.interimText.isNotBlank()) {
            item {
                InterimBubble(
                    text = state.interimText,
                    isInputEnglish = state.isInputEnglish
                )
            }
        }

        if (state.streamingTranslation != null) {
            item {
                StreamingTranslationItem(
                    sourceText = state.streamingTranslation.first,
                    translatedText = state.streamingTranslation.second,
                    isSourceEnglish = state.isInputEnglish,
                    onSpeakSource = {
                        if (state.isInputEnglish) onSpeakEnglish(state.streamingTranslation.first)
                        else onSpeakThai(state.streamingTranslation.first)
                    },
                    onSpeakTranslation = {
                        if (state.isInputEnglish) onSpeakThai(state.streamingTranslation.second)
                        else onSpeakEnglish(state.streamingTranslation.second)
                    },
                    onCopySource = {
                        clipboardManager.setText(AnnotatedString(state.streamingTranslation.first))
                        copiedStates[state.streamingTranslation.first] = true
                    },
                    onCopyTranslation = {
                        clipboardManager.setText(AnnotatedString(state.streamingTranslation.second))
                        copiedStates[state.streamingTranslation.second] = true
                    },
                    showCopiedSource = copiedStates[state.streamingTranslation.first] == true,
                    showCopiedTranslation = copiedStates[state.streamingTranslation.second] == true
                )
            }
        }
    }
}

@Composable
fun InitialPlaceholder(text: String) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = TextSecondary,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}