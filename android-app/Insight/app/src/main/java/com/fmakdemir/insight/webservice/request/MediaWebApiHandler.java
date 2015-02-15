package com.fmakdemir.insight.webservice.request;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.fmakdemir.insight.utils.DataHolder;
import com.fmakdemir.insight.webservice.model.BaseResponse;
import com.fmakdemir.insight.webservice.model.EventListResponse;
import com.google.gson.Gson;
import com.google.gson.JsonParseException;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author fma
 * @date 19.12.2014.
 */
public class MediaWebApiHandler extends BaseWebApiHandler {
	private static final String URL_BASE = DataHolder.getServerUrl();
    private static final String URL_DOWNLOAD_DATA = URL_BASE+"/events/data";
	private static final String URL_LIST_EVENTS = URL_BASE+"/events/list";


	public static void downloadEventData(String session, String eventId, File target,
									   WebApiCallback<BaseResponse> callback) {
		HttpPost req = new HttpPost(URL_DOWNLOAD_DATA);

		List<NameValuePair> nameValuePairs = new ArrayList<>();
        nameValuePairs.add(new BasicNameValuePair("session", session));
        nameValuePairs.add(new BasicNameValuePair("eventid", eventId));

		try {
			req.setEntity(new UrlEncodedFormEntity(nameValuePairs, "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		download(req, target, callback);
	}

    private static void download(final HttpPost request, final File target, final WebApiCallback<BaseResponse> cb) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpClient client = DataHolder.getHttpClient();
                try {
                    Log.i("XXX", request.getURI() + "\n" + request.getParams());
                    HttpResponse response = client.execute(request);
                    String contentType = response.getEntity().getContentType().getValue();
                    if (contentType.equals("text/html") || contentType.equals("application/json")) {
                        String responseStr = EntityUtils.toString(response.getEntity());
                        Log.d("ad", responseStr);
                        Gson gson = new Gson();
                        final BaseResponse obj = gson.fromJson(responseStr, BaseResponse.class);
                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                cb.onSuccess(obj);
                            }
                        });
                    } else {
                        FileOutputStream fout = new FileOutputStream(target);
                        InputStream in = response.getEntity().getContent();
                        byte buffer[] = new byte[1024];
                        int bytes;
                        while ((bytes = in.read(buffer)) > -1) {
                            fout.write(buffer, 0, bytes);
                        }
                        fout.close();
                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                cb.onSuccess(new BaseResponse());
                            }
                        });
                    }
                    return;
                } catch (JsonParseException | IOException e) {
                    e.printStackTrace();
                }
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        cb.onError("");
                    }
                });
            }
        }).start();
    }


    public static void listEvents(String session, WebApiCallback<EventListResponse> callback) {
        HttpPost req = new HttpPost(URL_LIST_EVENTS);
        
        List<NameValuePair> nameValuePairs = new ArrayList<>();
        nameValuePairs.add(new BasicNameValuePair("session", session));

        try {
            req.setEntity(new UrlEncodedFormEntity(nameValuePairs, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        connect(req, callback, EventListResponse.class);
    }
}
