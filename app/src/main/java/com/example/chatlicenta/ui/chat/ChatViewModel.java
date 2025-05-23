package com.example.chatlicenta.ui.chat;

import android.app.Application;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.Observer;

import com.example.chatlicenta.data.repository.ChatRepository;
import com.example.chatlicenta.data.repository.FriendRepository;
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
 * - fallback local + sincronizare live pentru lista de prieteni
 * și expune un LiveData<List<Message>> gata pentru UI.
 */
public class ChatViewModel extends AndroidViewModel {
    private final ChatRepository repo;
    private final FriendRepository friendRepo;
    private final String conversationId;

    // surse de date pentru mesaje și prieteni
    private final LiveData<List<MessageEntity>> rawMessages;
    private final LiveData<List<FriendEntity>> localFriends;
    private final LiveData<List<FriendEntity>> remoteFriends;

    // LiveData combinată pentru UI
    private final MediatorLiveData<List<Message>> uiMessages = new MediatorLiveData<>();

    public ChatViewModel(@NonNull Application app, String conversationId) {
        super(app);
        this.conversationId = conversationId;

        repo = new ChatRepository(app);
        friendRepo = new FriendRepository(app);

        // Mesaje: pornește ascultarea Firebase → Room
        repo.subscribeAndCache(conversationId);
        rawMessages = repo.getMessagesFor(conversationId);

        // Prieteni: fallback local + sincronizare remote
        localFriends = friendRepo.getAllFriendsLive();
        remoteFriends = friendRepo.subscribeAndCache();

        // Combină sursele prieteni într-un singur LiveData
        MediatorLiveData<List<FriendEntity>> friendsList = new MediatorLiveData<>();
        friendsList.addSource(localFriends,  friendsList::setValue);
        friendsList.addSource(remoteFriends, friendsList::setValue);

        // Observă mesaje și prieteni pentru a actualiza UI
        uiMessages.addSource(rawMessages, ignored -> combine(rawMessages.getValue(), friendsList.getValue()));
        uiMessages.addSource(friendsList, ignored -> combine(rawMessages.getValue(), friendsList.getValue()));
    }

    /**
     * Combină mesajele și lista de prieteni pentru UI.
     */
    private void combine(List<MessageEntity> messages, List<FriendEntity> friends) {
        if (messages == null || friends == null) {
            return;
        }

        // Creează lookup userId → displayName
        Map<String, String> nameLookup = new HashMap<>();
        for (FriendEntity friend : friends) {
            nameLookup.put(friend.getId(), friend.getName());
        }
        String meId = getApplication()
                .getSharedPreferences("chat_prefs", Context.MODE_PRIVATE)
                .getString("key_user_id", "");
        String meName = getApplication()
                .getSharedPreferences("chat_prefs", Context.MODE_PRIVATE)
                .getString("key_user_name", "Me");
        nameLookup.put(meId, meName);

        // Mapare MessageEntity → Message cu nume
        List<Message> result = new ArrayList<>(messages.size());
        for (MessageEntity e : messages) {
            boolean sent = e.getSenderId().equals(meId);
            String displayName = nameLookup.getOrDefault(e.getSenderId(), sent ? meName : "Unknown");
            result.add(new Message(
                    e.getConversationId(),
                    e.getSenderId(),
                    displayName,
                    e.getContent(),
                    e.getTimestamp(),
                    sent
            ));
        }
        uiMessages.setValue(result);
    }

    /**
     * Returnează LiveData gata pentru afișare de către Activity.
     */
    public LiveData<List<Message>> getUiMessages() {
        return uiMessages;
    }

    /**
     * Trimite un mesaj text prin repository.
     */
    public void sendMessage(String text) {
        String userId = getApplication()
                .getSharedPreferences("chat_prefs", Context.MODE_PRIVATE)
                .getString("key_user_id", "");
        String userName = getApplication()
                .getSharedPreferences("chat_prefs", Context.MODE_PRIVATE)
                .getString("key_user_name", "");

        long timestamp = System.currentTimeMillis();
        Message message = new Message(conversationId, userId, userName, text, timestamp, true);
        repo.sendMessage(conversationId, message);
    }
}
