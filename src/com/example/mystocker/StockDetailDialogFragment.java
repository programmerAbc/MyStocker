package com.example.mystocker;

import java.text.DecimalFormat;

import android.app.DialogFragment;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class StockDetailDialogFragment extends DialogFragment{
    private int position;
    private Context context;
    public StockDetailDialogFragment(Context context) {
		this.context=context;
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view=inflater.inflate(R.layout.quote_detail,container, false);
		StockInfo stockInfo=App.getDataHandler().getQuoteFromIndex(position);
		TextView currentTV=(TextView)view.findViewById(R.id.current);
		TextView noTextView=(TextView)view.findViewById(R.id.no);
		TextView openTextView=(TextView)view.findViewById(R.id.opening_price);
		TextView closeTextView=(TextView)view.findViewById(R.id.closing_price);
		TextView dayLowTextView=(TextView)view.findViewById(R.id.day_low);
		TextView dayHighTextView=(TextView)view.findViewById(R.id.day_high);
		ImageView chartView=(ImageView)view.findViewById(R.id.chart_view);
		Button deleteButton=(Button)view.findViewById(R.id.delete);
		Button closeButton=(Button)view.findViewById(R.id.close);
		if(stockInfo.isBadNO())
		{
			this.getDialog().setTitle("¹ÉÆ±´úÂë´íÎó");
			currentTV.setText("");
			openTextView.setText("");
			closeTextView.setText("");
			dayLowTextView.setText("");
			dayHighTextView.setText("");
			noTextView.setText("");
			chartView.setImageDrawable(context.getResources().getDrawable(android.R.drawable.ic_delete));
		}
		else
		{
			this.getDialog().setTitle(stockInfo.getName());
			double current = Double.parseDouble(stockInfo.getCurrent_price());
			double closing_price = Double.parseDouble(stockInfo.getClosing_price());
			DecimalFormat df = new DecimalFormat("#0.00");
			String percent = df.format((current - closing_price) * 100 / closing_price) + "%";
			if (current > closing_price) {
				currentTV.setTextColor(0xffee3b3b);
			} else {
				currentTV.setTextColor(0xff2e8b57);
			}
			currentTV.setText(df.format(current) + "(" + percent + ")");
			openTextView.setText(stockInfo.opening_price);
			closeTextView.setText(stockInfo.closing_price);
			dayLowTextView.setText(stockInfo.min_price);
			dayHighTextView.setText(stockInfo.max_price);
			noTextView.setText(stockInfo.no);
			chartView.setImageBitmap(BitmapFactory.decodeByteArray(stockInfo.chart, 0, stockInfo.chart.length));	
		}
		
		deleteButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				App.getDataHandler().removeQuoteByIndex(position);
			    StockDetailDialogFragment.this.dismiss();
			}
		});
		closeButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				StockDetailDialogFragment.this.dismiss();
			}
		});
		
		
		return view;
	}
   
	public void setStockInfo(int position)
	{
		this.position=position;
	}
	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		getDialog().getWindow().setLayout(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT);
	}
}
