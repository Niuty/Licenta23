package com.example.chatlicenta.data;

import com.example.chatlicenta.data.model.RegisteredUser;

import java.util.UUID;

public class RegisterDataSource {

    public Result<RegisteredUser> register(String username, String password) {
        if (username.equals("admin")) {
            return new Result.Error(new Exception("Utilizator deja existent"));
        }

        RegisteredUser fakeUser = new RegisteredUser(UUID.randomUUID().toString(), username);
        return new Result.Success<>(fakeUser);
    }
}