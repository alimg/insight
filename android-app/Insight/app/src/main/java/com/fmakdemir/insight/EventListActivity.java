package com.fmakdemir.insight;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ListView;

import com.fmakdemir.insight.adapters.EventListAdapter;
import com.fmakdemir.insight.webservice.LoginService;
import com.fmakdemir.insight.webservice.WebApiConstants;
import com.fmakdemir.insight.webservice.model.EventListResponse;
import com.fmakdemir.insight.webservice.request.MediaWebApiHandler;
import com.fmakdemir.insight.webservice.request.WebApiCallback;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_events);

        loginService = LoginService.getInstance(this);
        if (!loginService.isLoggedin()) {
            finish();
            return;
        }
        
        ListView listView = (ListView)findViewById(R.id.list_events);
        mAdapter = new EventListAdapter(this);
        listView.setAdapter(mAdapter);

        MediaWebApiHandler.listEvents(loginService.getSessionToken(), mListener);
    }

}
