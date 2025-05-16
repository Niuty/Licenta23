package com.example.chatlicenta.data;

import com.example.chatlicenta.data.model.LoggedInUser;

import java.io.IOException;
import java.util.UUID;

import com.example.chatlicenta.data.model.RegisteredUser;

/**
 * Handles user registration (stub, fără back‑end real).
 */
public class RegisterRepository {
    private static volatile RegisterRepository instance;

    private RegisterRepository() {
    }

    public static RegisterRepository getInstance() {
        if (instance == null) {
            instance = new RegisterRepository();
        }
        return instance;
    }

    public Result<RegisteredUser> register(String username, String password) {
        try {
            // TODO: adaugă validări reale / apel rețea
            RegisteredUser newUser = new RegisteredUser(
                    UUID.randomUUID().toString(),
                    username
            );
            return new Result.Success<>(newUser);
        } catch (Exception e) {
            return new Result.Error(new IOException("Error registering", e));
        }
    }

    public void logout() {
        // stub
    }
}
