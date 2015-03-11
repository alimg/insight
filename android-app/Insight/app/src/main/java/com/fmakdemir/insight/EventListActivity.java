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
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.VideoView;

import com.fmakdemir.insight.adapters.EventListAdapter;
import com.fmakdemir.insight.webservice.LoginService;
import com.fmakdemir.insight.webservice.WebApiConstants;
import com.fmakdemir.insight.webservice.model.BaseResponse;
import com.fmakdemir.insight.webservice.model.EventListResponse;
import com.fmakdemir.insight.webservice.request.MediaWebApiHandler;
import com.fmakdemir.insight.webservice.request.WebApiCallback;

import java.io.File;

public class EventListActivity extends Activity {

    private EventListAdapter mAdapter;
    private LoginService loginService;
    private WebApiCallback<EventListResponse> mListener = new WebApiCallback<EventListResponse>() {
        @Override
        public void onSuccess(EventListResponse data) {
            if(data.status.equals(WebApiConstants.STATUS_SUCCESS)) {
                mAdapter.setList(data.events);
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_events);

        
        loginService = LoginService.getInstance(this);
        if (!loginService.isLoggedin()) {
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
                showEvent((EventListResponse.Event)mAdapter.getItem(position));
            }
        });

        mMediaPlayer = new MediaPlayer();
        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        videoView.setVisibility(View.GONE);

        MediaWebApiHandler.listEvents(loginService.getSessionToken(), mListener);
    }

    private void showEvent(final EventListResponse.Event event) {
        layoutOverlay.setVisibility(View.VISIBLE);
        File parentDir = new File(Environment.getExternalStorageDirectory().toString()+"/Insight/");
        parentDir.mkdirs();
        final File file = new File(Environment.getExternalStorageDirectory().toString()+"/Insight/"+event.filename);
        MediaWebApiHandler.downloadEventData(loginService.getSessionToken(),event.id, file,
                new WebApiCallback<BaseResponse>() {
                    @Override
                    public void onSuccess(BaseResponse data) {
                        if (event.type.equals("jpeg")) {
                            Bitmap img = BitmapFactory.decodeFile(file.getAbsolutePath());
                            if (img != null) {
                                imageView.setImageBitmap(img);
                            }
                        } else if(event.type.equals("ogg")) {
                            mMediaPlayer.reset();
                            try {
                                mMediaPlayer.setDataSource(file.getPath());
                                mMediaPlayer.prepare();
                                mMediaPlayer.start();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            imageView.setImageResource(R.drawable.ic_audio);
                        } else if(event.type.equals("h264")) {
                            //videoView.setVideoPath(file.getPath());
                            //videoView.start();
                            Intent i = new Intent(Intent.ACTION_VIEW);
                            i.setPackage("org.videolan.vlc.betav7neon");
                            i.setDataAndType(Uri.fromFile(file), "video/h264");
                            startActivity(i);
                        }
                    }

                    @Override
                    public void onError(String cause) {

                    }
                });
        
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
