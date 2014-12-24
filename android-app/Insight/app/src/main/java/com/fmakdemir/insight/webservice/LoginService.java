package com.fmakdemir.insight.webservice;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.fmakdemir.insight.utils.Helper;
import com.fmakdemir.insight.webservice.model.LoginResponse;
import com.fmakdemir.insight.webservice.model.User;
import com.fmakdemir.insight.webservice.request.UserWebApiHandler;
import com.fmakdemir.insight.webservice.request.WebApiCallback;

public class LoginService {

    public static final String PREF_NAME = "LoginService";
    public static final String KEY_SESSION_TOKEN = "session-token";
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
        String token = getSessionToken();
        return token != null && !token.isEmpty();
    }

    public void login(final String username, String password, final LoginListener listener) {
        UserWebApiHandler.loginUser(username, password, new WebApiCallback<LoginResponse>() {
            @Override
            public void onSuccess(LoginResponse data) {
                if (data.status.equals(WebApiConstants.STATUS_SUCCESS)) {
                    SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
                    SharedPreferences.Editor e = prefs.edit();
                    e.putString(KEY_SESSION_TOKEN, data.session_token);
					e.apply();

					Helper.putUsername(data.user.name);
					Helper.putEmail(data.user.email);

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
		Log.i("token", ""+sessionToken);
        return sessionToken;
    }

    public void logout() {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor e = prefs.edit();
        e.remove(KEY_SESSION_TOKEN);
        e.apply();
    }

    public interface LoginListener {
        public void loginSuccess(User user);
        public void loginFailed();
    }
}
