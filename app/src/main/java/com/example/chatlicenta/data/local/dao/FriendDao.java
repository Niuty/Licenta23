package com.example.chatlicenta.data.local.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Delete;
import androidx.room.Update;

import com.example.chatlicenta.data.local.entity.FriendEntity;

import java.util.List;

@Dao
public interface FriendDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(FriendEntity friend);

    @Update
    void update(FriendEntity friend);

    @Delete
    void delete(FriendEntity friend);

    @Query("SELECT * FROM friends ORDER BY name ASC")
    LiveData<List<FriendEntity>> getAllFriends();
    @Query("SELECT * FROM friends ORDER BY name ASC")
    List<FriendEntity> getAllFriendsSync();

    @Query("SELECT * FROM friends WHERE id = :id LIMIT 1")
    FriendEntity findById(String id);


}
