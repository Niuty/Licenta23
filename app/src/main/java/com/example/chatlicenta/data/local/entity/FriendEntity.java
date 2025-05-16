package com.example.chatlicenta.data.local.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.annotation.NonNull;

@Entity(tableName = "friends")
public class FriendEntity {
    @PrimaryKey @NonNull
    private String id;             // uid-ul prietenului
    private String name;           // nume afișat
    private String photoUri;       // opțional, avatar

    public FriendEntity(@NonNull String id, String name, String photoUri) {
        this.id = id;
        this.name = name;
        this.photoUri = photoUri;
    }

    @NonNull public String getId()    { return id; }
    public String getName()            { return name; }
    public String getPhotoUri()        { return photoUri; }

    public void setName(String name)           { this.name = name; }
    public void setPhotoUri(String photoUri)   { this.photoUri = photoUri; }
}
