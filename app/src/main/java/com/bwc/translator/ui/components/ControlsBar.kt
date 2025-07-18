package com.bwc.translator.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bwc.translator.ui.theme.MicButton
import com.bwc.translator.ui.theme.MicButtonDisabled
import com.bwc.translator.ui.theme.MicButtonListening
import com.bwc.translator.ui.theme.TextSecondary
import com.bwc.translator.viewmodel.InputMode

@Composable
fun ControlsBar(
    isListening: Boolean,
    isInputEnglish: Boolean,
    inputMode: InputMode,
    isMicEnabled: Boolean,
    onMicPress: () -> Unit,
    onMicRelease: () -> Unit,
    onMicClick: () -> Unit,
    onSwapLanguage: () -> Unit,
    onModeChange: (InputMode) -> Unit,
    onHistoryClick: () -> Unit
) {
    Surface(
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 4.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            ControlItem(modifier = Modifier.weight(1f), alignment = Alignment.CenterStart) {
                LanguageSwap(
                    isInputEnglish = isInputEnglish,
                    onClick = onSwapLanguage,
                    onHistoryClick = onHistoryClick
                )
            }

            MicButton(
                isListening = isListening,
                isEnabled = isMicEnabled,
                inputMode = inputMode,
                onPress = onMicPress,
                onRelease = onMicRelease,
                onClick = onMicClick
            )

            ControlItem(modifier = Modifier.weight(1f), alignment = Alignment.CenterEnd) {
                ModeToggle(
                    currentMode = inputMode,
                    onModeChange = onModeChange
                )
            }
        }
    }
}

@Composable
fun MicButton(
    isListening: Boolean,
    isEnabled: Boolean,
    inputMode: InputMode,
    onPress: () -> Unit,
    onRelease: () -> Unit,
    onClick: () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulse by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 15f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500),
            repeatMode = RepeatMode.Restart
        ), label = "pulse_alpha"
    )
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.7f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500),
            repeatMode = RepeatMode.Restart
        ), label = "pulse_shadow"
    )

    val bgColor by animateColorAsState(
        targetValue = when {
            !isEnabled -> MicButtonDisabled
            isListening -> MicButtonListening
            else -> MicButton
        }, label = "mic_bg_color"
    )

    Box(
        modifier = Modifier
            .size(72.dp)
            .shadow(
                elevation = if (isListening) pulse.dp else 0.dp,
                shape = CircleShape,
                ambientColor = MicButtonListening.copy(alpha = pulseAlpha),
                spotColor = MicButtonListening.copy(alpha = pulseAlpha)
            )
            .clip(CircleShape)
            .background(bgColor)
            .clickable(
                enabled = isEnabled,
                onClick = { if (inputMode == InputMode.TAP) onClick() },
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            )
            .pointerInput(inputMode, isEnabled) {
                if (inputMode == InputMode.HOLD && isEnabled) {
                    detectTapGestures(
                        onPress = {
                            onPress()
                            tryAwaitRelease()
                            onRelease()
                        }
                    )
                }
            },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Default.Mic,
            contentDescription = "Microphone",
            tint = Color.White,
            modifier = Modifier.size(32.dp)
        )
    }
}

@Composable
fun LanguageSwap(isInputEnglish: Boolean, onClick: () -> Unit, onHistoryClick: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(4.dp)) {
        IconButton(onClick = onClick) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(if (isInputEnglish) "EN" else "TH", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Icon(Icons.Default.SwapHoriz, "Swap Languages")
                Text(if (isInputEnglish) "TH" else "EN", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        }
        Text(
            text = "History",
            fontSize = 12.sp,
            color = TextSecondary,
            modifier = Modifier.clickable { onHistoryClick() }
        )
    }
}


@Composable
fun ModeToggle(currentMode: InputMode, onModeChange: (InputMode) -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Switch(
            checked = currentMode == InputMode.TAP,
            onCheckedChange = { isChecked ->
                onModeChange(if (isChecked) InputMode.TAP else InputMode.HOLD)
            },
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = Color(0xFF10B981)
            )
        )
        Text(
            text = if (currentMode == InputMode.HOLD) "Hold to Talk" else "Tap to Talk",
            fontSize = 12.sp,
            color = TextSecondary
        )
    }
}

@Composable
private fun ControlItem(modifier: Modifier = Modifier, alignment: Alignment, content: @Composable () -> Unit) {
    Box(
        modifier = modifier,
        contentAlignment = alignment
    ) {
        content()
    }
}