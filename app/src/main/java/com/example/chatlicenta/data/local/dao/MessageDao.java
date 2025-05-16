package com.example.chatlicenta.data.local.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.OnConflictStrategy;

import com.example.chatlicenta.data.local.entity.MessageEntity;

import java.util.List;

@Dao
public interface MessageDao {

    /**
     * Returnează toate mesajele pentru o conversație,
     * ordonate crescător după timestamp.
     */
    @Query("SELECT * FROM messages WHERE conversationId = :conversationId ORDER BY timestamp ASC")
    LiveData<List<MessageEntity>> getMessagesFor(String conversationId);

    /**
     * Inserează un mesaj nou. Dacă există deja (același primary key),
     * îl înlocuiește (deși, pentru autoGenerate=true, nu se va întâmpla).
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(MessageEntity message);

    // (opțional) dacă vei avea nevoie să ștergi mesaje:
    // @Query("DELETE FROM messages WHERE id = :messageId")
    // void deleteById(long messageId);
}
