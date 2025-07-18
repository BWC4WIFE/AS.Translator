package com.bwc.translator.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.bwc.translator.data.model.ConversationSession
import com.bwc.translator.data.model.TranslationEntry
import kotlinx.coroutines.flow.Flow

@Dao
interface SessionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSession(session: ConversationSession)

    @Query("SELECT * FROM sessions ORDER BY startTime DESC")
    fun getAllSessions(): Flow<List<ConversationSession>>

    @Query("DELETE FROM sessions WHERE id = :sessionId")
    suspend fun deleteSessionById(sessionId: Long)
}

@Dao
interface EntryDao {
    @Insert
    suspend fun insertEntry(entry: TranslationEntry)

    @Query("SELECT * FROM entries WHERE sessionId = :sessionId ORDER BY timestamp ASC")
    fun getEntriesForSession(sessionId: Long): Flow<List<TranslationEntry>>

    @Query("DELETE FROM entries WHERE sessionId = :sessionId")
    suspend fun deleteEntriesForSession(sessionId: Long)
}