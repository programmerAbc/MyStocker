package com.example.mystocker;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

class QuoteAdapter extends BaseAdapter{
	public DataHandler dataHandler;
	
	StockInfoCellView.CellInterface cellInterface;
	
	
	public QuoteAdapter(DataHandler mDataHandler) {
		dataHandler = mDataHandler;
	}

	public void setCellInterface(StockInfoCellView.CellInterface cellInterface){
		this.cellInterface=cellInterface;
		if(cellInterface==null)
		{
			Log.i("DESTROY", "cellInterface==null");
		}
		
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
	public View getView(int position, View convertView, ViewGroup parent) {
		StockInfo quote = dataHandler.getQuoteFromIndex(position);
		StockInfoCellView v = (StockInfoCellView)convertView;
		if (v == null) {
			v =new StockInfoCellView(parent.getContext());
			v.setCellInterface(cellInterface);
		} 
        v.setStockInfo(quote, position);
		return v;
	}

	
}