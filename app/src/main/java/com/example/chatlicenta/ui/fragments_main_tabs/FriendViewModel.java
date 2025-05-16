package com.example.chatlicenta.ui.fragments_main_tabs;

import android.app.Application;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.chatlicenta.data.FriendRepository;
import com.example.chatlicenta.data.local.entity.FriendEntity;
import com.example.chatlicenta.data.model.Friend;

import java.util.List;

public class FriendViewModel extends AndroidViewModel {
    private final LiveData<List<FriendEntity>> friendEntities;
    private final FriendRepository repo;

    public FriendViewModel(@NonNull Application application) {
        super(application);                                // ← must call super
        repo = new FriendRepository(application);          // use application
        friendEntities = repo.getAllFriendEntities();
    }

    public LiveData<List<FriendEntity>> getFriends() {
        return friendEntities;
    }

    public void addFriend(Friend f) {
        repo.addFriend(f);
    }

    public void removeFriend(String id) {
        repo.removeFriend(id);
    }
}
