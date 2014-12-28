package com.fmakdemir.insight;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

import com.fmakdemir.insight.utils.MediaStorageHelper;

import java.io.IOException;


public class ImageTestActivity extends Activity {

	public static final String EXT_IMG_NAME = "ImageTestAct.ext_img_name";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_image_test);

		String imageName = getIntent().getStringExtra(EXT_IMG_NAME);
		try {
			((ImageView) findViewById(R.id.img_view)).setImageBitmap(MediaStorageHelper.retrievePhoto(imageName));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.image_test, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		switch (id) {
			case R.id.action_settings:
				return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
