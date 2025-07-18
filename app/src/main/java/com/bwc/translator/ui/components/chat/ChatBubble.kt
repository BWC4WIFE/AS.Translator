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
fun ChatBubble(
    text: String,
    isEnglish: Boolean,
    modifier: Modifier = Modifier,
    isInterim: Boolean = false,
    onSpeakClick: (() -> Unit)? = null,
    onCopyClick: (() -> Unit)? = null,
    showCopiedIndicator: Boolean = false
) {
    Box(modifier = modifier) {
        Card(
            shape = RoundedCornerShape(16.dp).copy(
                bottomStart = if (isEnglish) 4.dp else 16.dp,
                bottomEnd = if (!isEnglish) 4.dp else 16.dp
            ),
            colors = CardDefaults.cardColors(
                containerColor = if (isEnglish) BubbleEnBg else BubbleThBg
            ),
            modifier = Modifier.padding(bottom = 24.dp)
        ) {
            Text(
                text = text,
                color = TextPrimary,
                fontSize = 20.sp,
                fontFamily = if (isEnglish) null else Sarabun,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)
            )
        }

        if (!isInterim && (onSpeakClick != null || onCopyClick != null)) Row(
            modifier = Modifier
                .align(if (isEnglish) Alignment.BottomStart else Alignment.BottomEnd)
                .padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            onSpeakClick?.let { onClick ->
                IconButton(onClick = onClick, modifier = Modifier.size(32.dp)) {
                    Icon(
                        Icons.Default.VolumeUp,
                        contentDescription = "Speak",
                        tint = TextSecondary,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }





            onCopyClick?.let { onClick ->
                IconButton(onClick = onClick, modifier = Modifier.size(32.dp)) {
                    if (showCopiedIndicator) {
                        Text("Copied!", fontSize = 10.sp, color = TextSecondary)
                    } else {
                        Icon(
                            Icons.Default.ContentCopy,
                            contentDescription = "Copy",
                            tint = TextSecondary,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    }
}
