package com.example.chatlicenta.ui.register;

import androidx.annotation.Nullable;

public class RegisterResult {
    @Nullable
    private final RegisteredUserView success;
    @Nullable
    private final Integer error;

    public RegisterResult(@Nullable Integer error) {
        this.error = error;
        this.success = null;
    }

    public RegisterResult(@Nullable RegisteredUserView success) {
        this.success = success;
        this.error = null;
    }

    @Nullable
    public RegisteredUserView getSuccess() {
        return success;
    }

    @Nullable
    public Integer getError() {
        return error;
    }
}
