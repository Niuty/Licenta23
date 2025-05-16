package com.example.chatlicenta.data;

import com.example.chatlicenta.data.model.LoggedInUser;

import java.io.IOException;

/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
public class LoginDataSource {

    public Result<LoggedInUser> login(String username, String password) {

        try {
            // Verificare hardcodată
            if (username.equals("admin") && password.equals("admin123")) {
                LoggedInUser fakeUser = new LoggedInUser(
                        java.util.UUID.randomUUID().toString(),
                        "Admin User");
                return new Result.Success<>(fakeUser);
            } else {
                return new Result.Error(new IOException("Date de autentificare invalide"));
            }
        } catch (Exception e) {
            return new Result.Error(new IOException("Eroare la autentificare", e));
        }
    }

        public void logout () {
            // TODO: revoke authentication
        }

}