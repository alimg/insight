package com.fmakdemir.insight;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

import com.fmakdemir.insight.ZXingQRGenerator.Contents;
import com.fmakdemir.insight.ZXingQRGenerator.QRCodeEncoder;
import com.fmakdemir.insight.utils.Helper;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;

import java.io.IOException;


public class ImageTestActivity extends Activity {

	public static final String EXT_MAKE_QR = "ImageTestActivity_ExtMakeQR";
	public static final String EXT_QR_STR = "ImageTestActivity_ExtQRStr";

	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_test);

		try {
			if (getIntent().getBooleanExtra(EXT_MAKE_QR, false)) {
				//Encode with a QR Code image
				String qrStr = getIntent().getStringExtra(EXT_QR_STR);
				if (qrStr == null || qrStr.equals("")) {
					qrStr = "test qr string";
				}
				QRCodeEncoder qrCodeEncoder = new QRCodeEncoder(qrStr,
						null,
						Contents.Type.TEXT,
						BarcodeFormat.QR_CODE.toString(),
						512);
				try {
					Bitmap bitmap = qrCodeEncoder.encodeAsBitmap();
					ImageView myImage = (ImageView) findViewById(R.id.img_view);
					myImage.setImageBitmap(bitmap);

				} catch (WriterException e) {
					e.printStackTrace();
				}
			} else {
				((ImageView) findViewById(R.id.img_view)).setImageDrawable(Helper.retrieveImage("testimg.png"));
			}
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
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
