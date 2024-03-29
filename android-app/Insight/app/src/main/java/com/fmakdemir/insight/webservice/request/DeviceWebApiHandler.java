package com.fmakdemir.insight.webservice.request;

import com.fmakdemir.insight.utils.DataHolder;
import com.fmakdemir.insight.webservice.model.BaseResponse;
import com.fmakdemir.insight.webservice.model.DeviceListResponse;
import com.fmakdemir.insight.webservice.model.DeviceStatusResult;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class DeviceWebApiHandler extends BaseWebApiHandler {
    private static final String URL_BASE = DataHolder.getServerUrl();
    private static final String URL_REGISTER_INSIGHT = URL_BASE+"/register_insight";
	private static final String URL_LIST_INSIGHT = URL_BASE+"/insight_list";
	private static final String URL_INSIGHT_STATUS = URL_BASE+"/insight/status";
	private static final String URL_INSIGHT_COMMAND = URL_BASE+"/insight/command";

    public static void registerInsight(String token, String insightId,
                          WebApiCallback<BaseResponse> callback) {
        HttpPost req = new HttpPost(URL_REGISTER_INSIGHT);

        List<NameValuePair> nameValuePairs = new ArrayList<>();
        nameValuePairs.add(new BasicNameValuePair("insight_id", insightId));
        nameValuePairs.add(new BasicNameValuePair("session", token));

        try {
            req.setEntity(new UrlEncodedFormEntity(nameValuePairs, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        connect(req, callback, BaseResponse.class);
    }

	public static void listInsight(String token, WebApiCallback<DeviceListResponse> callback) {
		HttpPost req = new HttpPost(URL_LIST_INSIGHT);

		List<NameValuePair> nameValuePairs = new ArrayList<>();
		nameValuePairs.add(new BasicNameValuePair("session", token));

		try {
			req.setEntity(new UrlEncodedFormEntity(nameValuePairs, "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		connect(req, callback, DeviceListResponse.class);
	}

    public static void getDeviceStatus(String token, String deviceId,
                                       WebApiCallback<DeviceStatusResult> callback) {

        HttpPost req = new HttpPost(URL_INSIGHT_STATUS);

        List<NameValuePair> nameValuePairs = new ArrayList<>();
        nameValuePairs.add(new BasicNameValuePair("session", token));
        nameValuePairs.add(new BasicNameValuePair("insight_id", deviceId));

        try {
            req.setEntity(new UrlEncodedFormEntity(nameValuePairs, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        connect(req, callback, DeviceStatusResult.class);
    }


    public static void sendCommand(String token, String deviceId, String type,
                                       WebApiCallback<BaseResponse> callback) {

        HttpPost req = new HttpPost(URL_INSIGHT_COMMAND);

        List<NameValuePair> nameValuePairs = new ArrayList<>();
        nameValuePairs.add(new BasicNameValuePair("session", token));
        nameValuePairs.add(new BasicNameValuePair("insight_id", deviceId));
        nameValuePairs.add(new BasicNameValuePair("type", type));

        try {
            req.setEntity(new UrlEncodedFormEntity(nameValuePairs, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        connect(req, callback, BaseResponse.class);
    }

    public static void setDevicesEnabled(String token, String deviceId, boolean enable,WebApiCallback<BaseResponse> callback) {
        HttpPost req = new HttpPost(URL_INSIGHT_COMMAND);

        List<NameValuePair> nameValuePairs = new ArrayList<>();
        nameValuePairs.add(new BasicNameValuePair("session", token));
        nameValuePairs.add(new BasicNameValuePair("insight_id", deviceId));
        nameValuePairs.add(new BasicNameValuePair("type", "setSystemEnabled"));
        nameValuePairs.add(new BasicNameValuePair("enable", enable? "true": "false"));

        try {
            req.setEntity(new UrlEncodedFormEntity(nameValuePairs, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        connect(req, callback, BaseResponse.class);
    }
}
