package com.fmakdemir.insight.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

/**
 * @author fma
 * @date 07.11.2014.
 */
public class Helper {

	private static Context C;
	public static void setContext(Context C) {
		DataHolder.setContext(C);
		MediaStorageHelper.init(C);
		Helper.C = C;
		SharedPreferences.Editor e = C.getSharedPreferences(DataHolder.PREF_FILE, Context.MODE_PRIVATE).edit();
		e.apply();
	}

	public static void toastIt(String msg) {
		toastIt(msg, Toast.LENGTH_SHORT);
	}
	public static void toastIt(String msg, int len) {
		Toast.makeText(C, msg, len).show();
	}

	public static String getTag(Object o) {return o.getClass().getSimpleName();}

	public static String getExceptionString(Exception e) {
		return  ".\nType: "+e.getClass().getSimpleName()
				+"\nCause: "+e.getCause()
				+"\nMessage: "+e.getMessage();
	}

	public static String getEmail() {
		SharedPreferences prefs = C.getSharedPreferences(DataHolder.PREF_FILE, Context.MODE_PRIVATE);
		Log.i("SS", ""+prefs);
		return prefs.getString(DataHolder.PREFS_EMAIL, "test@mail.com");
	}

	public static String getUsername() {
		SharedPreferences prefs = C.getSharedPreferences(DataHolder.PREF_FILE, Context.MODE_PRIVATE);
		return prefs.getString(DataHolder.PREFS_USERNAME, "test");
	}

	public static void putUsername(String username) {
		SharedPreferences.Editor e = C.getSharedPreferences(DataHolder.PREF_FILE, Context.MODE_PRIVATE).edit();
		e.putString(DataHolder.PREFS_USERNAME, username);
		e.apply();
	}

	public static void putEmail(String email) {
		SharedPreferences.Editor e = C.getSharedPreferences(DataHolder.PREF_FILE, Context.MODE_PRIVATE).edit();
		e.putString(DataHolder.PREFS_EMAIL, email);
		e.apply();
	}

}
