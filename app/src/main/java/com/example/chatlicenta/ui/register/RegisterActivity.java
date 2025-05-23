package com.example.chatlicenta.ui.register;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.inputmethod.EditorInfo;
import android.widget.Toast;

import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.chatlicenta.R;
import com.example.chatlicenta.databinding.ActivityRegisterBinding;
import com.example.chatlicenta.ui.main.MainActivity;

public class RegisterActivity extends AppCompatActivity {

    private RegisterViewModel registerViewModel;
    private ActivityRegisterBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Initialize ViewModel with factory that takes Context
        registerViewModel = new ViewModelProvider(
                this,
                new RegisterViewModelFactory(this)
        ).get(RegisterViewModel.class);

        registerViewModel.getRegisterFormState().observe(this, formState -> {
            if (formState == null) return;

            binding.buttonRegisterConfirm.setEnabled(formState.isDataValid());

            if (formState.getEmailError() != null) {
                binding.editTextEmailRegister.setError(getString(formState.getEmailError()));
            }
            if (formState.getUsernameError() != null) {
                binding.editTextNewUsernameRegister.setError(getString(formState.getUsernameError()));
            }
            if (formState.getPasswordError() != null) {
                binding.editTextNewPasswordRegister.setError(getString(formState.getPasswordError()));
            }
        });

        registerViewModel.getRegisterResult().observe(this, result -> {
            if (result == null) return;

            if (result.getError() != null) {
                showRegisterFailed(result.getError());
            }
            if (result.getSuccess() != null) {
                // Registration successful, navigate to MainActivity
                startActivity(new Intent(this, MainActivity.class));
                setResult(Activity.RESULT_OK);
                finish();
            }
        });

        TextWatcher afterTextChanged = new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override public void afterTextChanged(Editable s) {
                registerViewModel.registerDataChanged(
                        binding.editTextEmailRegister.getText().toString(),
                        binding.editTextNewUsernameRegister.getText().toString(),
                        binding.editTextNewPasswordRegister.getText().toString()
                );
            }
        };

        binding.editTextEmailRegister.addTextChangedListener(afterTextChanged);
        binding.editTextNewUsernameRegister.addTextChangedListener(afterTextChanged);
        binding.editTextNewPasswordRegister.addTextChangedListener(afterTextChanged);

        binding.editTextNewPasswordRegister.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                attemptRegister();
                return true;
            }
            return false;
        });

        binding.buttonRegisterConfirm.setOnClickListener(v -> attemptRegister());
    }

    private void attemptRegister() {
        String email       = binding.editTextEmailRegister.getText().toString().trim();
        String displayName = binding.editTextNewUsernameRegister.getText().toString().trim();
        String password    = binding.editTextNewPasswordRegister.getText().toString();
        registerViewModel.register(email, displayName, password);
    }

    private void showRegisterFailed(@StringRes Integer errorString) {
        Toast.makeText(getApplicationContext(), getString(errorString), Toast.LENGTH_SHORT).show();
    }
}
