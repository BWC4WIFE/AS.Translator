package com.bwc.translator.services

import android.content.Context
import android.speech.tts.TextToSpeech
import android.util.Log
import java.util.Locale

class TextToSpeechService(context: Context, private val onInit: (Boolean) -> Unit) {
    private var tts: TextToSpeech? = null
    private var isReady = false
    private val thaiLocale = Locale("th", "TH")
    private val usLocale = Locale.US

    init {
        tts = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                isReady = true
                Log.d("TTS", "TextToSpeech engine initialized successfully.")
                // Check language availability
                val thaiAvailable = tts?.isLanguageAvailable(thaiLocale) == TextToSpeech.LANG_AVAILABLE
                val usAvailable = tts?.isLanguageAvailable(usLocale) == TextToSpeech.LANG_AVAILABLE
                Log.d("TTS", "Thai available: $thaiAvailable, US English available: $usAvailable")
                onInit(true)
            } else {
                Log.e("TTS", "Failed to initialize TextToSpeech engine.")
                onInit(false)
            }
        }
    }

    fun speak(text: String, isEnglish: Boolean) {
        if (!isReady || text.isBlank()) return
        val locale = if (isEnglish) usLocale else thaiLocale
        tts?.language = locale
        tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, null)
    }

    fun shutdown() {
        tts?.stop()
        tts?.shutdown()
    }
}