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
	private ArrayList<StockInfo> fstockInfos = null;
	Context context;
	StockDatabase stockDatabase;
	QuoteAdapter adapter;
	FocusQuoteAdapter fadapter;
	Handler handler;
	Intent stockUpdateServiceIntent;
	PendingIntent stockUpdateServicePendingIntent;
	ArrayAdapter<String> suggestionAdapter;
    private boolean enableNotify=false;
	
	public DataHandler(Context context) {
		this.context = context;
		handler = new Handler(Looper.getMainLooper());

		suggestionAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, new ArrayList<String>());
		stockDatabase = new StockDatabase(context, StockDatabase.DATABASE_NAME, null, StockDatabase.DATABASE_VERSION);
		stockInfos = stockDatabase.selectStock();
		if (stockInfos == null) {
			stockInfos = new ArrayList<StockInfo>();
		} else {
			refreshStocks();
		}
		fstockInfos = new ArrayList<StockInfo>();
		adapter = new QuoteAdapter(this);
		fadapter = new FocusQuoteAdapter(this);
		populateSuggestionAdapter();
		populateFocusedStockInfos();
		stockUpdateServiceIntent = new Intent(context, StockUpdateService.class);
		stockUpdateServicePendingIntent = PendingIntent.getService(context, 0, stockUpdateServiceIntent, PendingIntent.FLAG_UPDATE_CURRENT);
	}

	private void populateFocusedStockInfos() {
		fstockInfos.clear();
		if (stockInfos != null && stockInfos.isEmpty() == false) {
			for (StockInfo sinfo : stockInfos) {
				if (sinfo.isFocused()) {
					fstockInfos.add(sinfo);
				}
			}
		}
		fadapter.notifyDataSetChanged();
	}

	public BaseAdapter getAdatper() {
		return adapter;
	}

	public BaseAdapter getFocusedAdapter() {
		return fadapter;
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
				if(enableNotify&&stockinfo.isFocused()){
				NotificationFactory.Notify(context, sinfo);
				enableNotify=false;
				}
				break;
			}
		}
		if (dataHasChanged) {
			handler.post(new Runnable() {
				@Override
				public void run() {
					// TODO Auto-generated method stub
					adapter.notifyDataSetChanged();
					fadapter.notifyDataSetChanged();
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
		enableNotify=true;
		context.startService(new Intent(context, StockUpdateService.class));
	}

	public int size() {
		if (stockInfos != null) {
			return stockInfos.size();
		} else {
			return 0;
		}
	}

	public int focusedSize() {
		if (fstockInfos != null) {
			return fstockInfos.size();
		} else {
			return 0;
		}
	}

	public StockInfo get(int index) {
		if (stockInfos != null && stockInfos.isEmpty() == false) {
			return stockInfos.get(index);
		} else {
			return null;
		}
	}

	public StockInfo focusedGet(int index) {
		if (fstockInfos != null && fstockInfos.isEmpty() == false) {
			return fstockInfos.get(index);
		} else {
			return null;
		}
	}

	public ArrayList<StockInfo> getAll() {
		return stockInfos;
	}

	public ArrayList<StockInfo> focusedGetAll() {
		return fstockInfos;
	}

	public boolean isEmpty() {
		return stockInfos.isEmpty();
	}

	public boolean focusedIsEmpty() {
		return fstockInfos.isEmpty();
	}

	public void removeQuoteByIndex(int index) {
	
		if (stockInfos != null && stockInfos.isEmpty() == false) {
			stockInfos.remove(index);
			adapter.notifyDataSetChanged();
			populateSuggestionAdapter();
			populateFocusedStockInfos();
		}
	}

	public void removeFocusedQuoteByIndex(int index) {
		
		if (fstockInfos != null && fstockInfos.isEmpty() == false) {
			
           StockInfo stock_info=fstockInfos.get(index);
           fstockInfos.remove(index);
           fadapter.notifyDataSetChanged();
           for(StockInfo sinfo:stockInfos)
           {
        	   if(sinfo.equals(stock_info))
        	   {
        		   
        		   sinfo.setFocused(false);
                   adapter.notifyDataSetChanged();
                   return;
        	   }
        	   
           }
           
		}
	}

	public StockInfo getQuoteFromIndex(int index) {
		return get(index);
	}

	public StockInfo getFocusedQuoteFromIndex(int index) {
		return focusedGet(index);
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

	public void setFocused(int position, boolean isFocused) {
		stockInfos.get(position).setFocused(isFocused);
		adapter.notifyDataSetChanged();
		populateFocusedStockInfos();
	}

	public void fresetFocused(int position) {
		removeFocusedQuoteByIndex(position);
	}

	public boolean isFocused(int position) {
		return stockInfos.get(position).isFocused();
	}
}
