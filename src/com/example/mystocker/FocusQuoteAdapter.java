package com.example.mystocker;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public class FocusQuoteAdapter extends BaseAdapter{
	public DataHandler dataHandler;
	Context context;
	FocusedStockInfoCellView.CellInterface cellInterface;
	public FocusQuoteAdapter(DataHandler dataHandler) {
		// TODO Auto-generated constructor stub
		this.dataHandler=dataHandler;
	}
	
	public void setActivityContext(Context context){
		this.context=context;
	}
	public void setCellInterface(FocusedStockInfoCellView.CellInterface cellInterface){
		this.cellInterface=cellInterface;
	}
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return dataHandler.focusedSize();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return dataHandler.focusedGet(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		StockInfo quote = dataHandler.getFocusedQuoteFromIndex(position);
        FocusedStockInfoCellView v = (FocusedStockInfoCellView)convertView;
		if (v == null) {
			v =new FocusedStockInfoCellView(context);
			v.setCellInterface(cellInterface);
		} 
        v.setStockInfo(quote, position);
		return v;
	}
}
