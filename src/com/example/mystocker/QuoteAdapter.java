package com.example.mystocker;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

class QuoteAdapter extends BaseAdapter {
	public DataHandler dataHandler;
	Context context;
	public QuoteAdapter(Context mContext, DataHandler mDataHandler) {
		context = mContext;
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
		StockInfoCellView v = (StockInfoCellView)arg1;
		if (v == null) {
			v =new StockInfoCellView(context);
		} 
        v.setStockInfo(quote, arg0);
		return v;
	}
}