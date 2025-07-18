package com.bwc.translator.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "sessions")
data class ConversationSession(
    @PrimaryKey val id: Long = System.currentTimeMillis(),
    val startTime: Date = Date()
)

@Entity(tableName = "entries")
data class TranslationEntry(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val sessionId: Long,
    val englishText: String,
    val thaiText: String,
    val timestamp: Date = Date(),
    val isFromEnglish: Boolean
)