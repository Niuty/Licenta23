package com.example.chatlicenta.ui.fragments_main_tabs;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.chatlicenta.data.repository.FriendRepository;
import com.example.chatlicenta.data.local.entity.FriendEntity;
import com.example.chatlicenta.data.model.Friend;

import java.util.List;

public class FriendViewModel extends AndroidViewModel {
    private final LiveData<List<FriendEntity>> friendEntities;
    private final FriendRepository repo;

    public FriendViewModel(@NonNull Application application) {
        super(application);                                // ← must call super
        repo = new FriendRepository(application);          // use application
        friendEntities = repo.getAllFriendsLive();
    }

    public LiveData<List<FriendEntity>> getFriends() {
        return friendEntities;
    }

    public void addFriend(Friend friend) {
        FriendEntity fe = new FriendEntity(
                friend.getId(),
                friend.getName(),
                friend.getPhotoUri()
        );
        repo.addFriend(fe);
    }

    public void removeFriend(String id) {
        repo.removeFriend(id);
    }
}
