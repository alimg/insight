package com.fmakdemir.insight;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
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
import com.fmakdemir.insight.utils.DataHolder;
import com.fmakdemir.insight.utils.Helper;
import com.fmakdemir.insight.utils.MediaStorageHelper;
import com.fmakdemir.insight.webservice.LoginService;
import com.fmakdemir.insight.webservice.WebApiConstants;
import com.fmakdemir.insight.webservice.model.DeviceListResponse;
import com.fmakdemir.insight.webservice.request.DeviceWebApiHandler;
import com.fmakdemir.insight.webservice.request.WebApiCallback;
import com.parse.Parse;

import java.io.File;
import java.util.ArrayList;


public class HomeActivity extends Activity {

    private LoginService loginService;
    private InsightListAdapter adapter;
    private WebApiCallback<DeviceListResponse> mDevicesListener =  new WebApiCallback<DeviceListResponse>() {
        @Override
        public void onSuccess(DeviceListResponse data) {
            if (data.status.equals(WebApiConstants.STATUS_SUCCESS)) {
                adapter.clear();
                for (String device: data.devices)
                    adapter.add(device);
            } else {
                Helper.toastIt("Couldn't fetch InSightList!", Toast.LENGTH_LONG);
            }

        }

        @Override
        public void onError(String cause) {
            Helper.toastIt("Couldn't fetch InSightList!", Toast.LENGTH_LONG);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        loginService = LoginService.getInstance(this);
        if (!loginService.isLoggedin()) {
            finish();
            return;
        }

		Parse.initialize(this, "HIWZgDpELVc7HanpltUv1EtSPGF5eBGJBj6QGrVS", "pjmqIrf4drl5g1JLkftZ9ZOpxUyRTe8H8HbmECA9");

		Helper.setContext(getApplicationContext());
		MediaStorageHelper.init(getApplicationContext());

		String mDeviceID = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);
		Log.i(getClass().getSimpleName(), "Device ID: "+mDeviceID);
		Intent mMQTTServiceIntent = new Intent(this, InsightMQTTService.class);
		mMQTTServiceIntent.setData(Uri.parse(mDeviceID));
		// Starts the IntentService
//		startService(mMQTTServiceIntent);

		ListView listView = (ListView) findViewById(R.id.list_view_insight);

		for(File f:getApplicationContext().getFilesDir().listFiles() ) {
			boolean delete = f.delete();
			if (delete) {
				Log.d("Delete", "success");
			}
		}

		ArrayList<String> strList = new ArrayList<>();
		adapter = new InsightListAdapter(getApplicationContext(), strList);
		DataHolder.setListAdapter(adapter);
		final InsightListAdapter listAdapter = DataHolder.getListAdapter();
		listView.setAdapter(listAdapter);

		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, final View view,
									int position, long id) {

				String insightIid = listAdapter.getItem(position);

				Intent intent = new Intent(HomeActivity.this, DeviceActivity.class);
				intent.putExtra(DeviceActivity.EXT_INSIGHT_IID, insightIid);

				Toast.makeText(HomeActivity.this.getApplicationContext(), "ID: "+insightIid, Toast.LENGTH_LONG).show();
				intent.putExtra("InsightId", listAdapter.getItem(position));
				startActivity(intent);
				overridePendingTransition (R.anim.open_next, R.anim.close_main);
			}

		});


		DeviceWebApiHandler.listInsight(loginService.getSessionToken(), mDevicesListener);
    }

	public void btnClicked(View v) {
        Intent intent;
		switch (v.getId()) {
            case R.id.btn_event_list:
                intent = new Intent(HomeActivity.this, EventListActivity.class);
                startActivity(intent);
                break;
			case R.id.btn_add_new:
				intent = new Intent(HomeActivity.this, RegisterInsightActivity.class);
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
				Intent intent = new Intent(HomeActivity.this, SettingsActivity.class);
				startActivity(intent);
//				Toast.makeText(getApplicationContext(), "Action Settings", Toast.LENGTH_SHORT).show();
				break;
			case R.id.action_logout:
				LoginService.getInstance(this).clearSession();
				finish();
				break;
		}
        return super.onOptionsItemSelected(item);
    }
}
