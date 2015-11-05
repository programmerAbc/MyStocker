package com.example.mystocker;

import java.text.DecimalFormat;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

class QuoteAdapter extends BaseAdapter {
	public DataHandler dataHandler;
	Context context;
	MainActivity mainActivity;
	LayoutInflater inflater;
	private final int[] backgroundColor = { Color.rgb(119, 138, 170), Color.rgb(48, 92, 131) };

	public QuoteAdapter(Context mContext, DataHandler mDataHandler) {
		context = mContext;
		inflater = LayoutInflater.from(context);
		dataHandler = mDataHandler;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return dataHandler.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return dataHandler.getQuoteFromIndex(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	@Override
	public View getView(int arg0, View arg1, ViewGroup arg2) {
		StockInfo quote = dataHandler.getQuoteFromIndex(arg0);
		View v = arg1;
		ViewHolder vh;
		if (v == null) {
			v = inflater.inflate(R.layout.quote_cell, null);
			vh = new ViewHolder();
			vh.currentTV = (TextView) v.findViewById(R.id.current);
			vh.nameTV = (TextView) v.findViewById(R.id.name);
			vh.percentTV = (TextView) v.findViewById(R.id.percent);
			vh.symbolTV = (TextView) v.findViewById(R.id.symbol);
			v.setTag(vh);
		} else {
			vh = (ViewHolder) v.getTag();
		}

		vh.symbolTV.setText(quote.getNo());
		vh.nameTV.setText(quote.getName());
		double current = Double.parseDouble(quote.getCurrent_price());
		double closing_price = Double.parseDouble(quote.getClosing_price());
		DecimalFormat df = new DecimalFormat("#0.00");
		vh.currentTV.setText(df.format(current));
		if (current > closing_price) {
			vh.percentTV.setTextColor(0xffee3b3b);
		} else {
			vh.percentTV.setTextColor(0xff2e8b57);
		}
		vh.percentTV.setText(df.format(((current - closing_price) * 100 / closing_price)) + "%");
		if (quote.isBadNO()) {
			v.setBackgroundColor(0xff880000);
		} else {
			v.setBackgroundColor(backgroundColor[arg0 % 2]);
		}
		return v;
	}
	
	private class ViewHolder {
		public TextView symbolTV;
		public TextView nameTV;
		public TextView currentTV;
		public TextView percentTV;
	}
	
}