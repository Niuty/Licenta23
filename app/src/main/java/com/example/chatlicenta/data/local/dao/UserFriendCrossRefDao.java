package com.example.chatlicenta.data.local.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Delete;

import com.example.chatlicenta.data.local.entity.UserFriendCrossRef;

@Dao
public interface UserFriendCrossRefDao {

    /**
     * Inserează o legătură user ↔ friend.
     * Dacă există deja, nu face nimic (IGNORE).
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(UserFriendCrossRef ref);

    /**
     * Șterge o legătură user ↔ friend.
     */
    @Delete
    void delete(UserFriendCrossRef ref);
}
