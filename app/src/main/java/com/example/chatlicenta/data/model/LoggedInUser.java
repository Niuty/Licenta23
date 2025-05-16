package com.example.chatlicenta.data.model;

/**
 * Data class that captures user information for logged in users retrieved from LoginRepository
 */
public class LoggedInUser {

    private final String userId;
    private final String displayName;
    private final String photoUri;

    public LoggedInUser(String userId, String displayName, String photoUri) {
        this.userId = userId;
        this.displayName = displayName;
        this.photoUri = photoUri;
    }
    public LoggedInUser(String userId, String displayName) {
        this(userId, displayName, null);
    }

    public String getUserId() {
        return userId;
    }

    public String getDisplayName() {
        return displayName;
    }
    public String getPhotoUri()   { return photoUri; }
}