// app/src/main/java/com/example/chatlicenta/ui/chat/ChatViewModel.java
package com.example.chatlicenta.ui.chat;

import android.app.Application;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.chatlicenta.data.ChatRepository;      // updated import
import com.example.chatlicenta.data.local.entity.MessageEntity;

import java.util.List;

public class ChatViewModel extends AndroidViewModel {
    private final ChatRepository repo;                   // renamed from MessageRepository
    private final LiveData<List<MessageEntity>> messages;
    private final String conversationId;                 // store for sendMessage

    public ChatViewModel(@NonNull Application app, String conversationId) {
        super(app);
        this.conversationId = conversationId;
        repo     = new ChatRepository(app);              // updated constructor
        messages = repo.getMessagesFor(conversationId);
    }

    public LiveData<List<MessageEntity>> getMessages() {
        return messages;
    }

    public void sendMessage(String text) {
        // fetch current user ID however you store it…
        String userId = getApplication()
                .getSharedPreferences("chat_prefs", Context.MODE_PRIVATE)
                .getString("key_user_id", "");

        MessageEntity m = new MessageEntity(
                /*conversationId=*/ conversationId,
                /*senderId=*/       userId,
                /*timestamp=*/      System.currentTimeMillis(),
                /*type=*/           "text",
                /*content=*/        text,
                /*sent=*/           true
        );
        repo.sendMessage(m);
    }
}
