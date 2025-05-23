// app/src/main/java/com/example/chatlicenta/data/ChatRepository.java
package com.example.chatlicenta.data.repository;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import com.example.chatlicenta.data.local.AppDatabase;
import com.example.chatlicenta.data.local.dao.MessageDao;
import com.example.chatlicenta.data.local.entity.MessageEntity;
import com.example.chatlicenta.data.model.Message;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ChatRepository {
    private final MessageDao dao;
    private final DatabaseReference chatsRef;
    private final ExecutorService exec = Executors.newSingleThreadExecutor();

    public ChatRepository(Context ctx) {
        dao = AppDatabase.getInstance(ctx).messageDao();
        chatsRef = FirebaseDatabase.getInstance().getReference("chats");
    }

    /** LiveData care emite toate mesajele pentru o conversație */
    public LiveData<List<MessageEntity>> getMessagesFor(String conversationId) {
        return dao.getMessagesFor(conversationId);
    }

    /** Inserează un mesaj nou pe un thread de background */
//    public void sendMessage(MessageEntity message) {
//        exec.execute(() -> dao.insert(message));
//    }
    /**
     * Ascultă mesajele din Firebase și le reflectă local în Room.
     */
    public void subscribeAndCache(String conversationId) {
        chatsRef.child(conversationId)
                .child("messages")
                .orderByChild("timestamp")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snap) {
                        List<MessageEntity> entities = new ArrayList<>();
                        for (DataSnapshot msgSnap : snap.getChildren()) {
                            Message m = msgSnap.getValue(Message.class);
                            entities.add(toEntity(m));
                        }
                        exec.execute(() ->
                                dao.insertAll(entities)
                        );
                    }
                    @Override public void onCancelled(@NonNull DatabaseError err) {
                        // poți loga eroarea aici
                    }
                });
    }

    /**
     * Trimite un mesaj în Firebase și îl salvează local.
     */
    public void sendMessage(String conversationId, Message m) {
        // 1) Push în Firebase
        String key = chatsRef.child(conversationId)
                .child("messages")
                .push()
                .getKey();
        if (key != null) {
            chatsRef.child(conversationId)
                    .child("messages")
                    .child(key)
                    .setValue(m);
        }
        // 2) Salvează local
        exec.execute(() ->
                dao.insert(toEntity(m))
        );
    }

    private MessageEntity toEntity(Message m) {
        return new MessageEntity(
                m.getConversationId(),
                m.getSenderId(),
                m.getTimestamp(),
                "text",               // dacă ai doar text pentru început
                m.getText(),
                m.isSent()
        );
    }

    private Message toModel(MessageEntity e, String senderName) {
        return new Message(
                e.getConversationId(),
                e.getSenderId(),
                senderName,           // va trebui să-l obții dintr-un user cache sau mapă
                e.getContent(),
                e.getTimestamp(),
                e.isSent()
        );
    }

}
