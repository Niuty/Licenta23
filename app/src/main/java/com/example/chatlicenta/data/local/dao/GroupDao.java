package com.example.chatlicenta.data.local.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Delete;
import androidx.room.Update;

import com.example.chatlicenta.data.local.entity.GroupEntity;

import java.util.List;

@Dao
public interface GroupDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(GroupEntity g);

    @Update
    void update(GroupEntity g);

    @Delete
    void delete(GroupEntity g);

    @Query("SELECT * FROM groups ORDER BY name ASC")
    LiveData<List<GroupEntity>> getAllGroupsLive();

    @Query("SELECT * FROM groups WHERE id = :id LIMIT 1")
    GroupEntity findById(String id);
}
