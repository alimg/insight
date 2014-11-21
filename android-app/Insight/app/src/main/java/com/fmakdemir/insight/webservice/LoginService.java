package com.fmakdemir.insight.webservice;

import android.content.Context;
import android.content.SharedPreferences;

import com.fmakdemir.insight.webservice.model.LoginResponse;
import com.fmakdemir.insight.webservice.model.User;
import com.fmakdemir.insight.webservice.request.BaseWebApiHandler;
import com.fmakdemir.insight.webservice.request.UserWebApiHandler;
import com.fmakdemir.insight.webservice.request.WebApiCallback;

public class LoginService {

    private static final String PREF_NAME = "LoginService";
    private static final String KEY_SESSION_TOKEN = "session-token";
    private final Context context;
    private String sessionToken = null;

    private LoginService(Context context) {
        this.context = context;
        init();
    }


    public static LoginService getInstance(Context context) {

        return new LoginService(context);
    }

    private void init() {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        sessionToken = prefs.getString(KEY_SESSION_TOKEN, null);

    }

    public boolean isLoggedin(){
        return getSessionToken() != null;
    }

    public void login(String username, String password, final LoginListener listener) {
        UserWebApiHandler.loginUser(username, password, new WebApiCallback<LoginResponse>() {
            @Override
            public void onSuccess(LoginResponse data) {
                if (data.status.equals(WebApiConstants.STATUS_SUCCESS)) {
                    SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
                    SharedPreferences.Editor e = prefs.edit();
                    e.putString(KEY_SESSION_TOKEN, data.session_token);
                    e.commit();
                    listener.loginSuccess(data.user);
                }
                else {
                    listener.loginFailed();
                }
            }

            @Override
            public void onError(String cause) {
                listener.loginFailed();
            }
        });
    }

    public String getSessionToken() {
        return sessionToken;
    }

    public interface LoginListener {
        public void loginSuccess(User user);
        public void loginFailed();
    }
}
