package com.example.chatlicenta.ui.settings;

import android.app.Application;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.Transformations;

import com.example.chatlicenta.data.local.AppDatabase;
import com.example.chatlicenta.data.local.dao.UserDao;
import com.example.chatlicenta.data.local.entity.UserEntity;
import com.example.chatlicenta.data.model.LoggedInUser;
import com.example.chatlicenta.data.session.SessionManager;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SettingsViewModel extends AndroidViewModel {

    private final SessionManager sessionManager;
    private final UserDao userDao;
    private final String currentUserId;
    private final LiveData<UserEntity> userEntityLive;
    private final MediatorLiveData<LoggedInUser> userLiveData = new MediatorLiveData<>();
    private final ExecutorService dbExecutor = Executors.newSingleThreadExecutor();

    public SettingsViewModel(@NonNull Application app) {
        super(app);

        // sessionManager for reading/writing SharedPreferences
        sessionManager = new SessionManager(app);



        // fetch current userId
        currentUserId = sessionManager.getUserId();

        // Room dao
        userDao = AppDatabase.getInstance(app).userDao();

        // LiveData<UserEntity> from Room
        userEntityLive = userDao.findByIdLive(currentUserId);

        // Mediator: convert UserEntity → LoggedInUser and push into userLiveData
        userLiveData.addSource(userEntityLive, ent -> {
            if (ent != null) {
                userLiveData.setValue(
                        new LoggedInUser(ent.getUserId(), ent.getDisplayName())
                );
            }
        });
    }

    /**
     * Expose LoggedInUser to the fragment
     */
    public LiveData<LoggedInUser> getUser() {
        return userLiveData;
    }

    /**
     * Change display name in Room (background thread) and in SharedPreferences
     */
    public void changeName(@NonNull String newName) {
        if (currentUserId == null) return;
        dbExecutor.execute(() -> {
            UserEntity ent = userDao.findById(currentUserId);
            if (ent != null) {
                ent.setDisplayName(newName);
                userDao.update(ent);
            }
        });
        // also update SharedPreferences
        sessionManager.saveUser(new LoggedInUser(currentUserId, newName,null));
    }

    /**
     * Change photo: update SharedPreferences and re‑emit current userLiveData
     */
    public void changePhoto(@NonNull Uri photoUri) {
        //1) actualizează entitatea în DB cu noul photoUri
        dbExecutor.execute(() -> {
            UserEntity ent = userDao.findById(currentUserId);
            if (ent != null) {
                ent.setPhotoUri(photoUri.toString());
                userDao.update(ent);
            }
        });

        // 2) actualizează şi SharedPreferences
        LoggedInUser old = sessionManager.getUser();
        if (old != null) {
            LoggedInUser updated = new LoggedInUser(
                    old.getUserId(),
                    old.getDisplayName(),
                    photoUri.toString()
            );
            sessionManager.saveUser(updated);
            userLiveData.setValue(updated);
        }
    }

    /**
     * Logout: clear session (Room data remains until you choose to clear it)
     */
    public void logout() {
        sessionManager.clearSession();
    }
}
