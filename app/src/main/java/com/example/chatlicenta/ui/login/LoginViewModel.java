package com.example.chatlicenta.ui.login;

import android.util.Log;
import android.util.Patterns;

import androidx.annotation.StringRes;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.chatlicenta.R;
import com.example.chatlicenta.data.model.LoggedInUser;
import com.example.chatlicenta.data.repository.UserRepository;
import com.google.firebase.auth.AuthResult;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

/**
 * ViewModel pentru LoginActivity folosind UserRepository.
 */
public class LoginViewModel extends ViewModel {

    private final UserRepository userRepository;
    private final MutableLiveData<LoginFormState> loginFormState = new MutableLiveData<>();
    private final MutableLiveData<LoginResult>    loginResult    = new MutableLiveData<>();

    public LoginViewModel(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public LiveData<LoginFormState> getLoginFormState() {
        return loginFormState;
    }

    public LiveData<LoginResult> getLoginResult() {
        return loginResult;
    }

    /**
     * Începe procesul de autentificare cu email și parolă.
     */
    public void login(String email, String password) {
        // apel asincron către UserRepository
        userRepository.login(email, password)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult res) {
                        // sesiunea e deja salvată de UserRepository
                        LoggedInUser me = userRepository.getCurrentUser();
                        loginResult.postValue(
                                new LoginResult(
                                        new LoggedInUserView(me.getUserId(), me.getDisplayName())
                                )
                        );
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(Exception e) {
                        Log.e("LOGIN_VM", "Login failed", e);
                        loginResult.postValue(
                                new LoginResult(R.string.login_failed)
                        );
                    }
                });
    }

    /**
     * Validarea câmpurilor email și parolă pentru butonul "Login".
     */
    public void loginDataChanged(String email, String password) {
        if (!isEmailValid(email)) {
            loginFormState.setValue(new LoginFormState(R.string.invalid_email, null));
        } else if (!isPasswordValid(password)) {
            loginFormState.setValue(new LoginFormState(null, R.string.invalid_password));
        } else {
            loginFormState.setValue(new LoginFormState(true));
        }
    }

    private boolean isEmailValid(String email) {
        return email != null && Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private boolean isPasswordValid(String password) {
        return password != null && password.length() >= 6;
    }
}
