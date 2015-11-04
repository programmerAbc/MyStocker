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
	DataHandler mDataHandler;
	Context mContext;
    int currentPosition;
	public final static String saveFilePath;

	static {
		saveFilePath = Environment.getExternalStorageDirectory().toString() + "/MyStocks";
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mContext = this;
		File mFile = new File(saveFilePath + "/symbols.txt");
		if (mFile.exists() == false) {
			try {
				FileOutputStream outputStream = openFileOutput("symbols.txt", MODE_PRIVATE);
				outputStream.close();
			} catch (Exception e) {
			}
		}
		mDataHandler = new DataHandler(mContext);
		quoteAdapter = new QuoteAdapter(this, this, mDataHandler);
		this.setListAdapter(quoteAdapter);
		addButton = (Button) findViewById(R.id.add_symbols_button);
		addButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				addSymbol();
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

	class QuoteAdapter extends BaseAdapter implements Runnable {
		private static final int DISPLAY_COUNT = 10;
		public DataHandler dataHandler;
		private boolean forceUpdate = false;
		Context context;
		MainActivity mainActivity;
		LayoutInflater inflater;
		QuoteRefreshTask quoteRefreshTask = null;
		int progressInterval;
		Handler messageHandler = new Handler();
		private final int[] backgroundColor = { Color.rgb(119, 138, 170), Color.rgb(48, 92, 131) };

		public QuoteAdapter(MainActivity aController, Context mContext, DataHandler mDataHandler) {
			context = mContext;
			inflater = LayoutInflater.from(context);
			mainActivity = aController;
			dataHandler = mDataHandler;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return dataHandler.stocksSize();
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

		public void stopRefresh() {
			quoteRefreshTask.cancelTimer();
			quoteRefreshTask = null;
		}

		public void startRefresh() {

			if (quoteRefreshTask == null)
				quoteRefreshTask = new QuoteRefreshTask(this);
		}

		@Override
		public void run() {
			// TODO Auto-generated method stub
			if (dataHandler.stocksSize() > 0) {
				if (forceUpdate) {
					forceUpdate = false;
					progressInterval = 10000 / DISPLAY_COUNT;
					mainActivity.setProgressBarVisibility(true);
					mainActivity.setProgress(progressInterval);
					dataHandler.refreshStocks();
				}
				this.notifyDataSetChanged();
			}
		}

		public void refreshStocks() {
			dataHandler.refreshStocks();
			this.notifyDataSetChanged();
		}

		public void addSymbols(ArrayList<String> symbols) {
			dataHandler.addSymbols(symbols);
			refreshStocks();
		}

		public void removeQuoteAtIndex(int index) {
			dataHandler.removeQuoteByIndex(index);
			this.notifyDataSetChanged();
		}

		private class ViewHolder {
			public TextView symbolTV;
			public TextView nameTV;
			public TextView currentTV;
			public TextView percentTV;
		}

		public class QuoteRefreshTask extends TimerTask {
			QuoteAdapter quoteAdatper;
			Timer refreshTimer;
			final static int TEN_SECONDS = 10000;

			public QuoteRefreshTask(QuoteAdapter anAdapter) {
				refreshTimer = new Timer("Quote Refresh Timer");
				refreshTimer.schedule(this, TEN_SECONDS, TEN_SECONDS);
				quoteAdatper = anAdapter;
			}

			@Override
			public void run() {
				// TODO Auto-generated method stub
				messageHandler.post(quoteAdatper);
			}

			public void startTimer() {
				if (refreshTimer == null) {
					refreshTimer = new Timer("Quote Refersh Timer");
					refreshTimer.schedule(this, TEN_SECONDS, TEN_SECONDS);
				}
			}

			public void cancelTimer() {
				this.cancel();
				refreshTimer.cancel();
				refreshTimer = null;
			}
		}
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		mDataHandler.saveStockToFile();
		super.onDestroy();
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (quoteAdapter != null) {
			quoteAdapter.startRefresh();
		}
	}

	@Override
	protected void onStop() {
		super.onStop();
		quoteAdapter.startRefresh();
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
					quoteAdapter.removeQuoteAtIndex(currentPosition);
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
			chartView.setImageBitmap(mDataHandler.getChartFromSymbol(quote.no));
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
		quoteAdapter.addSymbols(symbolList);
		symbolText.setText(null);
	}
}
