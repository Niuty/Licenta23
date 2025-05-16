package com.example.chatlicenta.ui.chat;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

/**
 * Factory-ul care crează ChatViewModel și-i furnizează conversationId.
 */
public class ChatViewModelFactory implements ViewModelProvider.Factory {

    private final Application application;
    private final String conversationId;

    public ChatViewModelFactory(@NonNull Application application,
                                @NonNull String conversationId) {
        this.application    = application;
        this.conversationId = conversationId;
    }

    @NonNull @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(ChatViewModel.class)) {
            //noinspection unchecked
            return (T) new ChatViewModel(application, conversationId);
        }
        throw new IllegalArgumentException("Unknown ViewModel class: " + modelClass.getName());
    }
}
