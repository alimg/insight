package com.fmakdemir.insight.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Filter;

import com.fmakdemir.insight.R;
import com.fmakdemir.insight.adapters.model.EventFilter;
import com.fmakdemir.insight.webservice.model.EventListResponse;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class EventListAdapter extends BaseAdapter implements Filterable {
    private final Context mContext;
    private List<EventListResponse.Event> list = new ArrayList<>();
    private List<EventListResponse.Event> fullList = new ArrayList<>();
    private ItemFilter mFilter = new ItemFilter();

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

		switch (event.type) {
			case "ogg":
				((ImageView) convertView.findViewById(R.id.thumbnail)).setImageResource(R.drawable.ic_audio);
				break;
			case "h264":
				((ImageView) convertView.findViewById(R.id.thumbnail)).setImageResource(R.drawable.ic_video);
				break;
			case "jpeg":
				((ImageView) convertView.findViewById(R.id.thumbnail)).setImageResource(R.drawable.ic_image);
				break;
		}
        return convertView;
    }

    public void setList(List<EventListResponse.Event> list) {
        this.list = list;
		this.fullList = list;
        notifyDataSetInvalidated();
    }

    @Override
    public Filter getFilter() {
        return mFilter;
    }

    private class ItemFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            Gson gson = new Gson();
            EventFilter filter = gson.fromJson(constraint.toString(), EventFilter.class);

            FilterResults results = new FilterResults();

            int count = fullList.size();
            final ArrayList<EventListResponse.Event> nlist = new ArrayList<>(fullList);

            EventListResponse.Event event;

            // remove not matching types
            for (int i = 0; i < count; i++) {
                event = fullList.get(i);
                if ((!filter.type.equals("All") && !filter.type.equals(event.type))
                    || (!filter.deviceid.equals("All") && !filter.deviceid.equals(event.deviceid)
                    || Integer.parseInt(filter.priority) > event.priority)) {
                    nlist.remove(event);
                }
            }

            results.values = nlist;
            results.count = nlist.size();

            return results;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint, android.widget.Filter.FilterResults results) {
            list = (ArrayList<EventListResponse.Event>) results.values;
            notifyDataSetChanged();
        }

    }
}
