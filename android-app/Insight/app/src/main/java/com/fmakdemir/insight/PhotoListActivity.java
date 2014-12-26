package com.fmakdemir.insight;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.fmakdemir.insight.adapters.InsightListAdapter;
import com.fmakdemir.insight.utils.DataHolder;
import com.fmakdemir.insight.utils.Helper;

import java.util.ArrayList;


public class PhotoListActivity extends Activity {

	static InsightListAdapter adapter;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_photo_list);

		ListView listView = (ListView) findViewById(R.id.list_view_insight);

		ArrayList<String> strList = new ArrayList<>();
		adapter = new InsightListAdapter(getApplicationContext(), strList);
		final InsightListAdapter listAdapter = DataHolder.getListAdapter();
		listView.setAdapter(listAdapter);

		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, final View view,
									int position, long id) {

				String photoName = listAdapter.getItem(position);

				Intent intent = new Intent(PhotoListActivity.this, ImageTestActivity.class);
				intent.putExtra(ImageTestActivity.EXT_IMG_NAME, photoName);

				Helper.toastIt("Name: " + photoName);
				startActivity(intent);
				overridePendingTransition(R.anim.open_next, R.anim.close_main);
			}

		});

	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_photo_list, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();

		//noinspection SimplifiableIfStatement
		if (id == R.id.action_settings) {
			return true;
		}

		return super.onOptionsItemSelected(item);
	}
}
