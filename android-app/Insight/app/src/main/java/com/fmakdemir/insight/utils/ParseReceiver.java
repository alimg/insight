package com.fmakdemir.insight.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.fmakdemir.insight.EventListActivity;
import com.parse.ParsePushBroadcastReceiver;

public class ParseReceiver extends ParsePushBroadcastReceiver {
	public ParseReceiver() {
		super();
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		Log.d("Push", "received");
		super.onReceive(context, intent);
	}

	protected void onPushReceive(Context context, Intent intent) {
		Log.i("Push", intent.getExtras().toString());
		super.onPushReceive(context, intent);
	}

	protected void onPushDismiss(Context context, Intent intent) {
		Log.d("Push", "dismissed");
		super.onPushDismiss(context, intent);
	}

	protected void onPushOpen(Context context, Intent intent) {
		Log.d("Push", "opened");
		intent.putExtra(EventListActivity.EXT_OPEN_LATEST, true);
		super.onPushOpen(context, intent);
	}

	protected Class<? extends Activity> getActivity(Context context, Intent intent) {
		return EventListActivity.class;
	}

	protected int getSmallIconId(Context context, Intent intent) {
		return super.getSmallIconId(context, intent);
	}

	protected android.graphics.Bitmap getLargeIcon(Context context, Intent intent) {
		return super.getLargeIcon(context, intent);
	}

	protected android.app.Notification getNotification(Context context, Intent intent) {
		return super.getNotification(context, intent);
	}

}
