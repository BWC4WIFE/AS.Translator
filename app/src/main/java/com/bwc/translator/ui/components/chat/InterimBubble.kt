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
fun InterimBubble(
    text: String,
    isInputEnglish: Boolean,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = if (isInputEnglish) Arrangement.Start else Arrangement.End
    ) {
        ChatBubble(text = text, isEnglish = isInputEnglish, isInterim = true)
    }
}