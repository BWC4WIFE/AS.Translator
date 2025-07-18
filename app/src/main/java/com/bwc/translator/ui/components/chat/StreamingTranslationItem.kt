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
fun StreamingTranslationItem(
    sourceText: String,
    translatedText: String,
    isSourceEnglish: Boolean,
    modifier: Modifier = Modifier,
    onSpeakSource: (() -> Unit)? = null,
    onSpeakTranslation: (() -> Unit)? = null,
    onCopySource: (() -> Unit)? = null,
    onCopyTranslation: (() -> Unit)? = null,
    showCopiedSource: Boolean = false,
    showCopiedTranslation: Boolean = false
) {
    Column(
        horizontalAlignment = if (isSourceEnglish) Alignment.Start else Alignment.End,
        modifier = modifier.fillMaxWidth()
    ) {
        ChatBubble(
            text = sourceText,
            isEnglish = isSourceEnglish,
            onSpeakClick = onSpeakSource,
            onCopyClick = onCopySource,
            showCopiedIndicator = showCopiedSource
        )
        ChatBubble(
            text = translatedText.ifBlank { "..." },
            isEnglish = !isSourceEnglish,
            modifier = Modifier.alpha(0.7f),
            onSpeakClick = onSpeakTranslation,
            onCopyClick = onCopyTranslation,
            showCopiedIndicator = showCopiedTranslation
        )
    }
}