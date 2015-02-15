package com.fmakdemir.insight.webservice.request;

import com.fmakdemir.insight.utils.DataHolder;
import com.fmakdemir.insight.utils.Helper;
import com.fmakdemir.insight.webservice.model.BaseResponse;
import com.fmakdemir.insight.webservice.model.EventListResponse;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author fma
 * @date 19.12.2014.
 */
public class MediaWebApiHandler extends BaseWebApiHandler {
	private static final String URL_BASE = DataHolder.getServerUrl();
    private static final String URL_DOWNLOAD_SOUND = URL_BASE+"/insight/sound";
	private static final String URL_LIST_EVENTS = URL_BASE+"/events/list";


	public static void downloadSound(String insightId,
									   WebApiCallback<BaseResponse> callback) {
		HttpPost req = new HttpPost(URL_DOWNLOAD_SOUND);
		String username = Helper.getUsername();

		List<NameValuePair> nameValuePairs = new ArrayList<>();
		nameValuePairs.add(new BasicNameValuePair("insight_id", insightId));
		nameValuePairs.add(new BasicNameValuePair("username", username));

		try {
			req.setEntity(new UrlEncodedFormEntity(nameValuePairs, "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		connect(req, callback, BaseResponse.class);
	}

	public static void downloadImage(String insightId,
									 WebApiCallback<BaseResponse> callback) {
		HttpPost req = new HttpPost(URL_DOWNLOAD_SOUND);
		String username = Helper.getUsername();

		List<NameValuePair> nameValuePairs = new ArrayList<>();
		nameValuePairs.add(new BasicNameValuePair("insight_id", insightId));
		nameValuePairs.add(new BasicNameValuePair("username", username));

		try {
			req.setEntity(new UrlEncodedFormEntity(nameValuePairs, "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		connect(req, callback, BaseResponse.class);
	}
    
    public static void listEvents(String session, String insightId,
                                     WebApiCallback<EventListResponse> callback) {
        HttpPost req = new HttpPost(URL_LIST_EVENTS);
        
        List<NameValuePair> nameValuePairs = new ArrayList<>();
        nameValuePairs.add(new BasicNameValuePair("session", session));
        nameValuePairs.add(new BasicNameValuePair("insight_id", insightId));

        try {
            req.setEntity(new UrlEncodedFormEntity(nameValuePairs, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        connect(req, callback, EventListResponse.class);
    }
}
