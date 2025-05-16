// app/src/main/java/com/example/chatlicenta/data/ChatRepository.java
package com.example.chatlicenta.data;

import android.content.Context;

import androidx.lifecycle.LiveData;

import com.example.chatlicenta.data.local.AppDatabase;
import com.example.chatlicenta.data.local.dao.MessageDao;
import com.example.chatlicenta.data.local.entity.MessageEntity;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ChatRepository {
    private final MessageDao dao;
    private final ExecutorService exec = Executors.newSingleThreadExecutor();

    public ChatRepository(Context ctx) {
        dao = AppDatabase.getInstance(ctx).messageDao();
    }

    /** LiveData care emite toate mesajele pentru o conversație */
    public LiveData<List<MessageEntity>> getMessagesFor(String conversationId) {
        return dao.getMessagesFor(conversationId);
    }

    /** Inserează un mesaj nou pe un thread de background */
    public void sendMessage(MessageEntity message) {
        exec.execute(() -> dao.insert(message));
    }
}
