package com.fmakdemir.insight;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.method.CharacterPickerDialog;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.VideoView;

import com.fmakdemir.insight.adapters.EventListAdapter;
import com.fmakdemir.insight.adapters.model.EventFilter;
import com.fmakdemir.insight.utils.Helper;
import com.fmakdemir.insight.webservice.LoginService;
import com.fmakdemir.insight.webservice.WebApiConstants;
import com.fmakdemir.insight.webservice.model.BaseResponse;
import com.fmakdemir.insight.webservice.model.EventListResponse;
import com.fmakdemir.insight.webservice.request.MediaWebApiHandler;
import com.fmakdemir.insight.webservice.request.WebApiCallback;
import com.google.gson.Gson;

import java.io.File;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.util.Comparator;

public class EventListActivity extends Activity {

	public static final String EXT_OPEN_LATEST = "EventListActivity.openLatest";
    private EventListAdapter mAdapter;
    private LoginService loginService;

	ArrayAdapter<String> eventTypeAdapter;
	ArrayAdapter<String> deviceAdapter;
	ArrayAdapter<String> priorityAdapter;

	private WebApiCallback<EventListResponse> mListener = new WebApiCallback<EventListResponse>() {
        @Override
        public void onSuccess(EventListResponse data) {
            if(data.status.equals(WebApiConstants.STATUS_SUCCESS)) {
                mAdapter.setList(data.events);

                eventTypeAdapter = new ArrayAdapter<>(EventListActivity.this, android.R.layout.simple_spinner_item);
//				eventTypeAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
                eventTypeAdapter.add("All");

                deviceAdapter = new ArrayAdapter<>(EventListActivity.this, android.R.layout.simple_spinner_item);
//				deviceAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
                deviceAdapter.add("All");

                priorityAdapter= new ArrayAdapter<>(EventListActivity.this, android.R.layout.simple_spinner_item);
//				priorityAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
				priorityAdapter.add("All");

                for (EventListResponse.Event event: data.events) {
                    if (deviceAdapter.getPosition(event.deviceid) == -1) {
                        deviceAdapter.add(event.deviceid);
                        Log.d("deviceAdapter", event.deviceid);
                    }
                    if (priorityAdapter.getPosition(event.priority.toString()) == -1) {
                        priorityAdapter.add(event.priority.toString());
                        Log.d("priorityAdapter", event.priority.toString());
                    }
                    if (eventTypeAdapter.getPosition(event.type) == -1) {
                        eventTypeAdapter.add(event.type);
					}
				}
				priorityAdapter.sort(new Comparator<String>() {
					@Override
					public int compare(String lhs, String rhs) {
						if (lhs.equals("All")) {
							return -1;
						} else if (rhs.equals("All")) {
							return 1;
						}
						return Integer.parseInt(lhs)-Integer.parseInt(rhs);
					}
				});

				eventTypeSpinner.setAdapter(eventTypeAdapter);
                deviceSpinner.setAdapter(deviceAdapter);
                prioritySpinner.setAdapter(priorityAdapter);

                if (getIntent().getBooleanExtra(EXT_OPEN_LATEST, false)) {
					showLatestEvent();
				}
            }
        }

        @Override
        public void onError(String cause) {

        }
    };
    private View layoutOverlay;
    private ImageView imageView;
    private MediaPlayer mMediaPlayer;
    private VideoView videoView;
    private Spinner deviceSpinner, prioritySpinner, eventTypeSpinner;
	private String selectedEventType = "All";
    String selectedDeviceId = "All";
    private String selectedPriority = "All";

	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_events);
        
        loginService = LoginService.getInstance(this);
        if (!loginService.isLoggedin()) {
			Helper.toastIt("Not logged in", Toast.LENGTH_LONG);
            finish();
            return;
        }
        layoutOverlay = findViewById(R.id.layout_overlay);
        layoutOverlay.setVisibility(View.GONE);
        layoutOverlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        imageView = (ImageView)findViewById(R.id.imageView);
        videoView = (VideoView)findViewById(R.id.videoView);
        ListView listView = (ListView)findViewById(R.id.list_events);
        mAdapter = new EventListAdapter(this);
        listView.setAdapter(mAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				showEvent((EventListResponse.Event) mAdapter.getItem(position));
			}
		});

        eventTypeSpinner = (Spinner) findViewById(R.id.spinner_event_type);
        deviceSpinner = (Spinner) findViewById(R.id.spinner_device);
        prioritySpinner = (Spinner) findViewById(R.id.spinner_priority);

        eventTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedEventType = eventTypeAdapter.getItem(position);
                mAdapter.getFilter().filter(getFilterConstraint());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedEventType = "All";
            }
        });

        deviceSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedDeviceId = deviceAdapter.getItem(position);
                mAdapter.getFilter().filter(getFilterConstraint());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedDeviceId = "All";
            }
        });

		prioritySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				selectedPriority = priorityAdapter.getItem(position);
				mAdapter.getFilter().filter(getFilterConstraint());
			}
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedPriority = "All";
            }
        });

		mMediaPlayer = new MediaPlayer();
        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        videoView.setVisibility(View.GONE);

        MediaWebApiHandler.listEvents(loginService.getSessionToken(), mListener);
    }

	private String getFilterConstraint() {
		EventFilter filter = new EventFilter();
		filter.type = selectedEventType;
		filter.priority = "-1";
		if (!selectedPriority.equals("All")) {
			filter.priority = selectedPriority;
		}
		filter.deviceid = selectedDeviceId;
		return new Gson().toJson(filter);
	}

	public void showLatestEvent() {
/*		String id = DataHolder.getLatestEventId();
		if (id == null) {
			return;
		}
		EventListResponse.Event event = (EventListResponse.Event) mAdapter.getItemById(id);
		showEvent(event);*/
		if (mAdapter.getCount() > 0) {
			showEvent((EventListResponse.Event) mAdapter.getItem(0));
		}
	}

    private void showEvent(final EventListResponse.Event event) {
        layoutOverlay.setVisibility(View.VISIBLE);
        File parentDir = new File(Environment.getExternalStorageDirectory().toString()+"/Insight/");
        parentDir.mkdirs();
        final File file = new File(Environment.getExternalStorageDirectory().toString()+"/Insight/"+event.filename);
        if (file.exists()) {
            showEventContent(event, file);
        } else {
            MediaWebApiHandler.downloadEventData(loginService.getSessionToken(), event.id, file,
                    new WebApiCallback<BaseResponse>() {
                        @Override
                        public void onSuccess(BaseResponse data) {
                            try {
                                File out = EncryptionUtil.decryptFile(event, file);
                                showEventContent(event, out);
                            } catch (IOException | InvalidKeyException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onError(String cause) {

                        }
                    });
        }
    }

    private void showEventContent(EventListResponse.Event event, File file) {
        switch (event.type) {
            case "jpeg":
                Bitmap img = BitmapFactory.decodeFile(file.getAbsolutePath());
                if (img != null) {
                    imageView.setImageBitmap(img);
                }
                break;
            case "ogg":
                mMediaPlayer.reset();
                try {
                    mMediaPlayer.setDataSource(file.getPath());
                    mMediaPlayer.prepare();
                    mMediaPlayer.start();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                imageView.setImageResource(R.drawable.ic_audio);
                break;
            case "h264":
                //videoView.setVideoPath(file.getPath());
                //videoView.start();
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setPackage("org.videolan.vlc.betav7neon");
                i.setDataAndType(Uri.fromFile(file), "video/h264");
                startActivity(i);
                break;
        }
    }

    @Override
    public void onBackPressed() {
        if (layoutOverlay.getVisibility() != View.GONE) {
            layoutOverlay.setVisibility(View.GONE);
            if (mMediaPlayer.isPlaying()) {
                mMediaPlayer.stop();
            }
        }
        else super.onBackPressed();
    }

}
