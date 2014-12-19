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
	public static final String EXT_INSIGHT_EMAIL = "MainAct.ext_insight_email";

	private BootstrapButton btnGetImg;
	private BootstrapEditText edtQRString;
//	private AsyncImageGetter asyncImageGetter;

	String insightIid, insightEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

		Helper.setContext(getApplicationContext());

		insightIid = getIntent().getStringExtra(MainActivity.EXT_INSIGHT_IID);
		insightEmail = getIntent().getStringExtra(MainActivity.EXT_INSIGHT_EMAIL);

		btnGetImg = (BootstrapButton) findViewById(R.id.btn_get_img);
		edtQRString = (BootstrapEditText) findViewById(R.id.edt_qr_str);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.login, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
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
		Intent intent;
		switch (v.getId()) {
			case R.id.btn_get_img:
				btnGetImg.setEnabled(false);
				//asyncImageGetter = new AsyncImageGetter("testuser", "testpass");
				//asyncImageGetter.execute();
				new AsyncImageGetter(insightIid, insightEmail).execute();
				break;
			case R.id.btn_show_img:
				startActivity(new Intent(this, ImageTestActivity.class));
				break;
			case R.id.btn_make_qr:
				intent = new Intent(this, ImageTestActivity.class);
				intent.putExtra(ImageTestActivity.EXT_MAKE_QR, true);
				intent.putExtra(ImageTestActivity.EXT_QR_STR, edtQRString.getText().toString());
				startActivity(intent);
				break;
			case R.id.btn_wifi_setup:
				startActivity(new Intent(this, WifiSetupActivity.class));
				break;
		}
	}

	public class AsyncImageGetter extends AsyncTask<Void, Void, String> {
		private ArrayList<NameValuePair> mData = new ArrayList<NameValuePair>();

		/**
		 * constructor
		 */
		public AsyncImageGetter(String insightId, String email) {
			// add data to post data

			mData.add(new BasicNameValuePair("insight_iid", insightId));
			mData.add(new BasicNameValuePair("insight_email", email));
		}

		/**
		 * background
		 */
		@Override
		protected String doInBackground(Void... voids) {
			byte[] result;
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
					Helper.storeImage(entity.getContent(), "testimg.png");
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
}
