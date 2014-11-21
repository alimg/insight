package com.fmakdemir.insight;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.fmakdemir.insight.R;
import com.fmakdemir.insight.webservice.WebApiConstants;
import com.fmakdemir.insight.webservice.model.BaseResponse;
import com.fmakdemir.insight.webservice.request.UserWebApiHandler;
import com.fmakdemir.insight.webservice.request.WebApiCallback;

public class RegisterActivity extends Activity {

    private EditText editName;
    private EditText editPassword;
    private EditText editEmail;
    private View btnRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        editName = (EditText) findViewById(R.id.edit_name);
        editPassword = (EditText) findViewById(R.id.edit_password);
        editEmail = (EditText) findViewById(R.id.edit_email);

        btnRegister = findViewById(R.id.btn_register);
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = editName.getText().toString();
                String password = editPassword.getText().toString();
                String email = editEmail.getText().toString();
                UserWebApiHandler.registerUser(username, password, email, new WebApiCallback<BaseResponse>() {
                    @Override
                    public void onSuccess(BaseResponse data) {
                        if(data.status.equals(WebApiConstants.STATUS_SUCCESS)) {
                            Toast.makeText(RegisterActivity.this, "Registration successful.", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Toast.makeText(RegisterActivity.this, "Registration failed.", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onError(String cause) {
                        Toast.makeText(RegisterActivity.this, "Registration failed.", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

    }


}
