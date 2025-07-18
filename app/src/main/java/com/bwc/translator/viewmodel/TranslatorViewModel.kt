package com.bwc.translator.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.bwc.translator.data.model.ConversationSession
import com.bwc.translator.data.model.TranslationEntry
import com.bwc.translator.data.repository.TranslationRepository
import com.bwc.translator.services.RecognitionState
import com.bwc.translator.services.SpeechRecognizerService
import com.bwc.translator.services.TextToSpeechService
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class InputMode { HOLD, TAP }

sealed class TranslatorUiState {
    data object Loading : TranslatorUiState()
    data class Success(
        val isInputEnglish: Boolean = true,
        val inputMode: InputMode = InputMode.HOLD,
        val isListening: Boolean = false,
        val currentSessionId: Long? = null,
        val currentEntries: List<TranslationEntry> = emptyList(),
        val sessions: List<Pair<ConversationSession, String>> = emptyList(),
        val interimText: String = "",
        val streamingTranslation: Pair<String, String>? = null, // <Source, Translation>
        val error: String? = null
    ) : TranslatorUiState()
}

class TranslatorViewModel(application: Application) : ViewModel() {

    private val repository = TranslationRepository(application)
    private val speechRecognizer = SpeechRecognizerService(application)
    private val textToSpeech = TextToSpeechService(application) { isSuccess ->
        // Can handle TTS init status if needed
    }

    private val _internalState = MutableStateFlow(TranslatorUiState.Success())
    private var translationJob: Job? = null

    val uiState: StateFlow<TranslatorUiState> = combine(
        _internalState,
        repository.getAllSessions()
    ) { state, sessions ->
        // This combines our internal state with the session list from the DB
        // and fetches the preview for each session
        val sessionsWithPreviews = sessions.map { session ->
            val firstEntry = repository.getEntriesForSession(session.id).first().firstOrNull()
            val preview = firstEntry?.englishText ?: "Empty Session"
            session to preview
        }

        // If we don't have a session ID, and there are sessions, load the latest one.
        if (state.currentSessionId == null && sessions.isNotEmpty()) {
            loadSession(sessions.first().id)
        } else if (state.currentSessionId != null) {
            // If we have a session ID, listen for its entries
            repository.getEntriesForSession(state.currentSessionId).collect { entries ->
                _internalState.update { it.copy(currentEntries = entries) }
            }
        }

        state.copy(sessions = sessionsWithPreviews)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = TranslatorUiState.Loading
    )


    private val _showHistoryDialog = MutableStateFlow(false)
    val showHistoryDialog: StateFlow<Boolean> = _showHistoryDialog.asStateFlow()

    init {
        viewModelScope.launch {
            speechRecognizer.recognitionState.collect { state ->
                handleRecognitionState(state)
            }
        }
        viewModelScope.launch {
            val sessions = repository.getAllSessions().first()
            if(sessions.isEmpty()) {
                startNewSession()
            } else {
                loadSession(sessions.first().id)
            }
        }
    }

    private fun handleRecognitionState(state: RecognitionState) {
        when (state) {
            is RecognitionState.Listening -> _internalState.update { it.copy(isListening = true, interimText = "") }
            is RecognitionState.Idle -> _internalState.update { it.copy(isListening = false) }
            is RecognitionState.PartialResult -> _internalState.update { it.copy(interimText = state.text) }
            is RecognitionState.FinalResult -> {
                _internalState.update { it.copy(isListening = false, interimText = "") }
                if (state.text.isNotBlank()) {
                    processFinalTranscript(state.text)
                }
            }
            is RecognitionState.Error -> _internalState.update { it.copy(error = state.message, isListening = false) }
        }
    }

    private fun processFinalTranscript(text: String) {
        translationJob?.cancel()
        translationJob = viewModelScope.launch {
            val currentState = _internalState.value
            val sourceText = text
            var translatedText = ""

            _internalState.update { it.copy(streamingTranslation = sourceText to "") }

            try {
                repository.translateText(text, currentState.isInputEnglish).collect { streamedText ->
                    translatedText = streamedText
                    _internalState.update { it.copy(streamingTranslation = sourceText to translatedText) }
                }

                val newEntry = TranslationEntry(
                    sessionId = currentState.currentSessionId ?: return@launch,
                    englishText = if (currentState.isInputEnglish) sourceText else translatedText,
                    thaiText = if (currentState.isInputEnglish) translatedText else sourceText,
                    isFromEnglish = currentState.isInputEnglish
                )
                repository.saveTranslationEntry(newEntry)
                speak(translatedText, !currentState.isInputEnglish)

            } catch (e: Exception) {
                _internalState.update { it.copy(error = "Translation failed: ${e.message}") }
            } finally {
                _internalState.update { it.copy(streamingTranslation = null) }
            }
        }
    }

    // --- Public actions from UI ---

    fun startListening() = speechRecognizer.startListening(_internalState.value.isInputEnglish)
    fun stopListening() = speechRecognizer.stopListening()
    fun toggleListening() {
        if (_internalState.value.isListening) stopListening() else startListening()
    }
    fun swapLanguage() = _internalState.update { it.copy(isInputEnglish = !it.isInputEnglish) }
    fun setInputMode(mode: InputMode) = _internalState.update { it.copy(inputMode = mode) }
    fun speak(text: String, isEnglish: Boolean) = textToSpeech.speak(text, isEnglish)
    fun clearError() = _internalState.update { it.copy(error = null) }

    fun toggleHistoryDialog(show: Boolean) {
        _showHistoryDialog.value = show
    }

    fun loadSession(sessionId: Long) {
        viewModelScope.launch {
            _internalState.update { it.copy(currentSessionId = sessionId) }
            toggleHistoryDialog(false)
        }
    }

    fun startNewSession() {
        viewModelScope.launch {
            val newId = repository.startNewSession()
            _internalState.update { it.copy(currentSessionId = newId, currentEntries = emptyList()) }
            toggleHistoryDialog(false)
        }
    }

    fun deleteSession(sessionId: Long) {
        viewModelScope.launch {
            repository.deleteSession(sessionId)
            // The main flow will automatically handle reloading the correct session
        }
    }

    override fun onCleared() {
        super.onCleared()
        speechRecognizer.destroy()
        textToSpeech.shutdown()
    }

    // ViewModel Factory
    class TranslatorViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(TranslatorViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return TranslatorViewModel(application) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}