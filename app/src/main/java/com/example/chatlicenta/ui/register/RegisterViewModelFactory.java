package com.example.chatlicenta.ui.register;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.chatlicenta.data.repository.UserRepository;
import com.example.chatlicenta.ui.register.RegisterViewModel;

/**
 * Factory pentru RegisterViewModel, oferind UserRepository ca dependență.
 */
public class RegisterViewModelFactory implements ViewModelProvider.Factory {
    private final Context context;

    public RegisterViewModelFactory(@NonNull Context context) {
        this.context = context.getApplicationContext();
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(RegisterViewModel.class)) {
            UserRepository userRepo = new UserRepository(context);
            //noinspection unchecked
            return (T) new RegisterViewModel(userRepo);
        }
        throw new IllegalArgumentException("Unknown ViewModel class: " + modelClass.getName());
    }
}
