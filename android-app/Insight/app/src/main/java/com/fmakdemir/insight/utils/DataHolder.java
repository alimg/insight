package com.fmakdemir.insight.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Environment;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;

/**
 * @author fma
 * @date 21.07.2014.
 * these are getter/setter methods for common objects
 */
public class DataHolder {

	public static final String PREF_FILE = "insight_prefs";
	public static final String PREF_NAME_SERVER_URL = "IPREFS_SERVER_URL";
	public static final int REQ_INSIGHT_SCAN_QR = 0xff0;

	private static HttpClient httpClient;
	private static String serverHostname = "192.168.42.180";
	private static String serverProtocol = "http://";

	private static Context C;
	private static Drawable d;

	public static void setContext(Context C) {
		DataHolder.C = C;
		Helper.setContext(C);
	}

	// common HttpClient for cookies
	public static HttpClient getHttpClient() {
		if (httpClient == null) {
			httpClient = new DefaultHttpClient();
		}
		return httpClient;
	}

	public static String getServerUrl() {
		return serverProtocol + serverHostname;
	}

	public static String getServerHostname() {
		return serverHostname;
	}

	public static String getDir() {
//		return Environment.getExternalStorageDirectory().toString();
//		return C.getFilesDir().getAbsolutePath();
		return Environment.getDownloadCacheDirectory().toString();
	}

	public static void setD(Drawable d) {
		DataHolder.d = d;
	}

	public static Drawable getD() {
		return d;
	}

}
