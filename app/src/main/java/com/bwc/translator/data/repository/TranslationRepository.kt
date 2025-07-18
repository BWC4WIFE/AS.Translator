package com.bwc.translator.data.repository

import android.content.Context
import com.bwc.translator.BuildConfig
import com.bwc.translator.data.local.AppDatabase
import com.bwc.translator.data.model.ConversationSession
import com.bwc.translator.data.model.TranslationEntry
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.generationConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class TranslationRepository(context: Context) {

    private val generativeModel: GenerativeModel
    private val sessionDao = AppDatabase.getDatabase(context).sessionDao()
    private val entryDao = AppDatabase.getDatabase(context).entryDao()

    init {
        val config = generationConfig {
            temperature = 0.7f
        }
        generativeModel = GenerativeModel(
            modelName = "gemini-1.5-flash-latest",
            apiKey = BuildConfig.GEMINI_API_KEY,
            generationConfig = config
        )
    }

    fun translateText(text: String, isInputEnglish: Boolean): Flow<String> = flow {
        val prompt = if (isInputEnglish) {
            "Translate this English text to Thai. Use natural, informal Thai. Output ONLY the Thai translation and nothing else: \"$text\""
        } else {
            "Translate this Thai text to English. Capture the tone and be natural. Output ONLY the English translation and nothing else: \"$text\""
        }

        var fullResponse = ""
        generativeModel.generateContentStream(prompt).collect { chunk ->
            chunk.text?.let {
                fullResponse += it
                emit(fullResponse)
            }
        }
    }.flowOn(Dispatchers.IO)

    // Database operations
    fun getAllSessions(): Flow<List<ConversationSession>> = sessionDao.getAllSessions()

    fun getEntriesForSession(sessionId: Long): Flow<List<TranslationEntry>> = entryDao.getEntriesForSession(sessionId)

    suspend fun startNewSession(): Long {
        val newSession = ConversationSession()
        sessionDao.insertSession(newSession)
        return newSession.id
    }

    suspend fun saveTranslationEntry(entry: TranslationEntry) {
        entryDao.insertEntry(entry)
    }

    suspend fun deleteSession(sessionId: Long) {
        entryDao.deleteEntriesForSession(sessionId)
        sessionDao.deleteSessionById(sessionId)
    }
}