package com.fmakdemir.insight.webservice.request;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.fmakdemir.insight.webservice.model.BaseResponse;
import com.google.gson.Gson;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

public class BaseWebApiHandler {


    protected static <T extends BaseResponse> void connect(final HttpUriRequest request, final WebApiCallback cb, final Class<T> returnType) {
        new Thread(new Runnable() {
            @Override
            public void run() {

                HttpClient client = new DefaultHttpClient();
                try {
					Log.i("XXX", request.getURI()+"\n"+request.getParams());
                    HttpResponse response = client.execute(request);
                    String responseStr = EntityUtils.toString(response.getEntity());
                    Log.d("ad", responseStr);
                    Gson gson = new Gson();
                    final T obj = gson.fromJson(responseStr, returnType);
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            cb.onSuccess(obj);
                        }
                    });
                    return;
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (IllegalStateException e) {
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

}
