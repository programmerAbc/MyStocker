package com.example.mystocker;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;
import android.widget.Toast;

public class DataHandler {
	private static final String TAG = "DataHandler";
	private static final String QUERY_URL = "http://hq.sinajs.cn/list=";
	private static final String QUERY_IMG = "http://image.sinajs.cn/newchart/daily/n/";
	private int BUF_SIZE = 16384;
	private ArrayList<StockInfo> stockInfos = null;
	private final int NAME = 0;
	private final int OPENING_PRICE = 1;
	private final int CLOSING_PRICE = 2;
	private final int CURRENT_PRICE = 3;
	private final int MAX_PRICE = 4;
	private final int MIN_PRICE = 5;
	Context context;
	StockDatabase stockDatabase;

	public DataHandler(Context mContext) {
		context = mContext;
		stockDatabase = new StockDatabase(mContext, StockDatabase.DATABASE_NAME, null, StockDatabase.DATABASE_VERSION);
		stockInfos=stockDatabase.selectStock();
        if(stockInfos==null)
        {
        	stockInfos=new ArrayList<StockInfo>();
        }
        else
        {
        	refreshStocks();		
        }
	}

	public synchronized void addSymbols(ArrayList<String> stockList) {
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
						StockInfo tempSinfo=new StockInfo();
						tempSinfo.setNo(newSymbol);
						newStockInfos.add(tempSinfo);
					}
				}
				if (newStockInfos.isEmpty()==false) {
					stockInfos.addAll(newStockInfos);
					Toast.makeText(context, "股票添加成功", Toast.LENGTH_SHORT).show();
				}
				else
				{
					Toast.makeText(context, "股票添加失败", Toast.LENGTH_SHORT).show();
				}
			}
		}
	}



	public void saveStockToFile() {
			stockDatabase.insertStocks(stockInfos);
		
	}

	private boolean parseQuotesFromStream(InputStream aStream) {
		boolean finishedParse = false;
		if (aStream != null) {
			
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
				
				for(StockInfo sinfo:stockInfos)
				{
					sinfo.reset();					
				}
				
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
					}
					for(StockInfo sinfo:stockInfos){
						if(sinfo.equals(mStockInfo)){
							sinfo.copyFrom(mStockInfo);
						}
					}
				}
				
				finishedParse = true;
				
			} catch (Exception e) {
				
				finishedParse = false;
				
			}
		}
		return finishedParse;
	}

	protected ArrayList<StockInfo> getQuotesFromArray(ArrayList<StockInfo> stockSymbols) {
		if (stockSymbols != null && stockSymbols.isEmpty()==false) {
			HttpClient req = new DefaultHttpClient();
			StringBuffer buf = new StringBuffer();

			int count = stockSymbols.size();
			buf.append(QUERY_URL);
			buf.append(stockSymbols.get(0).getNo());
			for (int i = 1; i < count; ++i) {
				buf.append(",");
				buf.append(stockSymbols.get(i).getNo());
			}
			try {
				HttpGet httpGet = new HttpGet(buf.toString());
				HttpResponse response = req.execute(httpGet);
				InputStream iStream = response.getEntity().getContent();
				parseQuotesFromStream(iStream);
				return stockInfos;
			} catch (Exception e) {
			}
		}
		return null;
	}

	public Bitmap getChartFromSymbol(String symbol) {
		try {
			StringBuilder sb = new StringBuilder(QUERY_IMG);
			sb = sb.append(symbol + ".gif");
			HttpClient req = new DefaultHttpClient();
			HttpGet httpGet = new HttpGet(sb.toString());
			HttpResponse response = req.execute(httpGet);
			InputStream iStream;
			BitmapDrawable bitmapDrawable;
			iStream = response.getEntity().getContent();
			bitmapDrawable = new BitmapDrawable(iStream);
			iStream.close();
			iStream = null;
			return bitmapDrawable.getBitmap();
		} catch (Exception e) {
		}
		return null;
	}

	public void refreshStocks() {
		getQuotesFromArray(stockInfos);
	}

	public int stocksSize() {
		if (stockInfos != null) {
			return stockInfos.size();
		} else {
			return 0;
		}
	}

	public void removeQuoteByIndex(int index) {
		stockInfos.remove(index);
	}

	public StockInfo getQuoteFromIndex(int index) {
		return stockInfos.get(index);
	}
}
