package com.fmakdemir.insight.webservice.request;

import com.fmakdemir.insight.utils.DataHolder;
import com.fmakdemir.insight.webservice.model.BaseResponse;
import com.fmakdemir.insight.webservice.model.LoginResponse;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class UserWebApiHandler extends BaseWebApiHandler {
    private static final String URL_BASE = DataHolder.getServerUrl();
    private static final String URL_LOGIN = URL_BASE+"/login";
    private static final String URL_REGISTER = URL_BASE+"/register";


    public static void loginUser(String userName, String password,
                          WebApiCallback<LoginResponse> callback) {
        HttpPost req = new HttpPost(URL_LOGIN);

        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        nameValuePairs.add(new BasicNameValuePair("name", userName));
        nameValuePairs.add(new BasicNameValuePair("password", password));
        try {
            req.setEntity(new UrlEncodedFormEntity(nameValuePairs, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        connect(req, callback, LoginResponse.class);
    }


    public static void registerUser(String userName, String password, String email,
                          WebApiCallback<BaseResponse> callback) {
        HttpPost req = new HttpPost(URL_REGISTER);

        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        nameValuePairs.add(new BasicNameValuePair("name", userName));
        nameValuePairs.add(new BasicNameValuePair("password", password));
        nameValuePairs.add(new BasicNameValuePair("email", email));
        try {
            req.setEntity(new UrlEncodedFormEntity(nameValuePairs, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        connect(req, callback, BaseResponse.class);
    }

}
