package com.example.chatlicenta.data.local.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.annotation.NonNull;

@Entity(tableName = "users")
public class UserEntity {
    @PrimaryKey @NonNull
    private String userId;
    private String displayName;
    private String photoUri;   // pentru avatar, poate fi null

    public UserEntity(@NonNull String userId, String displayName, String photoUri) {
        this.userId = userId;
        this.displayName = displayName;
        this.photoUri = photoUri;
    }
    @NonNull public String getUserId()     { return userId; }
    public String getDisplayName()          { return displayName; }
    public String getPhotoUri()             { return photoUri; }
    public void setDisplayName(String n)    { this.displayName = n; }
    public void setPhotoUri(String uri)     { this.photoUri = uri; }
}
