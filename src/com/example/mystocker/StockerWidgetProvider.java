package com.example.mystocker;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.widget.RemoteViews;

public class StockerWidgetProvider extends AppWidgetProvider {

	final static int PendingIntentNextID=1234;
	final static int PendingIntentPreviousID=1235;
    final static String IntentNextExtraKey="NEXT";
	public static int[] StockAppWidgetIds=null;
    private boolean next=true;
	
	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		next=intent.getBooleanExtra(IntentNextExtraKey,true);
		super.onReceive(context, intent);
	}
	
	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
		// TODO Auto-generated method stub
		super.onUpdate(context, appWidgetManager, appWidgetIds);
		StockAppWidgetIds=appWidgetIds;
		int appWidgetIdsLength = appWidgetIds.length;
		StockInfo stockInfo = null;
		if (next) {
			stockInfo = App.getDataHandler().focusedNext();
		} else {
			stockInfo = App.getDataHandler().focusedPrevious();
		}

		for (int i = 0; i < appWidgetIdsLength; ++i) {
			RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widgetlayout);
            remoteViews.setTextViewText(R.id.widgetStockNameTV, stockInfo.getName());
            remoteViews.setImageViewBitmap(R.id.widgetStockChartIV, BitmapFactory.decodeByteArray(stockInfo.chart,0,stockInfo.chart.length));		
		    
            Intent intentNext=new Intent(context,StockerWidgetProvider.class);
		    intentNext.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
		    intentNext.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds);
		    intentNext.putExtra(IntentNextExtraKey, true);
		    PendingIntent pendingIntentNext=PendingIntent.getBroadcast(context,PendingIntentNextID,intentNext, PendingIntent.FLAG_UPDATE_CURRENT);
		    
		    Intent intentPrevious=new Intent(context,StockerWidgetProvider.class);
		    intentPrevious.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
		    intentPrevious.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds);
		    intentNext.putExtra(IntentNextExtraKey, false);
		    PendingIntent pendingIntentPrevious=PendingIntent.getBroadcast(context, PendingIntentPreviousID, intentPrevious, PendingIntent.FLAG_UPDATE_CURRENT);
		    
		    remoteViews.setOnClickPendingIntent(R.id.widgetPreviousButton,pendingIntentPrevious);
		    remoteViews.setOnClickPendingIntent(R.id.widgetNextButton, pendingIntentNext);
		    appWidgetManager.updateAppWidget(appWidgetIds[i], remoteViews);
		}
	}
}
