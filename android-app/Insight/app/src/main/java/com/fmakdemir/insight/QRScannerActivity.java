package com.fmakdemir.insight;

/**
 * @author fma
 * @date 10.11.2014.
 */
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import java.util.Arrays;

import me.dm7.barcodescanner.zbar.BarcodeFormat;
import me.dm7.barcodescanner.zbar.Result;
import me.dm7.barcodescanner.zbar.ZBarScannerView;

public class QRScannerActivity extends Activity implements ZBarScannerView.ResultHandler {
	public final static String EXT_QR_RESULT = "QRScanner_qr_result_nar_id";
	private ZBarScannerView mScannerView;

	@Override
	public void onCreate(Bundle state) {
		super.onCreate(state);
		mScannerView = new ZBarScannerView(this);

		// focus just on QR codes
		mScannerView.setFormats( Arrays.asList(BarcodeFormat.QRCODE) );

		setContentView(mScannerView);
	}

	@Override
	public void onResume() {
		super.onResume();
		mScannerView.setResultHandler(this);
		mScannerView.startCamera();
	}

	@Override
	public void onPause() {
		super.onPause();
		mScannerView.stopCamera();
	}

	@Override
	public void handleResult(Result rawResult) {
		// ok we got the qr result now make a toast for it!
		Toast.makeText(this, "Contents = " + rawResult.getContents(), Toast.LENGTH_SHORT).show();
		// parse result

		// return result by intent
		Intent resultIntent = new Intent();
		resultIntent.putExtra(EXT_QR_RESULT, rawResult.getContents());
		setResult(Activity.RESULT_OK, resultIntent);
		finish();
	}
}
