package com.fmakdemir.insight;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

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
        imageView = (ImageView)findViewById(R.id.imageView);
        ListView listView = (ListView)findViewById(R.id.list_events);
        mAdapter = new EventListAdapter(this);
        listView.setAdapter(mAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                showEvent((EventListResponse.Event)mAdapter.getItem(position));
            }
        });

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
                        }
                    }

                    @Override
                    public void onError(String cause) {

                    }
                });
        
    }

    @Override
    public void onBackPressed() {
        if (layoutOverlay.getVisibility() != View.GONE)
            layoutOverlay.setVisibility(View.GONE);
        else super.onBackPressed();
    }

}
