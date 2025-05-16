package com.example.chatlicenta.data.local.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;

@Entity(tableName = "messages")
public class MessageEntity {

    @PrimaryKey(autoGenerate = true)
    private long id;

    @NonNull
    @ColumnInfo(index = true)
    private String conversationId;

    @NonNull
    private String senderId;

    private long timestamp;

    @NonNull
    private String type;       // e.g. "text", "image", "voice"

    private String content;    // text body or media URI
    private boolean sent;

    public MessageEntity(@NonNull String conversationId,
                         @NonNull String senderId,
                         long timestamp,
                         @NonNull String type,
                         String content,
                         boolean sent) {
        this.conversationId = conversationId;
        this.senderId = senderId;
        this.timestamp = timestamp;
        this.type = type;
        this.content = content;
        this.sent = sent;
    }

    // ─── Getters & setters ─────────────────────────────────────────

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @NonNull
    public String getConversationId() {
        return conversationId;
    }

    public void setConversationId(@NonNull String conversationId) {
        this.conversationId = conversationId;
    }

    @NonNull
    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(@NonNull String senderId) {
        this.senderId = senderId;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    @NonNull
    public String getType() {
        return type;
    }

    public void setType(@NonNull String type) {
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public boolean isSent() {
        return sent;
    }

    public void setSent(boolean sent) {
        this.sent = sent;
    }
}
