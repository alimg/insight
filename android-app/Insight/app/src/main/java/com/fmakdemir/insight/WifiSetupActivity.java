package com.fmakdemir.insight;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;

import com.beardedhen.androidbootstrap.BootstrapEditText;
import com.fmakdemir.insight.ZXingQRGenerator.Contents;
import com.fmakdemir.insight.ZXingQRGenerator.QRCodeEncoder;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;

public class WifiSetupActivity extends Activity {

	BootstrapEditText editWifiName;

	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi_setup);

		editWifiName = (BootstrapEditText) findViewById(R.id.edit_wifi_name);

		WifiManager wifiMgr = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
		WifiInfo wifiInfo = wifiMgr.getConnectionInfo();
		if (wifiMgr.isWifiEnabled()) {
			editWifiName.setText(wifiInfo.getSSID());
		}

	}

	public void setupQR(View v) {
		//Encode with a QR Code image
		BootstrapEditText editWifiPass = (BootstrapEditText) findViewById(R.id.edit_wifi_pass);

		String qrStr = editWifiName.getText().toString()+"\n"+editWifiPass.getText().toString();

		QRCodeEncoder qrCodeEncoder = new QRCodeEncoder(qrStr,
				null,
				Contents.Type.TEXT,
				BarcodeFormat.QR_CODE.toString(),
				512);
		try {
			Bitmap bitmap = qrCodeEncoder.encodeAsBitmap();
			ImageView myImage = (ImageView) findViewById(R.id.wifi_qr_image);
			myImage.setImageBitmap(bitmap);

			InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);

		} catch (WriterException e) {
			e.printStackTrace();
		}
	}

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.wifi_setup, menu);
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
