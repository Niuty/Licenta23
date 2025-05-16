package com.example.chatlicenta.ui.login;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.chatlicenta.R;
import com.example.chatlicenta.data.model.LoggedInUser;
import com.example.chatlicenta.data.session.SessionManager;
import com.example.chatlicenta.databinding.ActivityLoginBinding;
import com.example.chatlicenta.ui.main.MainActivity;
import com.example.chatlicenta.ui.register.RegisterActivity;

public class LoginActivity extends AppCompatActivity {

    private LoginViewModel loginViewModel;
    private ActivityLoginBinding binding;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // verifică dacă e deja autentificat
        SessionManager session = new SessionManager(this);
        if (session.isLoggedIn()) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
            return;
        }

        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        loginViewModel = new ViewModelProvider(this, new LoginViewModelFactory())
                .get(LoginViewModel.class);

        EditText usernameEditText = binding.editTextUsername;
        EditText passwordEditText = binding.editTextPassword;
        Button loginButton = binding.buttonLogin;

        loginViewModel.getLoginFormState().observe(this, new Observer<LoginFormState>() {
            @Override public void onChanged(@Nullable LoginFormState state) {
                if (state == null) return;
                loginButton.setEnabled(state.isDataValid());
                if (state.getUsernameError() != null)
                    usernameEditText.setError(getString(state.getUsernameError()));
                if (state.getPasswordError() != null)
                    passwordEditText.setError(getString(state.getPasswordError()));
            }
        });

        loginViewModel.getLoginResult().observe(this, new Observer<LoginResult>() {
            @Override public void onChanged(@Nullable LoginResult result) {
                if (result == null) return;

                if (result.getError() != null) {
                    // ─── la eroare: doar afişăm toast, apoi oprim aici
                    showLoginFailed(result.getError());
                    return;
                }

                if (result.getSuccess() != null) {
                    updateUiWithUser(result.getSuccess());
                    // ─── la succes: închidem activity-ul
                    setResult(Activity.RESULT_OK);
                    finish();
                }
            }
        });

        loginViewModel.loginDataChanged(
                binding.editTextUsername.getText().toString(),
                binding.editTextPassword.getText().toString()
        );

        TextWatcher afterTextChangedListener = new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override public void afterTextChanged(Editable s) {
                loginViewModel.loginDataChanged(
                        usernameEditText.getText().toString(),
                        passwordEditText.getText().toString());
            }
        };


        usernameEditText.addTextChangedListener(afterTextChangedListener);
        passwordEditText.addTextChangedListener(afterTextChangedListener);
        passwordEditText.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                loginViewModel.login(
                        usernameEditText.getText().toString(),
                        passwordEditText.getText().toString());
                return true;
            }
            return false;
        });

        loginButton.setOnClickListener(v ->
                loginViewModel.login(
                        usernameEditText.getText().toString(),
                        passwordEditText.getText().toString()));

        binding.textViewRegister.setOnClickListener(v ->
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class)));
    }

    private void updateUiWithUser(LoggedInUserView model) {
        String welcome = getString(R.string.welcome) + model.getDisplayName();
        Toast.makeText(getApplicationContext(), welcome, Toast.LENGTH_LONG).show();

        // salvează user-ul în sesiune
        SessionManager session = new SessionManager(this);
        session.saveUser(
                new LoggedInUser(model.getUserId(), model.getDisplayName()));

        startActivity(new Intent(LoginActivity.this, MainActivity.class));
        finish();
    }

    private void showLoginFailed(@StringRes Integer errorString) {
        Toast.makeText(getApplicationContext(), errorString, Toast.LENGTH_SHORT).show();
    }
}
