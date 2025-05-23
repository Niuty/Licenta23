package com.example.chatlicenta.ui.login;

import androidx.annotation.Nullable;

/**
 * Starea formularului de login: posibilă eroare de email, parolă și flag de validare.
 */
public class LoginFormState {
    @Nullable
    private Integer emailError;
    @Nullable
    private Integer passwordError;
    private boolean isDataValid;

    /** Constructor pentru stare de eroare (email și/sau parolă) */
    public LoginFormState(@Nullable Integer emailError,
                          @Nullable Integer passwordError) {
        this.emailError    = emailError;
        this.passwordError = passwordError;
        this.isDataValid   = false;
    }

    /** Constructor pentru stare validă */
    public LoginFormState(boolean isDataValid) {
        this.emailError    = null;
        this.passwordError = null;
        this.isDataValid   = isDataValid;
    }

    @Nullable
    public Integer getEmailError() {
        return emailError;
    }

    @Nullable
    public Integer getPasswordError() {
        return passwordError;
    }

    public boolean isDataValid() {
        return isDataValid;
    }
}
