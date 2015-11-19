package com.example.mystocker;

import java.text.DecimalFormat;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class FocusedStockDetailFragment extends Fragment{
	private int position;
	private Context context;
    private StockDetailFragmentButtonClickListener listener;
	public FocusedStockDetailFragment(Context context,StockDetailFragmentButtonClickListener listener) {
		this.context = context;
		this.listener=listener;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.quote_detail, container, false);
		StockInfo stockInfo = App.getDataHandler().getFocusedQuoteFromIndex(position);
		TextView stockNameTV=(TextView)view.findViewById(R.id.stock_name);
		TextView currentTV = (TextView) view.findViewById(R.id.current);
		TextView noTextView = (TextView) view.findViewById(R.id.no);
		TextView openTextView = (TextView) view.findViewById(R.id.opening_price);
		TextView closeTextView = (TextView) view.findViewById(R.id.closing_price);
		TextView dayLowTextView = (TextView) view.findViewById(R.id.day_low);
		TextView dayHighTextView = (TextView) view.findViewById(R.id.day_high);
		ImageView chartView = (ImageView) view.findViewById(R.id.chart_view);
		Button focusButton = (Button) view.findViewById(R.id.focusButton);
		if (stockInfo.isFocused()) {
			focusButton.setText("取消关注");
		} else {
			focusButton.setText("关注");
		}

		Button closeButton = (Button) view.findViewById(R.id.closeButton);
		if (stockInfo.isBadNO()) {
			stockNameTV.setText("股票代码错误");
			currentTV.setText("");
			openTextView.setText("");
			closeTextView.setText("");
			dayLowTextView.setText("");
			dayHighTextView.setText("");
			noTextView.setText("");
			chartView.setImageDrawable(context.getResources().getDrawable(android.R.drawable.ic_delete));
		} else {
		    stockNameTV.setText(stockInfo.getName());
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

		focusButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				App.getDataHandler().setFocused(position,!App.getDataHandler().isFocused(position));
				if(listener!=null){
					listener.onFocusClicked();
				}
			}
		});
		closeButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(listener!=null)
				{
					listener.onCloseClicked();
				}
			}
		});

		return view;
	}

	public void setStockInfo(int position) {
		this.position = position;
	}
public interface StockDetailFragmentButtonClickListener{
	public void onFocusClicked();
	public void onCloseClicked();
}
}
