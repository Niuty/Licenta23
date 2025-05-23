package com.example.chatlicenta.data.repository;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.chatlicenta.data.local.AppDatabase;
import com.example.chatlicenta.data.local.dao.FriendDao;
import com.example.chatlicenta.data.local.entity.FriendEntity;
import com.example.chatlicenta.data.session.SessionManager;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FriendRepository {
    private final FriendDao friendDao;
    private final DatabaseReference friendsRef;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    public FriendRepository(Context ctx) {
        AppDatabase db = AppDatabase.getInstance(ctx);
        friendDao = db.friendDao();

        String userId = new SessionManager(ctx).getUserId();
        friendsRef = FirebaseDatabase.getInstance()
                .getReference("users")
                .child(userId)
                .child("friends");
    }

    /**
     * Ascultă lista de prieteni din Firebase și sincronizează cu Room.
     */
    public LiveData<List<FriendEntity>> subscribeAndCache() {
        MutableLiveData<List<FriendEntity>> liveData = new MutableLiveData<>();
        friendsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<FriendEntity> cachedList = new ArrayList<>();
                for (DataSnapshot friendNode : snapshot.getChildren()) {
                    String friendId = friendNode.getKey();
                    // Dacă DAO-ul tău are findById:
                    FriendEntity friendEntity = friendDao.findById(friendId);
                    if (friendEntity != null) {
                        cachedList.add(friendEntity);
                    }
                }
                // Suprascriem local în Room
                executor.execute(() -> {
                    friendDao.deleteAll();        // asigură-te că ai @Query("DELETE FROM friends") în DAO
                    friendDao.insertAll(cachedList); // asigură-te că ai insertAll(List<FriendEntity>)
                });
                liveData.postValue(cachedList);
            }

            @Override public void onCancelled(@NonNull DatabaseError error) { }
        });
        return liveData;
    }

    /**
     * Adaugă un prieten în Firebase și în Room.
     */
    public void addFriend(FriendEntity friendEntity) {
        // 1) Firebase
        friendsRef.child(friendEntity.getId()).setValue(true);
        // 2) Room
        executor.execute(() -> friendDao.insert(friendEntity));
    }

    /**
     * Șterge un prieten din Firebase și din Room.
     */
    public void removeFriend(String friendId) {
        // 1) Firebase
        friendsRef.child(friendId).removeValue();
        // 2) Room
        executor.execute(() -> {
            FriendEntity friendEntity = friendDao.findById(friendId);
            if (friendEntity != null) {
                friendDao.delete(friendEntity);
            }
        });
    }

    /**
     * Preia lista locală din Room (fallback sau pentru observație simplă).
     */
    public LiveData<List<FriendEntity>> getAllFriendsLive() {
        return friendDao.getAllFriends();  // DAO: LiveData<List<FriendEntity>> getAllFriends();
    }

    /**
     * Preluare sincronă (folosită intern).
     */
    public List<FriendEntity> getAllFriendsSync() {
        return friendDao.getAllFriendsSync();
    }
}
