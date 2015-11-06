package com.example.mystocker;

import java.text.DecimalFormat;
import java.util.ArrayList;

import android.app.ActionBar.LayoutParams;
import android.app.AlarmManager;
import android.app.Dialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends ListActivity implements OnSharedPreferenceChangeListener {

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
	private SwipeRefreshLayout swipeRefreshLayout;
	Context mContext;
	int currentPosition;
	Handler stopRefreshHandler;
	private static final String PrefAutoUpdate = "PREF_AUTO_UPDATE";
	private static final String PrefUpdateFreq = "PREF_UPDATE_FREQ";
	private double updateFreqMin = 0;
	private boolean autoUpdate = false;
    private static final String TAG="LifeCycle";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.i(TAG,"onCreate");
		setContentView(R.layout.activity_main);
		mContext = this;
		stopRefreshHandler = new Handler(Looper.getMainLooper());
		quoteAdapter = (QuoteAdapter) App.getDataHandler().getAdatper();
		this.setListAdapter(quoteAdapter);
		PreferenceManager.getDefaultSharedPreferences(mContext).registerOnSharedPreferenceChangeListener(this);
		addButton = (Button) findViewById(R.id.add_symbols_button);
		addButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				addSymbol();
				startService(new Intent(MainActivity.this, StockUpdateService.class));
			}
		});
		symbolText = (EditText) findViewById(R.id.stock_symbols);
		swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);
		swipeRefreshLayout.setColorSchemeColors(0xff5c87eb);
		swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

			@Override
			public void onRefresh() {
				// TODO Auto-generated method stub
				swipeRefreshLayout.setRefreshing(true);
				App.getDataHandler().refreshStocks();
				stopRefreshHandler.postDelayed(new Runnable() {
					@Override
					public void run() {
						swipeRefreshLayout.setRefreshing(false);
					}
				}, 1500);

			}
		});
		swipeRefreshLayout.post(new Runnable() {
			@Override
			public void run() {
				swipeRefreshLayout.setRefreshing(true);
				App.getDataHandler().refreshStocks();
				stopRefreshHandler.postDelayed(new Runnable() {
					@Override
					public void run() {
						swipeRefreshLayout.setRefreshing(false);
					}
				}, 1500);
			}
		});

	}

	private static final int MENU_PREFERENCES = Menu.FIRST + 1;
	private static final int SHOW_PREFERENCES = 1;

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		// getMenuInflater().inflate(R.menu.main, menu);
		super.onCreateOptionsMenu(menu);
		menu.add(0, MENU_PREFERENCES, Menu.NONE, R.string.menu_preferences).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		switch (id) {
		case MENU_PREFERENCES: {
			startActivityForResult(new Intent(this, FragmentPreferences.class), SHOW_PREFERENCES);
			return true;
		}
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		App.getDataHandler().saveStockToFile();
		
		super.onDestroy();
		Log.i(TAG,"onDestory");
	}

	@Override
	protected void onResume() {
		super.onResume();
		Log.i(TAG,"onResume");

	}

	@Override
	protected void onStop() {
		App.getDataHandler().unregisterAutoUpdate();
		super.onStop();
		Log.i(TAG,"onStop");
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		// TODO Auto-generated method stub
		super.onListItemClick(l, v, position, id);
		StockInfo quote = (StockInfo) quoteAdapter.getItem(position);
		currentPosition = position;
		Log.i("LISTCLICK", position + "\n");
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
			chartView.setImageBitmap(BitmapFactory.decodeByteArray(quote.chart, 0, quote.chart.length));
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

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
		// TODO Auto-generated method stub
		if (key.equals(PrefAutoUpdate)) {
			autoUpdate = sharedPreferences.getBoolean(key, false);
			if (autoUpdate) {
				App.getDataHandler().registerAutoUpdate((long) (updateFreqMin * 1000));
			} else {
				App.getDataHandler().unregisterAutoUpdate();
			}
		} else if (key.equals(PrefUpdateFreq)) {
			updateFreqMin = Double.parseDouble(sharedPreferences.getString(key, "60"));
		}
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		Log.i(TAG,"onPause");
	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		Log.i(TAG,"onStart");
		if (PreferenceManager.getDefaultSharedPreferences(mContext).contains(PrefAutoUpdate) == false) {
			SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(mContext).edit();
			editor.putBoolean(PrefAutoUpdate, false);
			editor.apply();
			autoUpdate = false;
		} else {
			autoUpdate = PreferenceManager.getDefaultSharedPreferences(mContext).getBoolean(PrefAutoUpdate, false);
		}

		if (PreferenceManager.getDefaultSharedPreferences(mContext).contains(PrefUpdateFreq) == false) {
			SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(mContext).edit();
			editor.putString(PrefUpdateFreq, "60");
			editor.apply();
			updateFreqMin = 60;
		} else {
			updateFreqMin = Double.parseDouble(PreferenceManager.getDefaultSharedPreferences(mContext).getString(PrefUpdateFreq, "60"));
		}

		if (autoUpdate) {
			App.getDataHandler().registerAutoUpdate((long) (updateFreqMin * 1000));
		} else {
			App.getDataHandler().unregisterAutoUpdate();
		}
		
		
	}

}
