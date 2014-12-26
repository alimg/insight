package com.fmakdemir.insight;

import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.fmakdemir.insight.adapters.InsightListAdapter;
import com.fmakdemir.insight.services.InsightMQTTService;
import com.fmakdemir.insight.utils.AudioAsynctask;
import com.fmakdemir.insight.utils.DataHolder;
import com.fmakdemir.insight.utils.Helper;
import com.fmakdemir.insight.webservice.LoginService;
import com.fmakdemir.insight.webservice.WebApiConstants;
import com.fmakdemir.insight.webservice.model.BaseResponse;
import com.fmakdemir.insight.webservice.request.DeviceWebApiHandler;
import com.fmakdemir.insight.webservice.request.WebApiCallback;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.ArrayList;


public class HomeActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

/*		String mDeviceID = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);
		Log.i(getClass().getSimpleName(), "Device ID: "+mDeviceID);
		Intent mMQTTServiceIntent = new Intent(this, InsightMQTTService.class);
		mMQTTServiceIntent.setData(Uri.parse(mDeviceID));
		// Starts the IntentService
		startService(mMQTTServiceIntent);
*/
		ListView listView = (ListView) findViewById(R.id.list_view_insight);

		ArrayList<String> strList = new ArrayList<>();
		InsightListAdapter adapter = new InsightListAdapter(getApplicationContext(), strList);
		DataHolder.setListAdapter(adapter);
		final InsightListAdapter listAdapter = DataHolder.getListAdapter();
		listView.setAdapter(listAdapter);

		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, final View view,
									int position, long id) {

				String insightIid = listAdapter.getItem(position);

				Intent intent = new Intent(HomeActivity.this, MainActivity.class);
				intent.putExtra(MainActivity.EXT_INSIGHT_IID, insightIid);

				Toast.makeText(HomeActivity.this.getApplicationContext(), "ID: "+insightIid, Toast.LENGTH_LONG).show();
				intent.putExtra("InsightId", listAdapter.getItem(position));
				startActivity(intent);
				overridePendingTransition (R.anim.open_next, R.anim.close_main);
			}

		});


/*		DeviceWebApiHandler.listInsight(Helper.getEmail(), new WebApiCallback<BaseResponse>() {
					@Override
					public void onSuccess(BaseResponse data) {
						if (data.status.equals(WebApiConstants.STATUS_SUCCESS)) {
							Helper.toastIt("Listing InSights");
						} else {
							Helper.toastIt("Couldn't fetch InSightList!", Toast.LENGTH_LONG);
						}

					}

					@Override
					public void onError(String cause) {
						Helper.toastIt("Couldn't fetch InSightList!", Toast.LENGTH_LONG);
					}
				}
		);*/
		new AsyncInsightListGetter(Helper.getEmail()).execute();
    }

	public void btnClicked(View v) {
		switch (v.getId()) {
/*			case R.id.btn_logout:
				LoginService.getInstance(this).logout();
				finish();
				break;*/
			case R.id.btn_add_new:
				Intent intent = new Intent(HomeActivity.this, RegisterInsightActivity.class);
				startActivity(intent);
				break;
		}
	}

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
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
				Toast.makeText(getApplicationContext(), "Action Settings", Toast.LENGTH_SHORT).show();
				break;
			case R.id.action_logout:
				LoginService.getInstance(this).logout();
				finish();
				break;
		}
        return super.onOptionsItemSelected(item);
    }
	private class AsyncInsightListGetter extends AsyncTask<Void, Void, String> {
		private ArrayList<NameValuePair> mData = new ArrayList<>();

		/**
		 * constructor
		 */
		public AsyncInsightListGetter(String email) {
			// add data to post data

			mData.add(new BasicNameValuePair("email", email));
		}

		/**
		 * background
		 */
		@Override
		protected String doInBackground(Void... voids) {
			String result = "";
			HttpClient client = DataHolder.getHttpClient();
			HttpPost post = new HttpPost(DataHolder.getServerUrl()+"/insight_list");

			try {

				post.setEntity(new UrlEncodedFormEntity(mData, "UTF-8"));

				HttpResponse response = client.execute(post);

				StatusLine statusLine = response.getStatusLine();
				int statusCode = statusLine.getStatusCode();
				if(statusCode == HttpURLConnection.HTTP_OK) {
					byte[] resEnt = EntityUtils.toByteArray(response.getEntity());
					result = new String(resEnt, "UTF-8");
					return result;

				} else {
					throw new IOException("Download failed, HTTP response code "
							+ statusCode + " - " + statusLine.getReasonPhrase());
				}

			}
			catch (Exception e) {
				e.printStackTrace();
			}
			return result;
		}

		/**
		 * on getting result
		 */
		@Override
		protected void onPostExecute(String result) {
			try {
				Object json = new JSONTokener(result).nextValue();
				if (json instanceof JSONObject) {
					if (!((JSONObject) json).optString("status").equals("0")) {
						Log.e(this.getClass().getSimpleName(), ""+((JSONObject) json).optString("message"));
						return;
					}
					JSONArray insightList = ((JSONObject) json).optJSONArray("insight_list");
					InsightListAdapter adapter = DataHolder.getListAdapter();
					adapter.clear();

					for (int i=0; i<insightList.length(); ++i) {
						adapter.add(insightList.getJSONArray(i).getString(0));
					}
				} else {
					Helper.toastIt("There was a server error try again later");
				}
			} catch (Exception e) {
				Log.e(Helper.getTag(this), Helper.getExceptionString(e));
			}

		}
	}
}
