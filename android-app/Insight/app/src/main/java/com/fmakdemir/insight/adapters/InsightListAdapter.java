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
	private final ArrayList<String> insightList;

	public InsightListAdapter(Context context, ArrayList<String> insightList) {

		super(context, R.layout.insight_list_item, insightList);

		this.context = context;
		this.insightList = insightList;
	}

	private static class InsightViewHolder {
		public TextView titleView;
	}

	InsightViewHolder insightViewHolder;

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		if (convertView == null) {
			// Get rowView from inflater
			convertView = View.inflate(context, R.layout.insight_list_item, null);

			insightViewHolder = new InsightViewHolder();
			insightViewHolder.titleView = (TextView) convertView.findViewById(R.id.insight_item_id);
			convertView.setTag(insightViewHolder);
		} else {
			insightViewHolder = (InsightViewHolder) convertView.getTag();
		}

		insightViewHolder.titleView.setText(insightList.get(position));
		insightViewHolder.titleView.setTextColor(context.getResources().getColor(R.color.black));

		// return view
		return convertView;
	}

	@Override
	public void add(String insightId) {
		insightList.add(insightId);
		this.notifyDataSetChanged();
	}

	public void remove(String item_id) {
		insightList.remove(item_id);
//		Log.i("NL", insightList.toString());
		this.notifyDataSetChanged();
	}

	public String get(int position) {
		return insightList.get(position);
	}
}
