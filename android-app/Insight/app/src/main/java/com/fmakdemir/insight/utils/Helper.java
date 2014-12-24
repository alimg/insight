package com.fmakdemir.insight.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author fma
 * @date 07.11.2014.
 */
public class Helper {

	private static Context C;
	public static void setContext(Context C) {
		DataHolder.setContext(C);
		Helper.C = C;
		SharedPreferences.Editor e = C.getSharedPreferences(DataHolder.PREF_FILE, Context.MODE_PRIVATE).edit();
		e.apply();
	}

	public static void toastIt(int strId) {
		toastIt(strId, Toast.LENGTH_SHORT);
	}
	public static void toastIt(String msg) {
		toastIt(msg, Toast.LENGTH_SHORT);
	}
	public static void toastIt(String msg, int len) {
		Toast.makeText(C, msg, len).show();
	}

	public static void toastIt(int strId, int len) {
		toastIt(C.getString(strId), len);
	}

	public static void storeImage(InputStream inputStream, String filename) throws IOException {

		File file = new File(C.getFilesDir(), filename);
		Log.i("StoreImage", "file path: "+file.getAbsolutePath());

		FileOutputStream fos = C.openFileOutput(filename, Context.MODE_PRIVATE);
		Bitmap b = BitmapFactory.decodeStream(inputStream);
		b.compress(Bitmap.CompressFormat.PNG, 100, fos);
		inputStream.close();
		fos.close();
		b.recycle();

		String logStr = ".\ncount = "+C.fileList().length;
		for (String s : C.fileList()) {
			logStr += "\n"+s;
		}
		Log.i("FL", logStr);
	}

	public static void storeSound(InputStream inputStream, String filename) throws IOException {

		File file = new File(C.getFilesDir(), filename);
		Log.i("StoreSound", "file path: "+file.getAbsolutePath());

		FileOutputStream fos = C.openFileOutput(filename, Context.MODE_PRIVATE);
		int c;
		while ((c = inputStream.read()) != -1) {
			fos.write(c);
		}
		inputStream.close();
		fos.close();

		String logStr = ".\ncount = "+C.fileList().length;
		for (String s : C.fileList()) {
			logStr += "\n"+s;
		}
		Log.i("FL", logStr);
	}

	public static Drawable retrieveImage(String filename) throws IOException {

		File file = new File(C.getFilesDir(), filename);
		Log.i("RetrieveImage", "file path: " + file.getAbsolutePath());

		if (file.exists()) {
			return Drawable.createFromPath(file.getAbsolutePath());
		} else {
			throw new IOException("File not found on path: "+file.getAbsolutePath(), null);
		}
	}

	public static String getTag(Object o) {return o.getClass().getSimpleName();}

	public static String getExceptionString(Exception e) {
		return  ".\nType: "+e.getClass().getSimpleName()
				+"\nCause: "+e.getCause()
				+"\nMessage: "+e.getMessage();
	}

	public static String getEmail() {
		SharedPreferences prefs = C.getSharedPreferences(DataHolder.PREF_FILE, Context.MODE_PRIVATE);
		return prefs.getString(DataHolder.PREFS_EMAIL, "test@mail.com");
	}

	public static String getUsername() {
		SharedPreferences prefs = C.getSharedPreferences(DataHolder.PREF_FILE, Context.MODE_PRIVATE);
		return prefs.getString(DataHolder.PREFS_USERNAME, "testuser");
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

	public static void playSound(String filename) {
		String path =  new File(C.getFilesDir(), filename).getAbsolutePath();
		new AudioAsynctask().play(path);
	}
}
