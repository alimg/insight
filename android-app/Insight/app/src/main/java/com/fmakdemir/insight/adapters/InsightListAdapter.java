package com.fmakdemir.insight.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.fmakdemir.insight.R;

import java.util.ArrayList;

/**
 * @author fma
 * @date 12.12.2014.
 */
public class InsightListAdapter extends ArrayAdapter<String> {
	private final Context context;

	public InsightListAdapter(Context context) {
		this(context, null);
	}
	public InsightListAdapter(Context context, ArrayList<String> idList) {

		super(context, R.layout.insight_list_item);

		this.context = context;
		if (idList != null) {
			this.addAll(idList);
		}
	}

	private static class StringViewHolder {
		public TextView titleView;
	}

	StringViewHolder StringViewHolder;

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		if (convertView == null) {
			// Get rowView from inflater
			convertView = View.inflate(context, R.layout.insight_list_item, null);

			StringViewHolder = new StringViewHolder();
			StringViewHolder.titleView = (TextView) convertView.findViewById(R.id.insight_item_id);
			convertView.setTag(StringViewHolder);
		} else {
			StringViewHolder = (StringViewHolder) convertView.getTag();
		}

		StringViewHolder.titleView.setText(this.getItem(position));

		// return view
		return convertView;
	}

/*	@Override
	public void add(String String) {
		this.add(String);
		this.notifyDataSetChanged();
	}
*/
/*	public void remove(String item_id) {
		for(String id: ) {
			if (id.equals(item_id)) {
				idList.remove(id);
				this.notifyDataSetChanged();
				return;
			}
		}
	}*/
}
