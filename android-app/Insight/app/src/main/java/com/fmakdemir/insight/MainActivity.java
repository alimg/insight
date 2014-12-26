package com.fmakdemir.insight;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.beardedhen.androidbootstrap.BootstrapButton;
import com.beardedhen.androidbootstrap.BootstrapEditText;
import com.fmakdemir.insight.utils.DataHolder;
import com.fmakdemir.insight.utils.Helper;
import com.fmakdemir.insight.utils.MediaStorageHelper;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.ArrayList;

public class MainActivity extends Activity {
	public static final String EXT_INSIGHT_IID = "MainAct.ext_insight_iid";

	private BootstrapButton btnGetImg, btnGetSnd;

	String insightIid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

		Helper.setContext(getApplicationContext());

		insightIid = getIntent().getStringExtra(MainActivity.EXT_INSIGHT_IID);
		((BootstrapEditText) findViewById(R.id.edit_title_insight)).setText(insightIid);

		btnGetImg = (BootstrapButton) findViewById(R.id.btn_get_img);
		btnGetSnd = (BootstrapButton) findViewById(R.id.btn_get_snd);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.common, menu);
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

	void ToastIt(String str) {
		ToastIt(str, Toast.LENGTH_SHORT);
	}

	void ToastIt(String str, int duration) {
		Toast.makeText(getApplicationContext(), str, duration).show();
	}

	public void onBtnClick(View v) {
		String username = Helper.getUsername();
		switch (v.getId()) {
			case R.id.btn_get_img:
				btnGetImg.setEnabled(false);
				new AsyncImageGetter(insightIid, username).execute();
				break;
			case R.id.btn_list_photos:
				startActivity(new Intent(this, ImageTestActivity.class));
				break;
			case R.id.btn_get_snd:
				btnGetSnd.setEnabled(false);
				new AsyncSoundGetter(insightIid, username).execute();
				break;
			case R.id.btn_play_snd:
				MediaStorageHelper.playSound(DataHolder.TEST_SND);
				break;
			case R.id.btn_wifi_setup:
				startActivity(new Intent(this, WifiSetupActivity.class));
				break;
		}
	}

	static boolean getImage = false, getSound = false;

	public class AsyncImageGetter extends AsyncTask<Void, Void, String> {
		private ArrayList<NameValuePair> mData = new ArrayList<>();

		/**
		 * constructor
		 */
		public AsyncImageGetter(String insightId, String username) {
			// add data to post data

			mData.add(new BasicNameValuePair("insight_id", insightId));
			mData.add(new BasicNameValuePair("username", username));
			if (!getImage) {
				mData.add(new BasicNameValuePair("act", "take"));
			} else {
				mData.add(new BasicNameValuePair("act", "get"));
			}
			getImage = !getImage;
		}

		/**
		 * background
		 */
		@Override
		protected String doInBackground(Void... voids) {
			String errMsg = "";
			HttpClient client = DataHolder.getHttpClient();
			HttpPost post = new HttpPost(DataHolder.getServerUrl()+"/insight/image");

			try {

				post.setEntity(new UrlEncodedFormEntity(mData, "UTF-8"));

				HttpResponse response = client.execute(post);

				StatusLine statusLine = response.getStatusLine();
				int statusCode = statusLine.getStatusCode();
				if(statusCode == HttpURLConnection.HTTP_OK) {
					HttpEntity entity = response.getEntity();
					MediaStorageHelper.storePhoto(entity.getContent());
					return errMsg;
				} else {
					throw new IOException("Download failed, HTTP response code "
							+ statusCode + " - " + statusLine.getReasonPhrase());
				}

			}
			catch (Exception e) {
				e.printStackTrace();
				errMsg = e.getClass().getSimpleName()+"\n"+e.getCause()+"\n"+e.getMessage();
			}
			return errMsg;
		}

		/**
		 * on getting result
		 */
		@Override
		protected void onPostExecute(String errMsg) {

			btnGetImg.setEnabled(true);

			if (errMsg.equals("")) {
				MainActivity.this.startActivity(new Intent(MainActivity.this, ImageTestActivity.class));
				ToastIt("Got image!\n" + errMsg);
			} else {
				Log.e(this.getClass().getSimpleName(), errMsg);
			}

		}
	}

	public class AsyncSoundGetter extends AsyncTask<Void, Void, String> {
		private ArrayList<NameValuePair> mData = new ArrayList<>();

		/**
		 * constructor
		 */
		public AsyncSoundGetter(String insightId, String username) {
			// add data to post data

			mData.add(new BasicNameValuePair("insight_id", insightId));
			mData.add(new BasicNameValuePair("username", username));
			if (!getSound) {
				mData.add(new BasicNameValuePair("act", "take"));
			} else {
				mData.add(new BasicNameValuePair("act", "get"));
			}
			getSound = !getSound;
		}

		/**
		 * background
		 */
		@Override
		protected String doInBackground(Void... voids) {
			String errMsg = "";
			HttpClient client = DataHolder.getHttpClient();
			HttpPost post = new HttpPost(DataHolder.getServerUrl()+"/insight/sound");

			try {

				post.setEntity(new UrlEncodedFormEntity(mData, "UTF-8"));

				HttpResponse response = client.execute(post);

				StatusLine statusLine = response.getStatusLine();
				int statusCode = statusLine.getStatusCode();
				if(statusCode == HttpURLConnection.HTTP_OK) {
					HttpEntity entity = response.getEntity();
					MediaStorageHelper.storeSound(entity.getContent());
					MediaStorageHelper.playSound(DataHolder.TEST_SND);
					return errMsg;
				} else {
					throw new IOException("Download failed, HTTP response code "
							+ statusCode + " - " + statusLine.getReasonPhrase());
				}

			}
			catch (Exception e) {
				e.printStackTrace();
				errMsg = e.getClass().getSimpleName()+"\n"+e.getCause()+"\n"+e.getMessage();
			}
			return errMsg;
		}

		/**
		 * on getting result
		 */
		@Override
		protected void onPostExecute(String errMsg) {

			btnGetSnd.setEnabled(true);

			if (errMsg.equals("")) {
				ToastIt("Got Sound!\n" + errMsg);
			} else {
				Log.e(this.getClass().getSimpleName(), errMsg);
			}

		}
	}
}
