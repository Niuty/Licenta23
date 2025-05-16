package com.example.chatlicenta.ui.register;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.chatlicenta.R;
import com.example.chatlicenta.data.RegisterRepository;
import com.example.chatlicenta.data.Result;
import com.example.chatlicenta.data.model.RegisteredUser;

public class RegisterViewModel extends ViewModel {

    private final MutableLiveData<RegisterFormState> registerFormState = new MutableLiveData<>();
    private final MutableLiveData<RegisterResult> registerResult = new MutableLiveData<>();
    private final RegisterRepository registerRepository;

    public RegisterViewModel(RegisterRepository registerRepository) {
        this.registerRepository = registerRepository;
    }

    public LiveData<RegisterFormState> getRegisterFormState() {
        return registerFormState;
    }

    public LiveData<RegisterResult> getRegisterResult() {
        return registerResult;
    }

    public void register(String username, String password) {
        Result<RegisteredUser> result = registerRepository.register(username, password);

        if (result instanceof Result.Success) {
            RegisteredUser data = ((Result.Success<RegisteredUser>) result).getData();
            registerResult.setValue(new RegisterResult(
                    new RegisteredUserView(
                            data.getUserId(),
                            data.getDisplayName()
                    )
            ));
        } else {
            registerResult.setValue(new RegisterResult(R.string.register_failed));
        }
    }

    public void registerDataChanged(String username, String password) {
        Log.d("REGISTER", "registerDataChanged: username=" + username + " password=" + password);
        if (!isUserNameValid(username)) {
            registerFormState.setValue(new RegisterFormState(R.string.invalid_username, null));
        } else if (!isPasswordValid(password)) {
            registerFormState.setValue(new RegisterFormState(null, R.string.invalid_password));
        } else {
            registerFormState.setValue(new RegisterFormState(true));
        }
    }

    private boolean isUserNameValid(String username) {
        return username != null && username.trim().length() >= 3;
    }

    private boolean isPasswordValid(String password) {
        return password != null && password.length() >= 6;
    }
}
