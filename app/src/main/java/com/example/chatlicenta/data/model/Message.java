package com.example.chatlicenta.data.model;

import java.util.UUID;

/**
 * Model pentru un mesaj într‑o conversație (user sau grup).
 */
public class Message {

    private final String id;               // identificator unic al mesajului
    private final String conversationId;   // id-ul conversației (userId sau groupId)
    private final String senderId;         // id‑ul celui care a trimis
    private final String text;             // conținutul mesajului
    private final long timestamp;          // momentul trimiterii, în millis
    private final boolean isSent;          // true dacă eu am trimis, false dacă l‑am primit
    private final String senderName;

    public Message(String conversationId, String senderId, String senderName, String text, long timestamp, boolean isSent) {
        this.id = UUID.randomUUID().toString();
        this.conversationId = conversationId;
        this.senderId = senderId;
        this.senderName = senderName;
        this.text = text;
        this.timestamp = timestamp;
        this.isSent = isSent;
    }

    // getters

    public String getId() {
        return id;
    }

    public String getConversationId() {
        return conversationId;
    }

    public String getSenderId() {
        return senderId;
    }

    public String getText() {
        return text;
    }

    public long getTimestamp() {
        return timestamp;
    }

    /**
     * @return true dacă mesajul a fost trimis de către utilizatorul curent
     */
    public boolean isSent() {
        return isSent;
    }

    public String getSenderName() {
        return senderName;
    }
}