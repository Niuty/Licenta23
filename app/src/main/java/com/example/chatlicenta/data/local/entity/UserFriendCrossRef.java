package com.example.chatlicenta.data.local.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;

@Entity(
        tableName = "user_friends",
        primaryKeys = { "userId", "friendId" },
        foreignKeys = {
                @ForeignKey(entity = UserEntity.class,
                        parentColumns = "userId", childColumns = "userId"),
                @ForeignKey(entity = UserEntity.class,
                        parentColumns = "userId", childColumns = "friendId")
        }
)
public class UserFriendCrossRef {
    @NonNull
    public String userId;
    @NonNull public String friendId;
}