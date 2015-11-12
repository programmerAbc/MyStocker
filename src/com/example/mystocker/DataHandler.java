package com.example.mystocker;

import java.util.ArrayList;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.ViewDebug.FlagToString;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Toast;

public class DataHandler {
	private ArrayList<StockInfo> stockInfos = null;
	Context context;
	StockDatabase stockDatabase;
	QuoteAdapter adapter;
	Handler handler;
	Intent stockUpdateServiceIntent;
	PendingIntent stockUpdateServicePendingIntent;
	ArrayAdapter<String> suggestionAdapter;
	public DataHandler(Context context) {
		this.context = context;
		handler = new Handler(Looper.getMainLooper());
		
		suggestionAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1,new ArrayList<String>());
		stockDatabase = new StockDatabase(context, StockDatabase.DATABASE_NAME, null, StockDatabase.DATABASE_VERSION);
		stockInfos = stockDatabase.selectStock();
		if (stockInfos == null) {
			stockInfos = new ArrayList<StockInfo>();
		} else {
			refreshStocks();
		}
		populateSuggestionAdapter();
		adapter = new QuoteAdapter(this);
		stockUpdateServiceIntent = new Intent(context, StockUpdateService.class);
		stockUpdateServicePendingIntent = PendingIntent.getService(context, 0, stockUpdateServiceIntent, PendingIntent.FLAG_UPDATE_CURRENT);
	}

	public BaseAdapter getAdatper() {
		return adapter;
	}

	private void populateSuggestionAdapter() {
		Log.i("POPULATE", "HERE");
		suggestionAdapter.clear();
		if (stockInfos.isEmpty() == false) {
			for (StockInfo stockInfo : stockInfos) {
				suggestionAdapter.add(stockInfo.getNo());
			}
		}	
	}

	public void updateStock(StockInfo sinfo) {
		if (stockInfos.isEmpty())
			return;
		boolean dataHasChanged = false;
		for (StockInfo stockinfo : stockInfos) {
			if (stockinfo.equals(sinfo)) {
				stockinfo.copyFrom(sinfo);
				dataHasChanged = true;
				break;
			}
		}
		if (dataHasChanged) {
			handler.post(new Runnable() {
				@Override
				public void run() {
					// TODO Auto-generated method stub
					adapter.notifyDataSetChanged();
				}
			});
		}
	}

	public void addSymbols(ArrayList<String> stockList) {
		if (stockList != null) {
			{
				ArrayList<StockInfo> newStockInfos = new ArrayList<StockInfo>();
				boolean foundSymbol = false;
				for (String newSymbol : stockList) {
					for (StockInfo sinfo : stockInfos) {
						if (sinfo.getNo().equals(newSymbol)) {
							foundSymbol = true;
							break;
						}
					}
					if (foundSymbol == false) {
						StockInfo tempSinfo = new StockInfo();
						tempSinfo.setNo(newSymbol);
						newStockInfos.add(tempSinfo);
					}
				}
				if (newStockInfos.isEmpty() == false) {
					stockInfos.addAll(newStockInfos);
					adapter.notifyDataSetChanged();
					populateSuggestionAdapter();
					Toast.makeText(context, "股票添加成功", Toast.LENGTH_SHORT).show();
				} else {
					Toast.makeText(context, "股票添加失败", Toast.LENGTH_SHORT).show();
				}
			}
		}
	}

	public void saveStockToFile() {
		stockDatabase.insertStocks(stockInfos);
	}

	public void refreshStocks() {
		context.startService(new Intent(context, StockUpdateService.class));
	}

	public int size() {
		if (stockInfos != null) {
			return stockInfos.size();
		} else {
			return 0;
		}
	}

	public StockInfo get(int index) {
		return stockInfos.get(index);
	}

	public ArrayList<StockInfo> getAll() {
		return stockInfos;
	}

	public boolean isEmpty() {
		return stockInfos.isEmpty();
	}

	public void removeQuoteByIndex(int index) {
		stockInfos.remove(index);
		adapter.notifyDataSetChanged();
		populateSuggestionAdapter();
	}

	public StockInfo getQuoteFromIndex(int index) {
		return stockInfos.get(index);
	}

	public void registerAutoUpdate(long timeInterval) {
		((AlarmManager) context.getSystemService(Context.ALARM_SERVICE)).setRepeating(AlarmManager.ELAPSED_REALTIME, 0, timeInterval, stockUpdateServicePendingIntent);
	}

	public void unregisterAutoUpdate() {
		((AlarmManager) context.getSystemService(Context.ALARM_SERVICE)).cancel(stockUpdateServicePendingIntent);
	}

	public ArrayAdapter<String> getSuggestionAdatper() {

		return suggestionAdapter;
	}
}
