package com.bwc.translator.data.model

data class ChatState(
    val entries: List<TranslationEntry>,
    val interimText: String = "",
    val isInputEnglish: Boolean = true,
    val streamingTranslation: Pair<String, String>? = null
)