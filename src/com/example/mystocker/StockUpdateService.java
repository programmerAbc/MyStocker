package com.example.mystocker;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.IntentService;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;

public class StockUpdateService extends IntentService {
	private static final String QUERY_URL = "http://hq.sinajs.cn/list=";
	private static final String QUERY_IMG = "http://image.sinajs.cn/newchart/daily/n/";
	private static final int BUF_SIZE = 1024;
	private static final int NAME = 0;
	private static final int OPENING_PRICE = 1;
	private static final int CLOSING_PRICE = 2;
	private static final int CURRENT_PRICE = 3;
	private static final int MAX_PRICE = 4;
	private static final int MIN_PRICE = 5;

	public StockUpdateService(String name) {
		super(name);
		// TODO Auto-generated constructor stub
	}

	public StockUpdateService() {
		super("Stock update service");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		// TODO Auto-generated method stub
		refreshStock();
	}

	public void refreshStock() {
		ContentResolver cr = getContentResolver();
		Cursor query = cr.query(StockProvider.CONTENT_URI, null, null, null, null);
		if (query.getCount() == 0) {
			query.close();
			return;
		}
		ArrayList<String> stocks = new ArrayList<String>();
		while (query.moveToNext()) {
			stocks.add(query.getString(1));
		}
		query.close();
		ArrayList<StockInfo> stockInfos = getQuotesFromArray(stocks);
		for (StockInfo stockInfo : stockInfos) {
			ContentValues values = new ContentValues();
			values.put(StockProvider.KEY_STOCK_NO, stockInfo.getNo());
			values.put(StockProvider.KEY_STOCK_NAME, stockInfo.getName());
			values.put(StockProvider.KEY_STOCK_OPENING_PRICE, stockInfo.getOpening_price());
			values.put(StockProvider.KEY_STOCK_CLOSING_PRICE, stockInfo.getClosing_price());
			values.put(StockProvider.KEY_STOCK_CURRENT_PRICE, stockInfo.getCurrent_price());
			values.put(StockProvider.KEY_STOCK_MIN_PRICE, stockInfo.getMin_price());
			values.put(StockProvider.KEY_STOCK_MAX_PRICE, stockInfo.getMax_price());
			values.put(StockProvider.KEY_STOCK_BAD_NO,stockInfo.isBadNO());
			values.put(StockProvider.KEY_STOCK_CHART,stockInfo.getChart());
			cr.update(StockProvider.CONTENT_URI, values, null, null);
		}

	}

	private ArrayList<StockInfo> parseQuotesFromStream(InputStream aStream) {
		ArrayList<StockInfo> stockInfos = null;
		if (aStream != null) {
			stockInfos = new ArrayList<StockInfo>();
			BufferedInputStream iStream = new BufferedInputStream(aStream);
			InputStreamReader iReader = null;
			try {
				iReader = new InputStreamReader(iStream, "GBK");
			} catch (UnsupportedEncodingException e) {

			}
			StringBuffer strBuf = new StringBuffer();
			char[] buf = new char[BUF_SIZE];
			try {
				int charsRead;
				while ((charsRead = iReader.read(buf, 0, buf.length)) != -1) {
					strBuf.append(buf, 0, charsRead);
				}
				Pattern pattern = Pattern.compile("([a-zA-Z]{2}\\d+)=\"([^\"]*)\"");
				Matcher matcher = pattern.matcher(strBuf);

				while (matcher.find()) {
					StockInfo mStockInfo = new StockInfo();
					mStockInfo.setNo(matcher.group(1).trim());
					String result = matcher.group(2);
					if (result.trim().equals("") == false) {
						String[] data = result.split(",");
						mStockInfo.setName(data[NAME]);
						mStockInfo.setOpening_price(data[OPENING_PRICE]);
						mStockInfo.setClosing_price(data[CLOSING_PRICE]);
						mStockInfo.setCurrent_price(Double.parseDouble(data[CURRENT_PRICE]) + 0.01 * (int) (10 * Math.random()) + "");
						mStockInfo.setMax_price(data[MAX_PRICE]);
						mStockInfo.setMin_price(data[MIN_PRICE]);
						mStockInfo.setBadNO(false);
						mStockInfo.setChart(getChartFromSymbol(mStockInfo.getNo()));
					}
					stockInfos.add(mStockInfo);
				}

			} catch (Exception e) {

			}
		}
		return stockInfos;
	}

	protected ArrayList<StockInfo> getQuotesFromArray(ArrayList<String> stocks) {
		if (stocks != null && stocks.isEmpty() == false) {
			HttpClient req = new DefaultHttpClient();
			StringBuffer buf = new StringBuffer();

			int count = stocks.size();
			buf.append(QUERY_URL);
			buf.append(stocks.get(0));
			for (int i = 1; i < count; ++i) {
				buf.append(",");
				buf.append(stocks.get(i));
			}
			try {
				HttpGet httpGet = new HttpGet(buf.toString());
				HttpResponse response = req.execute(httpGet);
				InputStream iStream = response.getEntity().getContent();
				return parseQuotesFromStream(iStream);
			} catch (Exception e) {
			}
		}
		return null;
	}

	public byte[] getChartFromSymbol(String symbol) {
		try {
			StringBuilder sb = new StringBuilder(QUERY_IMG);
			sb = sb.append(symbol + ".gif");
			URL url = new URL(sb.toString());
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setDoInput(true);
			connection.connect();
			InputStream input = connection.getInputStream();
			ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
			byte[] buffer = new byte[BUF_SIZE];
			int readLength = 0;
			while ((readLength = input.read(buffer)) != -1) {
				byteBuffer.write(buffer, 0, readLength);
			}
			byteBuffer.flush();
			return byteBuffer.toByteArray();
		} catch (Exception e) {
			return null;
		}
	}

}
