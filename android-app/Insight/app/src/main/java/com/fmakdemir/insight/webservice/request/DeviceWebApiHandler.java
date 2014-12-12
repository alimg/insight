package com.fmakdemir.insight.webservice.request;

import com.fmakdemir.insight.webservice.model.BaseResponse;
import com.fmakdemir.insight.webservice.model.LoginResponse;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class DeviceWebApiHandler extends BaseWebApiHandler {
    private static final String URL_BASE = "http://128.199.52.88:5000";
    private static final String URL_REGISTER_INSIGHT = URL_BASE+"/register_insight";


    public static void registerInsight(String email, String insightId,
                          WebApiCallback<BaseResponse> callback) {
        HttpPost req = new HttpPost(URL_REGISTER_INSIGHT);

        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        nameValuePairs.add(new BasicNameValuePair("insight_iid", insightId));
        nameValuePairs.add(new BasicNameValuePair("insight_email", email));

        try {
            req.setEntity(new UrlEncodedFormEntity(nameValuePairs, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        connect(req, callback, BaseResponse.class);
    }

}
