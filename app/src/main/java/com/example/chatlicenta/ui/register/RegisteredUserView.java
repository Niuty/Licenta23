package com.example.chatlicenta.ui.register;

public class RegisteredUserView {
    private final String displayName;
    private final String userId;

    public RegisteredUserView(String userId, String displayName) {
        this.userId = userId;
        this.displayName = displayName;
    }


    public String getDisplayName() {
        return displayName;
    }
    String getUserId() {
        return userId;
    }
}
