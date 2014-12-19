package com.fmakdemir.insight.utils;

import android.content.Context;
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

	public static void toastIt(String msg) {
		toastIt(msg, Toast.LENGTH_SHORT);
	}
	public static void toastIt(String msg, int len) {
		Toast.makeText(C, msg, len).show();
	}
	public static void toastIt(int strId) {
		toastIt(strId, Toast.LENGTH_SHORT);
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

	public static Drawable retrieveImage(String filename) throws IOException {

		File file = new File(C.getFilesDir(), filename);
		Log.i("RetrieveImage", "file path: "+file.getAbsolutePath());

		if (file.exists()) {
			return Drawable.createFromPath(file.getAbsolutePath());
		} else {
			throw new IOException("File not found on path: "+file.getAbsolutePath(), null);
		}
	}

	public static void setContext(Context C) {
		Helper.C = C;
		DataHolder.setContext(C);
	}

	public static String getTag(Object o) {return o.getClass().getSimpleName();}

	public static String getExceptionString(Exception e) {
		return  ".\nType: "+e.getClass().getSimpleName()
				+"\nCause: "+e.getCause()
				+"\nMessage: "+e.getMessage();
	}

}
