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
import com.fmakdemir.insight.webservice.LoginService;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends Activity {
	private BootstrapButton btnGetImg, btnShowImg;
	private BootstrapEditText edtQRString;
	private AsyncImageGetter asyncImageGetter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

		DataHolder.setContext(getApplicationContext());

		btnGetImg = (BootstrapButton) findViewById(R.id.btn_get_img);
		btnShowImg = (BootstrapButton) findViewById(R.id.btn_show_img);
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
				asyncImageGetter = new AsyncImageGetter("testuser", "testpass");
				asyncImageGetter.execute();
				break;
			case R.id.btn_show_img:
				startActivity(new Intent(this, ImageTestActivity.class));
				break;
			case R.id.btn_scan_qr:
				intent = new Intent(this, QRScannerActivity.class);
				startActivityForResult(intent, DataHolder.REQ_INSIGHT_SCAN_QR);
				break;
			case R.id.btn_make_qr:
				intent = new Intent(this, ImageTestActivity.class);
				intent.putExtra(ImageTestActivity.EXT_MAKE_QR, true);
				intent.putExtra(ImageTestActivity.EXT_QR_STR, edtQRString.getText().toString());
				startActivity(intent);
				break;
            case R.id.btn_logout:
                LoginService.getInstance(this).logout();
                finish();
                break;
		}
	}

	// get result from qr scan and generate a new Nar object
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		if (requestCode == DataHolder.REQ_INSIGHT_SCAN_QR) {
			if (resultCode == Activity.RESULT_OK) {
				String result = intent.getStringExtra(QRScannerActivity.EXT_QR_RESULT);
				Log.d("Test", "result=" + result);

			} else {
				ToastIt("QR result was not ok: " + resultCode, Toast.LENGTH_LONG);
			}
		}
	}

	public class AsyncImageGetter extends AsyncTask<Void, Void, String> {
		private ArrayList<NameValuePair> mData = new ArrayList<NameValuePair>();

		/**
		 * constructor
		 */
		public AsyncImageGetter(String username, String password) {
			// add data to post data

			mData.add(new BasicNameValuePair("username", username));
			mData.add(new BasicNameValuePair("password", password));
		}

		/**
		 * background
		 */
		@Override
		protected String doInBackground(Void... voids) {
//			byte[] result;
			String errMsg;
//			HttpClient client = DataHolder.getHttpClient();
//			HttpPost post = new HttpPost(DataHolder.getServerUrl()+"/robber.png");
//			HttpPost post = new HttpPost("http://fmakdemir.com/sketches/robber.png");

			try {
				InputStream imgStream = new URL(DataHolder.getServerUrl()+"/robber.png").openConnection().getInputStream();
//				Drawable d = Drawable.createFromStream(imgStream, "robber.png");
//				Bitmap bm = BitmapFactory.decodeStream(imgStream);
//				DataHolder.setBM(bm);
//				Log.w("XX", ""+bm+"\n");
//				bm.compress(Bitmap.CompressFormat.PNG, 100, new FileOutputStream(DataHolder.getDir() + "/asd.png"));

				// set up post data
//				post.setEntity(new UrlEncodedFormEntity(mData, "UTF-8"));

//				HttpResponse response = client.execute(post);
//				StatusLine statusLine = response.getStatusLine();
//				if(statusLine.getStatusCode() == HttpURLConnection.HTTP_OK) {
					Helper.storeImage(imgStream, "testimg.png");
//					result = EntityUtils.toByteArray(response.getEntity());
//					str = new String(result, "UTF-8");
					errMsg = "";
//				} else {
//					errMsg = "Error: "+statusLine.getStatusCode();
//				}
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
/*			if (result.equals("logged_out")) {
				android.os.Process.killProcess(android.os.Process.myPid());
				System.exit(0);
			}*/

			btnGetImg.setEnabled(true);

			if (errMsg.equals("")) {
				MainActivity.this.startActivity(new Intent(MainActivity.this, ImageTestActivity.class));
				ToastIt("Got image!\n" + errMsg);
			} else {
				Log.e(this.getClass().getSimpleName(), errMsg);
			}

/*			JSONObject json;
//			Log.i(TAG+"_res", result);
			try {
				json = new JSONObject(result);
				String err = json.optString("error", null);
//				Log.e(TAG+"_err", ""+err);
				if (err == null) {
					String narId = json.optString("nar_id", null);
					String lastalive = json.optString("lastalive", null);
					if (narId == null || lastalive == null) {
						return;
					}
//					Log.i(Helper.getTag(this), "Registered nar: " + narId + "|" + lastalive);

					ToastIt("Got image");

					Intent resultIntent = new Intent();
					resultIntent.putExtra("TEST", );
					setResult(Activity.RESULT_OK, resultIntent);
//					LoginActivity.this.finish();
					overridePendingTransition(R.anim.open_main, R.anim.close_next);

				} else {
					ToastIt(err);
				}
			} catch (Exception e) {
				Helper.getExceptionString(e);
			}*/

		}
	}
}
