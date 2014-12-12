package com.fmakdemir.insight;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.fmakdemir.insight.adapters.InsightListAdapter;
import com.fmakdemir.insight.utils.DataHolder;
import com.fmakdemir.insight.webservice.LoginService;


public class HomeActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

		ListView listView = (ListView) findViewById(R.id.list_view_insight);
		final InsightListAdapter listAdapter = DataHolder.getListAdapter();
		listView.setAdapter(listAdapter);
		listAdapter.add("Test Insight");
		listAdapter.notifyDataSetChanged();

		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, final View view,
									int position, long id) {

				Intent intent = new Intent(HomeActivity.this, MainActivity.class);
				final InsightListAdapter listAdapter = DataHolder.getListAdapter();
				Log.d("XXX", ""+listAdapter.getCount());
				Toast.makeText(HomeActivity.this.getApplicationContext(), "ID: "+listAdapter.getItem(position), Toast.LENGTH_LONG).show();
				intent.putExtra("InsightId", listAdapter.getItem(position));
				startActivity(intent);
//				overridePendingTransition (R.anim.open_next, R.anim.close_main);
			}

		});
    }

	public void btnClicked(View v) {
		switch (v.getId()) {
			case R.id.btn_logout:
				LoginService.getInstance(this).logout();
				finish();
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
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
