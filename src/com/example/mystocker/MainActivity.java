package com.example.mystocker;

import java.io.File;
import java.io.FileOutputStream;
import java.lang.reflect.Array;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;


import android.app.ActionBar.LayoutParams;
import android.app.Dialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends ListActivity {

	private QuoteAdapter quoteAdapter;
	private EditText symbolText;
	private Button addButton;
	private Button cancelButton;
	private Button deleteButton;
	private Dialog dialog = null;
	private TextView currentTextView;
	private TextView noTextView;
	private TextView openTextView;
	private TextView closeTextView;
	private TextView dayLowTextView;
	private TextView dayHighTextView;
	private ImageView chartView;
	
	Context mContext;
    int currentPosition;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mContext = this;
		quoteAdapter = (QuoteAdapter) App.getDataHandler().getAdatper();
		this.setListAdapter(quoteAdapter);
		addButton = (Button) findViewById(R.id.add_symbols_button);
		addButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				addSymbol();
				startService(new Intent(MainActivity.this,StockUpdateService.class));
			}
		});
		symbolText = (EditText) findViewById(R.id.stock_symbols);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		App.getDataHandler().saveStockToFile();
		super.onDestroy();
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onStop() {
		super.onStop();
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		// TODO Auto-generated method stub
		super.onListItemClick(l, v, position, id);
		StockInfo quote = (StockInfo) quoteAdapter.getItem(position);
		currentPosition= position;
		Log.i("LISTCLICK",position+"\n");
		if (dialog == null) {
			dialog = new Dialog(mContext);
			dialog.setContentView(R.layout.quote_detail);
			deleteButton = (Button) dialog.findViewById(R.id.delete);
			deleteButton.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					App.getDataHandler().removeQuoteByIndex(currentPosition);
					dialog.hide();
				}
			});
			cancelButton = (Button) dialog.findViewById(R.id.close);
			cancelButton.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					dialog.hide();
				}
			});
			currentTextView = (TextView) dialog.findViewById(R.id.current);
			noTextView = (TextView) dialog.findViewById(R.id.no);
			openTextView = (TextView) dialog.findViewById(R.id.opening_price);
			closeTextView = (TextView) dialog.findViewById(R.id.closing_price);
			dayLowTextView = (TextView) dialog.findViewById(R.id.day_low);
			dayHighTextView = (TextView) dialog.findViewById(R.id.day_high);
			chartView = (ImageView) dialog.findViewById(R.id.chart_view);
		}
		if (quote.isBadNO()) {
			dialog.setTitle("股票代码错误");
			currentTextView.setText("");
			openTextView.setText("");
			closeTextView.setText("");
			dayLowTextView.setText("");
			dayHighTextView.setText("");
			noTextView.setText("");
			chartView.setImageDrawable(mContext.getResources().getDrawable(android.R.drawable.ic_delete));
		} else {
			dialog.setTitle(quote.getName());
			double current = Double.parseDouble(quote.getCurrent_price());
			double closing_price = Double.parseDouble(quote.getClosing_price());
			DecimalFormat df = new DecimalFormat("#0.00");
			String percent = df.format((current - closing_price) * 100 / closing_price) + "%";
			if (current > closing_price) {
				currentTextView.setTextColor(0xffee3b3b);
			} else {
				currentTextView.setTextColor(0xff2e8b57);
			}
			currentTextView.setText(df.format(current) + "(" + percent + ")");
			openTextView.setText(quote.opening_price);
			closeTextView.setText(quote.closing_price);
			dayLowTextView.setText(quote.min_price);
			dayHighTextView.setText(quote.max_price);
			noTextView.setText(quote.no);
			chartView.setImageBitmap(BitmapFactory.decodeByteArray(quote.chart,0,quote.chart.length));
		}

		dialog.getWindow().setLayout(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		dialog.show();

	}

	private void addSymbol() {
		String symbolsStr = symbolText.getText().toString().trim();
		if (symbolsStr.equals("")) {
			Toast.makeText(mContext, "请输入股票代码", Toast.LENGTH_SHORT).show();
			return;
		}

		String symbolArray[] = symbolsStr.split("\n");
		int count = symbolArray.length;
		ArrayList<String> symbolList = new ArrayList<String>();
		for (int i = 0; i < count; ++i) {
			symbolList.add(symbolArray[i].trim());
		}
		App.getDataHandler().addSymbols(symbolList);
		symbolText.setText(null);
	}
}
