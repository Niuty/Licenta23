package com.example.chatlicenta.data;

import android.content.Context;

import androidx.lifecycle.LiveData;

import com.example.chatlicenta.data.local.AppDatabase;
import com.example.chatlicenta.data.local.dao.FriendDao;
import com.example.chatlicenta.data.local.entity.FriendEntity;
import com.example.chatlicenta.data.model.Friend;

import java.util.List;
import java.util.stream.Collectors;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FriendRepository {
    private final FriendDao friendDao;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    public FriendRepository(Context ctx) {
        friendDao = AppDatabase.getInstance(ctx).friendDao();
    }

    public LiveData<List<FriendEntity>> getAllFriendEntities() {
        // dacă vrei LiveData, schimbă DAO la return LiveData<List<...>>
        return friendDao.getAllFriends(); // vezi nota mai jos
    }

    public void addFriend(Friend f) {
        executor.execute(() -> {
            FriendEntity ent = new FriendEntity(f.getId(), f.getName(), f.getPhotoUri());
            friendDao.insert(ent);
        });
    }

    public void removeFriend(String id) {
        executor.execute(() -> {
            FriendEntity ent = friendDao.findById(id);
            if (ent != null) friendDao.delete(ent);
        });
    }

    public List<FriendEntity> getAllFriendsSync() {
        return friendDao.getAllFriendsSync();
    }
}
