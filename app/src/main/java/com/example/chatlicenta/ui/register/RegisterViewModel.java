package com.example.chatlicenta.ui.register;

import android.util.Log;
import android.util.Patterns;

import androidx.annotation.StringRes;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.chatlicenta.R;
import com.example.chatlicenta.data.model.LoggedInUser;
import com.example.chatlicenta.data.repository.UserRepository;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

/**
 * ViewModel pentru RegisterActivity folosind UserRepository.
 * Oferă feedback detaliat despre posibile erori.
 */
public class RegisterViewModel extends ViewModel {

    private final MutableLiveData<RegisterFormState> registerFormState = new MutableLiveData<>();
    private final MutableLiveData<RegisterResult>    registerResult    = new MutableLiveData<>();
    private final UserRepository userRepository;

    public RegisterViewModel(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public LiveData<RegisterFormState> getRegisterFormState() {
        return registerFormState;
    }

    public LiveData<RegisterResult> getRegisterResult() {
        return registerResult;
    }

    /**
     * Începe procesul de înregistrare cu email, nume afișat și parolă.
     * Emite erori specifice în caz de eșec.
     */
    public void register(final String email,
                         final String displayName,
                         final String password) {
        // Trimitem cererea
        userRepository.register(email, password, displayName)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        // Sesiunea e deja salvată de UserRepository
                        registerResult.postValue(
                                new RegisterResult(
                                        new RegisteredUserView(
                                                userRepository.getCurrentUser().getUserId(),
                                                userRepository.getCurrentUser().getDisplayName()
                                        )
                                )
                        );
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(Exception e) {
                        Log.e("REGISTER_VM", "Registration failed", e);
                        @StringRes int errRes;
                        if (e instanceof FirebaseAuthUserCollisionException) {
                            // email already in use
                            errRes = R.string.error_email_already_in_use;
                        } else if (e instanceof FirebaseAuthException) {
                            String code = ((FirebaseAuthException) e).getErrorCode();
                            switch (code) {
                                case "ERROR_INVALID_EMAIL":
                                    errRes = R.string.invalid_email;
                                    break;
                                case "ERROR_WEAK_PASSWORD":
                                    errRes = R.string.invalid_password;
                                    break;
                                default:
                                    errRes = R.string.register_failed;
                            }
                        } else if ("Username already taken".equals(e.getMessage())) {
                            errRes = R.string.error_username_taken;
                        } else if (e instanceof FirebaseNetworkException) {
                            errRes = R.string.error_network;
                        } else {
                            errRes = R.string.register_failed;
                        }
                        registerResult.postValue(new RegisterResult(errRes));
                    }
                });
    }

    /**
     * Validarea formularului de înregistrare (email, name, parolă).
     */
    public void registerDataChanged(String email,
                                    String displayName,
                                    String password) {
        if (!isEmailValid(email)) {
            registerFormState.setValue(
                    new RegisterFormState(R.string.invalid_email, null, null)
            );
        } else if (!isUserNameValid(displayName)) {
            registerFormState.setValue(
                    new RegisterFormState(null, R.string.invalid_username, null)
            );
        } else if (!isPasswordValid(password)) {
            registerFormState.setValue(
                    new RegisterFormState(null, null, R.string.invalid_password)
            );
        } else {
            registerFormState.setValue(new RegisterFormState(true));
        }
    }

    private boolean isEmailValid(String email) {
        return email != null && Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private boolean isUserNameValid(String username) {
        return username != null && username.trim().length() >= 3;
    }

    private boolean isPasswordValid(String password) {
        return password != null && password.length() >= 6;
    }
}
