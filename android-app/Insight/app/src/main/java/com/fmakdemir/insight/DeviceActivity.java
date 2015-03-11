package com.fmakdemir.insight;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.beardedhen.androidbootstrap.BootstrapButton;
import com.beardedhen.androidbootstrap.BootstrapEditText;
import com.fmakdemir.insight.utils.Helper;
import com.fmakdemir.insight.webservice.LoginService;
import com.fmakdemir.insight.webservice.WebApiConstants;
import com.fmakdemir.insight.webservice.model.BaseResponse;
import com.fmakdemir.insight.webservice.model.DeviceStatusResult;
import com.fmakdemir.insight.webservice.request.DeviceWebApiHandler;
import com.fmakdemir.insight.webservice.request.WebApiCallback;

public class DeviceActivity extends Activity {
	public static final String EXT_INSIGHT_IID = "MainAct.ext_insight_iid";

	private BootstrapButton btnTakeImg, btnTakeSnd;

	String insightIid;
    private LoginService loginService;
    private TextView textStatus;
    private BootstrapButton btnTakeVideo;
    private WebApiCallback<BaseResponse> onCommandCallback = new WebApiCallback<BaseResponse>() {
        @Override
        public void onSuccess(BaseResponse data) {
            if (data.status.equals(WebApiConstants.STATUS_SUCCESS)) {
                toastIt("Command sent.");
            }  else if (data.status.equals(WebApiConstants.STATUS_DEVICE_OFFLINE)) {
                toastIt("Device is not online.");
            } else {
                toastIt("Error sending command.");
            }

            btnTakeImg.setEnabled(true);
            btnTakeSnd.setEnabled(true);
            btnTakeVideo.setEnabled(true);
        }

        @Override
        public void onError(String cause) {
            btnTakeImg.setEnabled(true);
            btnTakeSnd.setEnabled(true);
            btnTakeVideo.setEnabled(true);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        loginService = LoginService.getInstance(this);
        if (!loginService.isLoggedin()) {
            finish();
            return;
        }
        
		Helper.setContext(getApplicationContext());

		insightIid = getIntent().getStringExtra(DeviceActivity.EXT_INSIGHT_IID);
		((BootstrapEditText) findViewById(R.id.edit_title_insight)).setText(insightIid);

		btnTakeImg = (BootstrapButton) findViewById(R.id.btn_take_img);
		btnTakeSnd = (BootstrapButton) findViewById(R.id.btn_take_snd);
		btnTakeVideo = (BootstrapButton) findViewById(R.id.btn_take_video);

        textStatus = (TextView) findViewById(R.id.text_status);

        DeviceWebApiHandler.getDeviceStatus(loginService.getSessionToken(), insightIid, new WebApiCallback<DeviceStatusResult>() {
            @Override
            public void onSuccess(DeviceStatusResult data) {
                if (data.status.equals(WebApiConstants.STATUS_SUCCESS)) {
                    textStatus.setText("Last Response: "+data.lastResponse+"\nAddress :"+data.address);
                }
            }

            @Override
            public void onError(String cause) {

            }
        });
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

	void toastIt(String str) {
		toastIt(str, Toast.LENGTH_SHORT);
	}

	void toastIt(String str, int duration) {
		Toast.makeText(getApplicationContext(), str, duration).show();
	}

	public void onBtnClick(View v) {
		String username = Helper.getUsername();
		switch (v.getId()) {
			case R.id.btn_take_img:
				btnTakeImg.setEnabled(false);
				DeviceWebApiHandler.sendCommand(loginService.getSessionToken(), insightIid, "photo",
                        onCommandCallback);
				break;
			case R.id.btn_list_photos:
				startActivity(new Intent(this, PhotoListActivity.class));
				overridePendingTransition(R.anim.open_next, R.anim.close_main);
				break;
			case R.id.btn_take_snd:
				btnTakeSnd.setEnabled(false);
                DeviceWebApiHandler.sendCommand(loginService.getSessionToken(), insightIid, "audio",
                        onCommandCallback);
				break;
			case R.id.btn_list_snd:
                startActivity(new Intent(this, WebViewActivity.class));
				overridePendingTransition(R.anim.open_next, R.anim.close_main);
				break;
            case R.id.btn_take_video:
                btnTakeVideo.setEnabled(false);
                DeviceWebApiHandler.sendCommand(loginService.getSessionToken(), insightIid, "video",
                        onCommandCallback);
                break;
			case R.id.btn_wifi_setup:
				startActivity(new Intent(this, WifiSetupActivity.class));
				break;
            case R.id.btn_mute:
                DeviceWebApiHandler.setDevicesEnabled(loginService.getSessionToken(), insightIid,
                        false, onCommandCallback);
                break;
            case R.id.btn_unmute:
                DeviceWebApiHandler.setDevicesEnabled(loginService.getSessionToken(), insightIid,
                        true, onCommandCallback);
                break;

		}
	}
}
