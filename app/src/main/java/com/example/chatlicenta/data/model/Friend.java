package com.example.chatlicenta.data.model;

public class Friend {
    private final String id;
    private final String name;
    private final String photoUri;

    public Friend(String id, String name ) {
        this(id, name, null);
    }
    public Friend(String id, String name , String photoUri) {
        this.id = id;
        this.name = name;
        this.photoUri = photoUri;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getPhotoUri() {
        return photoUri;
    }
}
