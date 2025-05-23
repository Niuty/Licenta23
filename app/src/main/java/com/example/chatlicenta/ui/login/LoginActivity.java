package com.example.chatlicenta.ui.login;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.chatlicenta.R;
import com.example.chatlicenta.ui.main.MainActivity;
import com.example.chatlicenta.ui.register.RegisterActivity;

public class LoginActivity extends AppCompatActivity {

    private LoginViewModel loginViewModel;
    private EditText       emailEdit, passEdit;
    private Button         loginButton;
    private TextView       registerLink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        emailEdit    = findViewById(R.id.editTextEmailLogin);
        passEdit     = findViewById(R.id.editTextPasswordLogin);
        loginButton  = findViewById(R.id.buttonLogin);
        registerLink = findViewById(R.id.textViewRegister);

        // Initialize ViewModel
        loginViewModel = new ViewModelProvider(
                this,
                new LoginViewModelFactory(this)
        ).get(LoginViewModel.class);

        // Observă validarea formularului
        loginViewModel.getLoginFormState().observe(this, formState -> {
            if (formState == null) return;
            loginButton.setEnabled(formState.isDataValid());
            if (formState.getEmailError() != null) {
                emailEdit.setError(getString(formState.getEmailError()));
            }
            if (formState.getPasswordError() != null) {
                passEdit.setError(getString(formState.getPasswordError()));
            }
        });

        // Observă rezultatul login-ului
        loginViewModel.getLoginResult().observe(this, result -> {
            if (result == null) return;
            if (result.getError() != null) {
                showLoginFailed(result.getError());
            }
            if (result.getSuccess() != null) {
                // Login reușit
                startActivity(new Intent(this, MainActivity.class));
                finish();
            }
        });

        // TextWatcher pentru validare on-the-fly
        TextWatcher tw = new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override public void afterTextChanged(Editable s) {
                loginViewModel.loginDataChanged(
                        emailEdit.getText().toString(),
                        passEdit.getText().toString()
                );
            }
        };
        emailEdit.addTextChangedListener(tw);
        passEdit.addTextChangedListener(tw);

        // Acțiune buton Login
        loginButton.setOnClickListener(v ->
                loginViewModel.login(
                        emailEdit.getText().toString().trim(),
                        passEdit.getText().toString()
                )
        );

        // Link către RegisterActivity
        registerLink.setOnClickListener(v ->
                startActivity(new Intent(this, RegisterActivity.class))
        );
    }

    private void showLoginFailed(@StringRes Integer errorString) {
        Toast.makeText(getApplicationContext(), R.string.error_login, Toast.LENGTH_SHORT).show();
    }
}
