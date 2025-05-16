package com.example.chatlicenta.data.local.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;
import androidx.room.Delete;

import com.example.chatlicenta.data.local.entity.UserEntity;

@Dao
public interface UserDao {
    @Query("SELECT * FROM users WHERE userId=:id")
    LiveData<UserEntity> findByIdLive(String id);
    @Query("SELECT * FROM users WHERE userId=:id")
    UserEntity findById(String id);
    @Update
    void update(UserEntity user);
    @Insert(onConflict=OnConflictStrategy.REPLACE)
    void insert(UserEntity user);
    @Delete
    void delete(UserEntity user);
}
