package com.fmakdemir.insight.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * @author fma
 * @date 26.12.2014.
 */
public class MediaStorageHelper {

	public static final String PHOTO_EXT = ".png";
	public static final String SOUND_EXT = ".mp3";
	private static Context C;
	public static File PHOTO_DIR, SOUND_DIR;

	public static void init(Context context) {
		C = context;
		PHOTO_DIR = C.getDir("photos", Context.MODE_PRIVATE);
		SOUND_DIR = C.getDir("sounds", Context.MODE_PRIVATE);
	}

	public static void storePhoto(InputStream inputStream) throws IOException {

		String filename = ""+System.currentTimeMillis()+".png";

		File file = new File(PHOTO_DIR, filename);
		Log.i("StoreImage", "file path: " + file.getAbsolutePath());

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

	public static void storeSound(InputStream inputStream) throws IOException {

		String filename = DataHolder.TEST_SND;

		File file = new File(SOUND_DIR, filename);
		Log.i("StoreSound", "file path: "+file.getAbsolutePath());

		FileOutputStream fos = C.openFileOutput(filename, Context.MODE_PRIVATE);
		int c;
		while ((c = inputStream.read()) != -1) {
			fos.write(c);
		}
		inputStream.close();
		fos.close();

		String logStr = ".\ncount = "+C.fileList().length;
		for (String s : SOUND_DIR.list()) {
			logStr += "\n"+s;
		}
		Log.i("FL", logStr);
	}

	public static ArrayList<String> listPhotos() {
		ArrayList<String> list = new ArrayList<String>();
		String logStr = "";
		for (String s : PHOTO_DIR.list()) {
			logStr += "\n"+s;
			list.add(s);
		}
		Log.i("Photo List", logStr);
		return list;
	}

	public static ArrayList<String> listSounds() {
		ArrayList<String> list = new ArrayList<String>();
		String logStr = "";
		for (String s : SOUND_DIR.list()) {
			logStr += "\n"+s;
			list.add(s);
		}
		Log.i("Sound List", logStr);
		return list;
	}

	public static Drawable retrievePhoto(String filename) throws IOException {

		Log.i("XY", ""+PHOTO_DIR);
		File file = new File(PHOTO_DIR, filename);
		Log.i("RetrieveImage", "file path: " + file.getAbsolutePath());

		if (file.exists()) {
			return Drawable.createFromPath(file.getAbsolutePath());
		} else {
			throw new IOException("File not found on path: "+file.getAbsolutePath(), null);
		}
	}

	public static void playSound(String filename) {
		String path =  new File(SOUND_DIR, filename).getAbsolutePath();
		new AudioAsynctask().play(path);
	}

}
