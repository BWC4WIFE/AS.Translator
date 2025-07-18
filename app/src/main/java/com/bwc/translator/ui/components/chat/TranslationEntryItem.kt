package com.bwc.translator.ui.components.chat

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import com.bwc.translator.data.model.ChatState
import com.bwc.translator.data.model.TranslationEntry

@Composable
fun TranslationEntryItem(
    entry: TranslationEntry,
    modifier: Modifier = Modifier,
    onSpeakSource: (() -> Unit)? = null,
    onSpeakTranslation: (() -> Unit)? = null,
    onCopySource: (() -> Unit)? = null,
    onCopyTranslation: (() -> Unit)? = null,
    showCopiedSource: Boolean = false,
    showCopiedTranslation: Boolean = false
) {
    Column(
        horizontalAlignment = if (entry.isFromEnglish) Alignment.Start else Alignment.End,
        modifier = modifier.fillMaxWidth()
    ) {
        val sourceText = if (entry.isFromEnglish) entry.englishText else entry.thaiText
        val translatedText = if (entry.isFromEnglish) entry.thaiText else entry.englishText

        ChatBubble(
            text = sourceText,
            isEnglish = entry.isFromEnglish,
            onSpeakClick = onSpeakSource,
            onCopyClick = onCopySource,
            showCopiedIndicator = showCopiedSource
        )
        ChatBubble(
            text = translatedText,
            isEnglish = !entry.isFromEnglish,
            onSpeakClick = onSpeakTranslation,
            onCopyClick = onCopyTranslation,
            showCopiedIndicator = showCopiedTranslation
        )
    }
}