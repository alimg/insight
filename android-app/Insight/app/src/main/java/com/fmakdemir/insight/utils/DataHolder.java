package com.fmakdemir.insight.utils;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.util.Log;

import com.fmakdemir.insight.adapters.InsightListAdapter;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;

import java.util.Hashtable;

/**
 * @author fma
 * @date 21.07.2014.
 * these are getter/setter methods for common objects
 */
public class DataHolder {

	public static final String PREF_FILE = "insight_prefs";
	public static final String PREFS_EMAIL = "insight_prefs.email";
	public static final String PREFS_USERNAME = "insight_prefs.username";

	public static final int REQ_INSIGHT_SCAN_QR = 0xff0;
	public static final String SENDER_ID = "513291732910";
	public static final String TEST_SND = "testsnd.mp3";

	private static HttpClient httpClient;
	//final private static String serverHostname = "10.42.1.1:5000";
	final private static String serverHostname = "128.199.52.88:5000";
	//final private static String serverHostname = "188.166.5.98:5000";
	final private static String serverProtocol = "http://";

	private static InsightListAdapter listAdapter;
//	private static String latestEventId;

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

	public static void setListAdapter(InsightListAdapter adapter) {
		listAdapter = adapter;
	}
	public static InsightListAdapter getListAdapter() {
		Log.e("DH_LA", ""+listAdapter.getCount());
		return listAdapter;
	}

	private static Hashtable<String, String> dataCache = new Hashtable<>();
	public static void addServerMessage(String key, String value) {
		dataCache.put(key, value);
	}
	public static String getServerMessage(String key) {
		String value = dataCache.get(key);
		dataCache.remove(key);
		return value;
	}

/*	public static String getLatestEventId() {
		String id = latestEventId;
		latestEventId = null;
		return id;
	}

	public static void setLatestEventId(String id) {
		latestEventId = id;
	}*/
}
