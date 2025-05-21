package com.example.chatlicenta.ui.chat;

import android.app.Application;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.Observer;

import com.example.chatlicenta.data.ChatRepository;
import com.example.chatlicenta.data.FriendRepository;
import com.example.chatlicenta.data.local.entity.MessageEntity;
import com.example.chatlicenta.data.local.entity.FriendEntity;
import com.example.chatlicenta.data.model.Message;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ViewModel pentru ChatActivity care combină:
 * - LiveData<List<MessageEntity>> din Room (sincronizat cu Firebase)
 * - lista prietenilor din FriendRepository
 * și expune un LiveData<List<Message>> gata pentru UI.
 */
public class ChatViewModel extends AndroidViewModel {
    private final ChatRepository repo;
    private final FriendRepository friendRepo;
    private final String conversationId;

    // sursele noastre de date
    private final LiveData<List<MessageEntity>> rawMessages;
    private final LiveData<List<FriendEntity>> friendsList;

    // LiveData pe care o observă UI-ul
    private final MediatorLiveData<List<Message>> uiMessages = new MediatorLiveData<>();

    public ChatViewModel(@NonNull Application app, String conversationId) {
        super(app);
        this.conversationId = conversationId;

        repo = new ChatRepository(app);
        friendRepo = new FriendRepository(app);

        // 1) pornește ascultarea realtime + cache în Room
        repo.subscribeAndCache(conversationId);

        // 2) fluxul raw de entități din Room
        rawMessages = repo.getMessagesFor(conversationId);

        // 3) lista de prieteni (doar entitățile, fără relație many-to-many)
        friendsList = friendRepo.getAllFriendEntities();

        // 4) combinăm ambele LiveData pentru UI
        uiMessages.addSource(rawMessages, combineObserver);
        uiMessages.addSource(friendsList, combineObserver);
    }

    /** Observer comun pentru rawMessages și friendsList */
    private final Observer<Object> combineObserver = new Observer<Object>() {
        @Override
        public void onChanged(Object ignored) {
            List<MessageEntity> entities = rawMessages.getValue();
            List<FriendEntity> friends    = friendsList.getValue();

            if (entities == null || friends == null) {
                return;
            }

            // construim un map de userId → displayName
            Map<String,String> nameLookup = new HashMap<>();
            for (FriendEntity f : friends) {
                nameLookup.put(f.getId(), f.getName());
            }
            // adăugăm și numele curent
            String me = getApplication()
                    .getSharedPreferences("chat_prefs", Context.MODE_PRIVATE)
                    .getString("key_user_name", "Me");
            String meId = getApplication()
                    .getSharedPreferences("chat_prefs", Context.MODE_PRIVATE)
                    .getString("key_user_id", "");
            nameLookup.put(meId, me);

            // mapăm fiecare MessageEntity → Message
            List<Message> out = new ArrayList<>(entities.size());
            for (MessageEntity e : entities) {
                boolean sent = e.getSenderId().equals(meId);
                String  name = nameLookup.getOrDefault(e.getSenderId(),
                        sent ? me : "Unknown");

                out.add(new Message(
                        e.getConversationId(),
                        e.getSenderId(),
                        name,
                        e.getContent(),
                        e.getTimestamp(),
                        sent
                ));
            }
            uiMessages.setValue(out);
        }
    };

    /** LiveData observată de Activity/Fragment */
    public LiveData<List<Message>> getUiMessages() {
        return uiMessages;
    }

    /**
     * Trimite un mesaj text.
     */
    public void sendMessage(String text) {
        String userId = getApplication()
                .getSharedPreferences("chat_prefs", Context.MODE_PRIVATE)
                .getString("key_user_id", "");
        String userName = getApplication()
                .getSharedPreferences("chat_prefs", Context.MODE_PRIVATE)
                .getString("key_user_name", "");

        long ts = System.currentTimeMillis();
        Message m = new Message(conversationId, userId, userName, text, ts, true);
        repo.sendMessage(conversationId, m);
    }
}
