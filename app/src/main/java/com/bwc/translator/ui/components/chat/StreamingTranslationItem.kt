package com.bwc.translator.ui.components.chat

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha

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