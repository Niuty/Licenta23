package com.example.chatlicenta.data;

import com.example.chatlicenta.data.model.Message;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryStore {
    private static InMemoryStore instance;
    private final Map<String, List<Message>> map = new HashMap<>();

    private InMemoryStore() {}

    public static synchronized InMemoryStore getInstance() {
        if (instance == null) {
            instance = new InMemoryStore();
        }
        return instance;
    }

    public List<Message> get(String conversationId) {
        return map.get(conversationId);
    }

    public void addMessage(String conversationId, Message msg) {
        List<Message> list = map.computeIfAbsent(conversationId, k -> new ArrayList<>());
        list.add(msg);
    }
}
