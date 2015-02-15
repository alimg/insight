package com.fmakdemir.insight;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.fmakdemir.insight.utils.DataHolder;
import com.fmakdemir.insight.utils.Helper;
import com.fmakdemir.insight.webservice.LoginService;
import com.fmakdemir.insight.webservice.SessionStatusListener;
import com.fmakdemir.insight.webservice.model.User;

public class SplashActivity extends Activity {

    private LoginService loginService;
    private View loginButton;
    private EditText editUsername;
    private EditText editPassword;
    private LoginService.LoginListener mLoginListener = new LoginService.LoginListener() {
        @Override
        public void loginSuccess(User user) {
            startActivity(new Intent(SplashActivity.this, HomeActivity.class));
            finish();
        }

        @Override
        public void loginFailed() {
            Toast.makeText(SplashActivity.this, "login failed", Toast.LENGTH_SHORT).show();
        }
    };
    private View registerButton;
    private SessionStatusListener mSessionListener = new SessionStatusListener() {
        @Override
        public void onSuccess() {
            startActivity(new Intent(SplashActivity.this, HomeActivity.class));
            finish();
        }

        @Override
        public void onFail() {
            startActivity(getIntent());
            finish();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

		Helper.setContext(getApplicationContext());

        loginService = LoginService.getInstance(this);
        if (loginService.isLoggedin()) {
            loginService.validateSession(mSessionListener);
            return;
        }
        setContentView(R.layout.activity_splash);


        loginButton = findViewById(R.id.btn_login);
        editUsername = (EditText) findViewById(R.id.edit_name);
        editPassword = (EditText) findViewById(R.id.edit_password);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginService.login(editUsername.getText().toString(),
                        editPassword.getText().toString(), mLoginListener);
            }
        });

        registerButton = findViewById(R.id.btn_register);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SplashActivity.this, RegisterActivity.class));
            }
        });
    }
}
