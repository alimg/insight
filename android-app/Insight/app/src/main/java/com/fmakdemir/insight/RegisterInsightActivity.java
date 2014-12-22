package com.fmakdemir.insight;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.beardedhen.androidbootstrap.BootstrapEditText;
import com.fmakdemir.insight.utils.DataHolder;
import com.fmakdemir.insight.utils.Helper;
import com.fmakdemir.insight.webservice.WebApiConstants;
import com.fmakdemir.insight.webservice.model.BaseResponse;
import com.fmakdemir.insight.webservice.request.DeviceWebApiHandler;
import com.fmakdemir.insight.webservice.request.WebApiCallback;


public class RegisterInsightActivity extends Activity {

	private BootstrapEditText editInsightId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_insight);

		editInsightId = ((BootstrapEditText)findViewById(R.id.edit_insight_id));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.register_insight, menu);
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

	public void onRegInsightBtnClicked(View v) {
		Intent intent;
		switch (v.getId()) {
			case R.id.btn_scan_qr:
				intent = new Intent(this, QRScannerActivity.class);
				startActivityForResult(intent, DataHolder.REQ_INSIGHT_SCAN_QR);
				break;
			case R.id.btn_register_insight:
				intent = new Intent();
				String insightId = editInsightId.getText().toString();
				if (insightId != null && !insightId.isEmpty()) {
					editInsightId.setText(insightId);
					intent.putExtra(QRScannerActivity.EXT_QR_RESULT, "id=" + insightId);
					onActivityResult(DataHolder.REQ_INSIGHT_SCAN_QR, Activity.RESULT_OK, intent);
				}
				break;
		}

	}

	// get result from qr scan and generate a new Nar object
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		if (requestCode == DataHolder.REQ_INSIGHT_SCAN_QR) {
			if (resultCode == Activity.RESULT_OK) {
				String result = intent.getStringExtra(QRScannerActivity.EXT_QR_RESULT);
				Log.d("Test", "result=" + result);
				String strAr[] = result.split("=");
				if (strAr[0].equals("id")) {
					final String insightId = strAr[1];
					final String username = Helper.getUsername();
					DeviceWebApiHandler.registerInsight(username, insightId, new WebApiCallback<BaseResponse>() {
						@Override
						public void onSuccess(BaseResponse data) {
							if(data.status.equals(WebApiConstants.STATUS_SUCCESS)) {
								Toast.makeText(getApplicationContext(), "Registration successful.", Toast.LENGTH_SHORT).show();
								// add insight to list and return to home
								DataHolder.getListAdapter().add(insightId);
								RegisterInsightActivity.this.finish();
							} else {
								Toast.makeText(getApplicationContext(), "Registration failed.", Toast.LENGTH_SHORT).show();
							}
						}

						@Override
						public void onError(String cause) {
							Toast.makeText(getApplicationContext(), "Registration failed.", Toast.LENGTH_SHORT).show();
						}

					});
				} else {
					Helper.toastIt("QR not well formed!", Toast.LENGTH_LONG);
				}

			} else {
				Helper.toastIt("QR result was not ok: " + resultCode, Toast.LENGTH_LONG);
			}
		}
	}
}
