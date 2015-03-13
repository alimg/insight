package com.fmakdemir.insight.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.fmakdemir.insight.R;
import com.fmakdemir.insight.webservice.model.EventListResponse;

import java.util.ArrayList;
import java.util.List;

public class EventListAdapter extends BaseAdapter{
    private final Context mContext;
    private List<EventListResponse.Event> list = new ArrayList<>();

    public EventListAdapter(Context context) {
        mContext = context;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

/*	public Object getItemById(String id) {
		for (EventListResponse.Event ev: list) {
			if (ev.id.equals(id)) {
				return ev;
			}
		}
		return null;
	}*/

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.list_item_event, null);
        }
        EventListResponse.Event event = list.get(position);
        ((TextView)convertView.findViewById(R.id.text_date)).setText("Date: "+event.date);
        ((TextView)convertView.findViewById(R.id.text_device)).setText("Device: "+event.deviceid);
        ((TextView)convertView.findViewById(R.id.text_type)).setText("File name: "+event.filename);
            
        if (event.type.equals("ogg"))
            ((ImageView)convertView.findViewById(R.id.thumbnail)).setImageResource(R.drawable.ic_audio);
        else if (event.type.equals("h264"))
            ((ImageView)convertView.findViewById(R.id.thumbnail)).setImageResource(R.drawable.ic_video);
        else ((ImageView)convertView.findViewById(R.id.thumbnail)).setImageResource(R.drawable.ic_image);
        return convertView;
    }

    public void setList(List<EventListResponse.Event> list) {
        this.list = list;
        notifyDataSetInvalidated();
    }
}
