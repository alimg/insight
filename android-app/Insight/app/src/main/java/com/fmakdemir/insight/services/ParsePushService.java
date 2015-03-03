package com.fmakdemir.insight.services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.fmakdemir.insight.R;
import com.fmakdemir.insight.utils.DataHolder;
import com.fmakdemir.insight.utils.Helper;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;

/*
 * Parse Push client in Android, able to receive
 *  push notifications from Parse.
 *
 */
public class ParsePushService extends Service
{
	// something unique to identify your app - used for stuff like accessing
	//   application preferences
	public static final String APP_ID = "com.fmakdemir.insight";

	// constants used to notify the Activity UI of received messages
	public static final String PARSE_MSG_RECEIVED_INTENT = APP_ID+".MSGRECVD";
	public static final String PARSE_MSG_RECEIVED_TOPIC  = APP_ID+".MSGRECVD_TOPIC";
	public static final String PARSE_MSG_RECEIVED_MSG    = APP_ID+".MSGRECVD_MSGBODY";

	// constants used to tell the Activity UI the connection status
	public static final String PARSE_STATUS_INTENT = APP_ID+".STATUS";
	public static final String PARSE_STATUS_MSG    = APP_ID+".STATUS_MSG";

	// constants for notifications
	final static String NTF_GROUP_PARSE_STATUS = "ntf_group_parse_status";
	final static String NTF_GROUP_PARSE_MESSAGES = "ntf_group_parse_messages";

	// constants used by status bar notifications
	public static final int PUSH_NOTIFICATION_ONGOING = 1;
	public static final int PUSH_NOTIFICATION_UPDATE = 2;

	// notification manager
	private NotificationManager notificationManager;

	/************************************************************************/
    /*    METHODS - core Service lifecycle methods                          */
	/************************************************************************/

	// see http://developer.android.com/guide/topics/fundamentals.html#lcycles

	@Override
	public void onCreate()
	{
		super.onCreate();

		// set contexts for helpers
		Helper.setContext(getApplicationContext());

		// create a binder that will let the Activity UI send
		//   commands to the Service
		mBinder = new LocalBinder<>(this);

		// Gets an instance of the NotificationManager service
		notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

		// get the broker settings out of app preferences
		//   this is not the only way to do this - for example, you could use
		//   the Intent that starts the Service to pass on configuration values
		SharedPreferences settings = getSharedPreferences(APP_ID, MODE_PRIVATE);
		String topicName      = settings.getString("topic",  "/insight/android");
		Log.d("Topic", topicName);

	}


	@Override
	public int onStartCommand(final Intent intent, int flags, final int startId)
	{
		new Thread(new Runnable() {
			@Override
			public void run() {
				handleStart(intent, startId);
			}
		}, Helper.getTag(this)).start();

		// return START_NOT_STICKY - we want this Service to be left running
		//  unless explicitly stopped, and it's process is killed, we want it to
		//  be restarted
		return START_STICKY;
	}

	synchronized void handleStart(Intent intent, int startId)
	{
/*		// before we start - check for a couple of reasons why we should stop
		if (!Helper.isOnline())
		{
			// There is no connection stop
			stopSelf();
			return;
		}*/

		// the Activity UI has started the Parse service - this may be starting
		//  the Service new for the first time, or after the Service has been
		//  running for some time (multiple calls to startService don't start
		//  multiple Services, but it does call this method multiple times)
		// if we have been running already, we re-send any stored data
		rebroadcastReceivedMessages();

	}

	@Override
	public void onDestroy()
	{
		super.onDestroy();

		// inform the app that the app has successfully disconnected
		broadcastServiceStatus("Disconnected");

		if (mBinder != null) {
			mBinder.close();
			mBinder = null;
		}
	}


	/************************************************************************/
    /*    METHODS - broadcasts and notifications                            */
	/************************************************************************/

	// methods used to notify the Activity UI of something that has happened
	//  so that it can be updated to reflect status and the data received
	//  from the server

	private void broadcastServiceStatus(String statusDescription)
	{
		// inform the app (for times when the Activity UI is running /
		//   active) of the current status so that it
		//   can update the UI accordingly
		Intent broadcastIntent = new Intent();
		broadcastIntent.setAction(PARSE_STATUS_INTENT);
		broadcastIntent.putExtra(PARSE_STATUS_MSG, statusDescription);
		sendBroadcast(broadcastIntent);
	}

	private void broadcastReceivedMessage(String topic, String message)
	{
		// pass a message received from the Parse on to the Activity UI
		//   (for times when it is running / active) so that it can be displayed
		//   in the app GUI
		Intent broadcastIntent = new Intent();
		broadcastIntent.setAction(PARSE_MSG_RECEIVED_INTENT);
		broadcastIntent.putExtra(PARSE_MSG_RECEIVED_TOPIC, topic);
		broadcastIntent.putExtra(PARSE_MSG_RECEIVED_MSG,   message);
		sendBroadcast(broadcastIntent);

		DataHolder.addServerMessage(topic, message);
	}

	// methods used to notify the user of what has happened for times when
	//  the app Activity UI isn't running

	private void notifyUser(String title, String text) {
		notifyUser(title, text, false);
	}
	private ArrayList<String> events = new ArrayList<String>();

	private void notifyUser(String title, String text, boolean isParseStatus)
	{
		String ntfGroup;
		int mNotificationId;
		ntfGroup = isParseStatus ? NTF_GROUP_PARSE_STATUS : NTF_GROUP_PARSE_MESSAGES;
		mNotificationId = isParseStatus ? PUSH_NOTIFICATION_ONGOING : PUSH_NOTIFICATION_UPDATE;

		Log.i("Notifier", ".\ntitle: "+title+"\ntext: "+text+"\ngroup: "+ntfGroup);

		Bitmap bm = BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher);

		NotificationCompat.Builder mBuilder =
				new NotificationCompat.Builder(this)
						.setDefaults(Notification.DEFAULT_ALL)
						.setSmallIcon(R.drawable.ic_launcher)
						.setLargeIcon(bm)
						.setContentTitle(title)
						.setContentText(text)
//						.setGroup(ntfGroup)
						.setLights(Color.WHITE, 1000, 1000)
						.setAutoCancel(true)//!isParseStatus)
						.setOngoing(isParseStatus);

		if (!isParseStatus) { // show multi line single notification
			notificationManager.cancel(PUSH_NOTIFICATION_ONGOING);

			// setup inbox-style
			NotificationCompat.InboxStyle inboxStyle =
					new NotificationCompat.InboxStyle();
			// Sets a title for the Inbox style big view
			inboxStyle.setBigContentTitle("Parse Messages");

			// carry to up
			int ind = events.indexOf(text);
			if (ind != -1) {
				events.remove(ind);
			}
			events.add(text);
			// Moves events into the big view
			for (String event : events) {
				inboxStyle.addLine(event);
			}
			// Moves the big view style object into the notification object.
			mBuilder.setStyle(inboxStyle);
			mBuilder.setNumber(events.size());

		}

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
			mBuilder.setPriority(Notification.PRIORITY_HIGH);
		}

		// show notification
		notificationManager.notify(mNotificationId, mBuilder.build());

	}


	/************************************************************************/
    /*    METHODS - binding that allows access from the Actitivy            */
	/************************************************************************/

	// trying to do local binding while minimizing leaks - code thanks to
	//   Geoff Bruckner - which I found at
	//   http://groups.google.com/group/cw-android/browse_thread/thread/d026cfa71e48039b/c3b41c728fedd0e7?show_docid=c3b41c728fedd0e7

	private LocalBinder<ParsePushService> mBinder;

	@Override
	public IBinder onBind(Intent intent)
	{
		return mBinder;
	}

	public class LocalBinder<S> extends Binder
	{
		private WeakReference<S> mService;

		public LocalBinder(S service)
		{
			mService = new WeakReference<S>(service);
		}
		public S getService()
		{
			return mService.get();
		}
		public void close()
		{
			mService = null;
		}
	}

	/*
	 *   callback - called when we receive a message from the server
	 */
	public void publishArrived(String topic, byte[] payloadBytes, boolean retained)
	{
		// we protect against the phone switching off while we're doing this
		//  by requesting a wake lock - we request the minimum possible wake
		//  lock - just enough to keep the CPU running until we've finished
		PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);
		WakeLock wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "Parse");
		wl.acquire();

		String messageBody = new String(payloadBytes);

		//
		//  for times when the app's Activity UI is not running, the Service
		//   will need to safely store the data that it receives
		if (addReceivedMessageToStore(topic, messageBody))
		{
			// this is a new message - a value we haven't seen before

			//
			// inform the app (for times when the Activity UI is running) of the
			//   received message so the app UI can be updated with the new data
			broadcastReceivedMessage(topic, messageBody);

			//
			// inform the user (for times when the Activity UI isn't running)
			//   that there is new data available
			notifyUser("New data received", topic+": "+messageBody);
		}

		// we're finished - if the phone is switched off, it's okay for the CPU
		//  to sleep now
		wl.release();
	}


	//  apps that handle very small amounts of data - e.g. updates and
	//   notifications that don't need to be persisted if the app / phone
	//   is restarted etc. may find it acceptable to store this data in a
	//   variable in the Service
	//  that's what I'm doing in this sample: storing it in a local hashtable
	//  if you are handling larger amounts of data, and/or need the data to
	//   be persisted even if the app and/or phone is restarted, then
	//   you need to store the data somewhere safely
	//  see http://developer.android.com/guide/topics/data/data-storage.html
	//   for your storage options - the best choice depends on your needs

	// stored internally

	private Hashtable<String, String> dataCache = new Hashtable<String, String>();

	private boolean addReceivedMessageToStore(String key, String value)
	{
		String previousValue;

		if (value.length() == 0) {
			previousValue = dataCache.remove(key);
		} else {
			previousValue = dataCache.put(key, value);
		}
		DataHolder.addServerMessage(key, value);

		// is this a new value? or am I receiving something I already knew?
		//  we return true if this is something new
		return true;//((previousValue == null) || (!previousValue.equals(value)));
	}

	// provide a public interface, so Activities that bind to the Service can
	//  request access to previously received messages

	public void rebroadcastReceivedMessages()
	{
		Enumeration<String> e = dataCache.keys();
		while(e.hasMoreElements())
		{
			String nextKey = e.nextElement();
			String nextValue = dataCache.get(nextKey);

			broadcastReceivedMessage(nextKey, nextValue);
		}
	}


}
