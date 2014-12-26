package com.fmakdemir.insight;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.fmakdemir.insight.adapters.InsightListAdapter;
import com.fmakdemir.insight.utils.Helper;
import com.fmakdemir.insight.utils.MediaStorageHelper;

import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.InputStream;


public class SoundListActivity extends Activity {

	static InsightListAdapter listAdapter;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sound_list);
		ListView listView = (ListView) findViewById(R.id.sound_list_view);

		listAdapter = new InsightListAdapter(getApplicationContext(), MediaStorageHelper.listSounds());
		listView.setAdapter(listAdapter);

/*		for (String s: this.getFilesDir().list(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String filename) {
				return filename.endsWith(MediaStorageHelper.SOUND_EXT);
			}
		})) {
			listAdapter.add(s);
		}*/

		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, final View view,
									int position, long id) {

				String soundName = listAdapter.getItem(position);

				MediaStorageHelper.playSound(soundName);

				Helper.toastIt("Name: " + soundName);
			}

		});
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_sound_list, menu);
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
