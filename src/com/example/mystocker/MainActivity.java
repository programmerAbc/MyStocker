package com.example.mystocker;

import java.util.ArrayList;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
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
import android.widget.ListView;
import android.widget.Toast;

public class MainActivity extends ListActivity implements OnSharedPreferenceChangeListener {

	private QuoteAdapter quoteAdapter;
	private EditText symbolText;
	private Button addButton;
	private SwipeRefreshLayout swipeRefreshLayout;
	Context mContext;
	int currentPosition;
	Handler stopRefreshHandler;
	private static final String PrefAutoUpdate = "PREF_AUTO_UPDATE";
	private static final String PrefUpdateFreq = "PREF_UPDATE_FREQ";
	private double updateFreqMin = 0;
	private boolean autoUpdate = false;
    private static final String TAG="LifeCycle";
    private StockDetailDialogFragment stockDetailDialogFragment;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.i(TAG,"onCreate");
		setContentView(R.layout.activity_main);
		mContext = this;
		stockDetailDialogFragment=new StockDetailDialogFragment(mContext);
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

	
	private static final int SHOW_PREFERENCES = 1;

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		super.onCreateOptionsMenu(menu);
		//menu.add(0, MENU_PREFERENCES, Menu.NONE, R.string.menu_preferences).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
		 getMenuInflater().inflate(R.menu.main, menu);
		
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		switch (id) {
		case R.id.settings: {
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
		stockDetailDialogFragment.setStockInfo(position);
		stockDetailDialogFragment.show(getFragmentManager(),"DetailFragmentDialog");
	}

	private void addSymbol() {
		String symbolsStr = symbolText.getText().toString().trim();
		if (symbolsStr.equals("")) {
			Toast.makeText(mContext, "«Î ‰»Îπ…∆±¥˙¬Î", Toast.LENGTH_SHORT).show();
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
