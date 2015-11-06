package com.example.mystocker;

import java.util.ArrayList;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.AsyncTask;
import android.widget.Toast;

public class StockDatabase extends SQLiteOpenHelper {
	public static final String DATABASE_NAME = "StockDatabase.db";
	private static final String TABLE_NAME = "stocktable";
	private static final String STOCK_COLUMN = "stock";
	public static final int DATABASE_VERSION = 1;
	private static final String CREATE_TABLE = "create table if not exists " + TABLE_NAME + " (_id integer primary key autoincrement, " + STOCK_COLUMN + " text not null);";
	private static final String INSERT_DATA = "insert into " + TABLE_NAME + "(" + STOCK_COLUMN + ") values(?);";
	private static final String GET_DATA = "select " + STOCK_COLUMN + " from " + TABLE_NAME + ";";
	private static final String CLEAR_DATA = "delete from " + TABLE_NAME + ";";
	private static final String DROP_TABLE="drop table if exists " + TABLE_NAME + ";";
	
	private Context context;

	public StockDatabase(Context context, String name, CursorFactory factory, int version) {
		super(context, name, factory, version);
		this.context = context;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		db.execSQL(CREATE_TABLE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		if (oldVersion < newVersion) {
			db.execSQL(DROP_TABLE);
			onCreate(db);
		}
	}

	public ArrayList<StockInfo> selectStock() {
		ArrayList<StockInfo> stocks = new ArrayList<StockInfo>();
		SQLiteDatabase db = StockDatabase.this.getReadableDatabase();
		Cursor cursor = db.rawQuery(GET_DATA, null);
		if (cursor.getCount() == 0) {
			return null;
		}
		while (cursor.moveToNext()) {
			StockInfo sinfo = new StockInfo();
			sinfo.setNo(cursor.getString(0));
			stocks.add(sinfo);
		}
		return stocks;
	}

	public void insertStocks(ArrayList<StockInfo> stocks) {
		if (stocks.isEmpty() == false) {
			ArrayList<String> stockNOs = new ArrayList<String>();
			for (StockInfo sinfo : stocks) {
				stockNOs.add(sinfo.getNo());
			}
			new InsertAsyncTask().execute(stockNOs);
		} else {
			SQLiteDatabase db = this.getWritableDatabase();
			db.execSQL(DROP_TABLE);
			db.execSQL(CREATE_TABLE);
			Toast.makeText(context, "数据库更新成功", Toast.LENGTH_LONG).show();
		}
	}

	private class InsertAsyncTask extends AsyncTask<ArrayList<String>, Integer, Integer> {
		@Override
		protected Integer doInBackground(ArrayList<String>... params) {
			// TODO Auto-generated method stub
			SQLiteDatabase db = StockDatabase.this.getWritableDatabase();
			ArrayList<String> stocks = params[0];
			int resultCode = -1;
			db.beginTransaction();
			db.execSQL(DROP_TABLE);
			db.execSQL(CREATE_TABLE);
			try {
				for (String stock : stocks) {
					db.execSQL(INSERT_DATA, new Object[] { stock });
				}
				db.setTransactionSuccessful();
				resultCode = 1;
			} catch (Exception e) {
				resultCode = -1;
			} finally {
				db.endTransaction();
			}
			return resultCode;
		}

		@Override
		protected void onPostExecute(Integer result) {
			// TODO Auto-generated method stub
			switch (result) {
			case 1: {
				Toast.makeText(context, "数据库更新成功", Toast.LENGTH_LONG).show();
				break;
			}
			case -1: {
				Toast.makeText(context, "数据库更新失败", Toast.LENGTH_LONG).show();
				break;
			}

			}
			super.onPostExecute(result);
		}
	}
}
