package com.example.chatlicenta.data.local.entity;

import androidx.room.Embedded;
import androidx.room.Junction;
import androidx.room.Relation;

import java.util.List;

public class UserWithFriends {
    @Embedded
    public UserEntity user;

    @Relation(
            parentColumn   = "userId",
            entityColumn   = "userId",
            associateBy    = @Junction(
                    value         = UserFriendCrossRef.class,
                    parentColumn  = "userId",
                    entityColumn  = "friendId"
            )
    )
    public List<UserEntity> friends;
}
