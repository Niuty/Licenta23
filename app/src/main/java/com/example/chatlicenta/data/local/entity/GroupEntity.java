
package com.example.chatlicenta.data.local.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.annotation.NonNull;

@Entity(tableName = "groups")
public class GroupEntity {
    @PrimaryKey
    @NonNull
    private String id;
    private String name;
    private int memberCount;
    private String photoUri;

    public GroupEntity(@NonNull String id, String name, int memberCount, String photoUri) {
        this.id = id;
        this.name = name;
        this.memberCount = memberCount;
        this.photoUri = photoUri;
    }

    @NonNull
    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getMemberCount() {
        return memberCount;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setMemberCount(int count) {
        this.memberCount = count;
    }

    public String getPhotoUri() {
        return photoUri;
    }


    public void setPhotoUri(String photoUri) {
        this.photoUri = photoUri;
    }
}
