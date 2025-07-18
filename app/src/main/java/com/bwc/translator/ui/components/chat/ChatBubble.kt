package com.bwc.translator.ui.components.chat

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bwc.translator.ui.theme.BubbleEnBg
import com.bwc.translator.ui.theme.BubbleThBg
import com.bwc.translator.ui.theme.Sarabun
import com.bwc.translator.ui.theme.TextPrimary
import com.bwc.translator.ui.theme.TextSecondary

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
                bottomStart = CornerSize(if (isEnglish) 4.dp else 16.dp),
                bottomEnd = CornerSize(if (!isEnglish) 4.dp else 16.dp)
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
