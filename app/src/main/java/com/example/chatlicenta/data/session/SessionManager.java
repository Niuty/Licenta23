package com.example.chatlicenta.data.session;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.chatlicenta.data.local.AppDatabase;
import com.example.chatlicenta.data.local.dao.UserDao;
import com.example.chatlicenta.data.local.entity.UserEntity;
import com.example.chatlicenta.data.model.LoggedInUser;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Manages user session data in SharedPreferences,
 * and persists extended profile info in Room asynchronously.
 */
public class SessionManager {
    private static final String PREFS_NAME       = "chat_prefs";
    private static final String KEY_USER_ID      = "key_user_id";
    private static final String KEY_USER_NAME    = "key_user_name";
    private static final String KEY_IS_LOGGED_IN = "key_is_logged_in";

    private final SharedPreferences prefs;
    private final UserDao           userDao;
    private final Executor          dbExecutor = Executors.newSingleThreadExecutor();

    public SessionManager(Context ctx) {
        prefs   = ctx.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        userDao = AppDatabase.getInstance(ctx).userDao();
    }

    public String getUserId() {
        return prefs.getString(KEY_USER_ID, null);
    }


    /**
     * Save basic login info in preferences and enqueue Room write for full profile.
     */
    public void saveUser(LoggedInUser user) {
        // 1) SharedPreferences (fast, on main thread)
        prefs.edit()
                .putString(KEY_USER_ID,   user.getUserId())
                .putString(KEY_USER_NAME, user.getDisplayName())
                .putBoolean(KEY_IS_LOGGED_IN, true)
                .apply();

        // 2) Persist full profile in Room (background thread)
        UserEntity ent = new UserEntity(
                user.getUserId(),
                user.getDisplayName(),
                null  // photoUri to be added later when supported
        );
        dbExecutor.execute(() -> userDao.insert(ent));
    }

    /**
     * Retrieve the logged-in user from SharedPreferences.
     * Does NOT hit Room, so safe on main thread.
     */
    public LoggedInUser getUser() {
        if (!prefs.getBoolean(KEY_IS_LOGGED_IN, false)) {
            return null;
        }
        String id   = prefs.getString(KEY_USER_ID, null);
        String name = prefs.getString(KEY_USER_NAME, null);
        if (id != null && name != null) {
            return new LoggedInUser(id, name);
        }
        return null;
    }

    /** @return true if a user is marked as logged in */
    public boolean isLoggedIn() {
        return prefs.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    /**
     * Clears both SharedPreferences and deletes the Room profile entity.
     */
    public void clearSession() {
        // 1) Read ID first
        String id = prefs.getString(KEY_USER_ID, null);
        // 2) Clear prefs
        prefs.edit().clear().apply();
        // 3) Delete Room entity asynchronously
        if (id != null) {
            dbExecutor.execute(() -> {
                UserEntity ent = userDao.findById(id);
                if (ent != null) {
                    userDao.delete(ent);
                }
            });
        }
    }
}
