package com.example.mystocker;

import java.util.ArrayList;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;

public class StockProvider extends ContentProvider{

	/////////////////
	public static final Uri CONTENT_URI = Uri.parse("content://com.example.mystock/stockinfos");
	// Column Names
	public static final String KEY_STOCK_NO = "no";
	public static final String KEY_STOCK_NAME = "name";
	public static final String KEY_STOCK_OPENING_PRICE = "opening_price";
	public static final String KEY_STOCK_CLOSING_PRICE = "closing_price";
	public static final String KEY_STOCK_CURRENT_PRICE = "current_price";
	public static final String KEY_STOCK_MAX_PRICE = "max_price";
	public static final String KEY_STOCK_MIN_PRICE = "min_price";
	public static final String KEY_STOCK_BAD_NO = "bad_no";
	public static final String KEY_STOCK_CHART="stock_chart";
	//////////////////
	private static final int STOCKS = 1;
	private static final int STOCK = 2;
	private static final int SEARCH = 3;
	private static final UriMatcher uriMatcher;
	static {
		uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		uriMatcher.addURI("com.example.mystock", "stockinfos", STOCKS);
		uriMatcher.addURI("com.example.mystock", "stockinfos/#", STOCK);
	}
	
	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getType(Uri uri) {
		switch (uriMatcher.match(uri)) {
		case STOCKS: {
			return "vnd.android.cursor.dir/vnd.mystock.stockinfo";
		}
		case STOCK: {
			return "vnd.android.cursor.item/vnd.mystock.stockinfo";
		}
		case SEARCH: {
			return "";
		}
		default: {
			return "Unsupported URI: " + uri;
		}
		}
	}
	
	@Override
	public Uri insert(Uri uri, ContentValues values) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean onCreate() {
		return true;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
		// TODO Auto-generated method stub
		if (App.getDataHandler().isEmpty())
			return null;
		MatrixCursor mc = null;
		switch (uriMatcher.match(uri)) {
		case STOCK: {
			mc = new MatrixCursor(new String[] { "_id", "stockno" });
			mc.addRow(new String[] { uri.getPathSegments().get(1), App.getDataHandler().get(Integer.parseInt(uri.getPathSegments().get(1))).getNo() });
			break;
		}
		case STOCKS: {
			mc = new MatrixCursor(new String[] { "_id", "stockno" });
			int stockInfosSize = App.getDataHandler().size();
			for (int i = 0; i < stockInfosSize; ++i) {
				mc.addRow(new String[] { String.valueOf(i), App.getDataHandler().get(i).getNo() });
			}
			break;
		}
		case SEARCH: {

			break;
		}
		default: {

			break;
		}
		}
		if (mc != null) {
			mc.setNotificationUri(getContext().getContentResolver(), uri);
		}
		return mc;
	}
	@Override
	public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
		// TODO Auto-generated method stub
		StockInfo stockInfo = new StockInfo();
		stockInfo.setNo(values.getAsString(KEY_STOCK_NO));
		stockInfo.setName(values.getAsString(KEY_STOCK_NAME));
		stockInfo.setOpening_price(values.getAsString(KEY_STOCK_OPENING_PRICE));
		stockInfo.setClosing_price(values.getAsString(KEY_STOCK_CLOSING_PRICE));
		stockInfo.setCurrent_price(values.getAsString(KEY_STOCK_CURRENT_PRICE));
		stockInfo.setMax_price(values.getAsString(KEY_STOCK_MAX_PRICE));
		stockInfo.setMin_price(values.getAsString(KEY_STOCK_MIN_PRICE));
		stockInfo.setBadNO(values.getAsBoolean(KEY_STOCK_BAD_NO));
        stockInfo.setChart(values.getAsByteArray(KEY_STOCK_CHART));
		App.getDataHandler().updateStock(stockInfo);
	    
		getContext().getContentResolver().notifyChange(uri, null);
		return 0;
	}

}
