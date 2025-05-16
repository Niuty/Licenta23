package com.example.chatlicenta.ui.register;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.Toast;

import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.chatlicenta.R;
import com.example.chatlicenta.data.model.LoggedInUser;
import com.example.chatlicenta.data.session.SessionManager;
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

        registerViewModel = new ViewModelProvider(this, new RegisterViewModelFactory())
                .get(RegisterViewModel.class);

        registerViewModel.getRegisterFormState().observe(this, formState -> {
            if (formState == null) return;

            binding.buttonRegisterConfirm.setEnabled(formState.isDataValid());

            if (formState.getUsernameError() != null) {
                binding.editTextNewUsername.setError(getString(formState.getUsernameError()));
            }
            if (formState.getPasswordError() != null) {
                binding.editTextNewPassword.setError(getString(formState.getPasswordError()));
            }
        });

        registerViewModel.registerDataChanged(
                binding.editTextNewUsername.getText().toString(),
                binding.editTextNewPassword.getText().toString()
        );

        registerViewModel.getRegisterResult().observe(this, result -> {
            if (result == null) return;

            if (result.getError() != null) {
                showRegisterFailed(result.getError());
            }
            if (result.getSuccess() != null) {
                RegisteredUserView user = result.getSuccess();
                // 1) arată toast
                updateUiWithUser(user);
                // 2) salvează sesiunea
                new SessionManager(this)
                        .saveUser(new LoggedInUser(user.getUserId(), user.getDisplayName()));
                // 3) trimite rezultatul și închide
                setResult(Activity.RESULT_OK);
                finish();
                // 4) deschide MainActivity
                startActivity(new Intent(this, MainActivity.class));
            }
        });

        TextWatcher afterTextChanged = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                registerViewModel.registerDataChanged(
                        binding.editTextNewUsername.getText().toString(),
                        binding.editTextNewPassword.getText().toString()
                );
            }
        };

        binding.editTextNewUsername.addTextChangedListener(afterTextChanged);
        binding.editTextNewPassword.addTextChangedListener(afterTextChanged);
        binding.editTextNewPassword.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                registerViewModel.register(
                        binding.editTextNewUsername.getText().toString(),
                        binding.editTextNewPassword.getText().toString()
                );
                return true;
            }
            return false;
        });

        binding.buttonRegisterConfirm.setOnClickListener(v ->
                registerViewModel.register(
                        binding.editTextNewUsername.getText().toString(),
                        binding.editTextNewPassword.getText().toString()
                ));
    }

    private void updateUiWithUser(RegisteredUserView model) {
        String welcomeMessage = getString(R.string.welcome) + model.getDisplayName();
        Toast.makeText(getApplicationContext(), welcomeMessage, Toast.LENGTH_LONG).show();
    }

    private void showRegisterFailed(@StringRes Integer errorString) {
        Toast.makeText(getApplicationContext(), errorString, Toast.LENGTH_SHORT).show();
    }
}
