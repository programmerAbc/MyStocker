package com.example.mystocker;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.util.Log;
import android.widget.RemoteViews;

public class NotificationFactory {
	public static final int NOTIFICATION_ID = 12345;
	private static Notification notification = null;
	private static int currentFocusedStockIndex = 0;

	public static void Notify(Context context, StockInfo stockInfo) {
		 currentFocusedStockIndex = 0;
		if (notification == null) {
			Notification.Builder builder = new Notification.Builder(context);
			builder.setSmallIcon(R.drawable.ic_launcher).setTicker("¹ÉÆ±¸üÐÂ").setWhen(System.currentTimeMillis()).setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_LIGHTS).setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)).setVibrate(new long[] { 1000, 1000, 1000, 1000, 1000 }).setLights(Color.RED, 1, 1).setContent(new RemoteViews(context.getPackageName(), R.layout.notification_remoteview_small)).setOnlyAlertOnce(false);
			notification = builder.getNotification();
			notification.bigContentView = new RemoteViews(context.getPackageName(), R.layout.notification_remoteview);
			Intent statusNavButtonClickedForwardIntent = new Intent(NotificationBroadcastReceiver.StatusNavButtonClickedAction);
			statusNavButtonClickedForwardIntent.putExtra(NotificationBroadcastReceiver.StatusNavButtonClickedIntentExtraKey, true);
			PendingIntent statusNavButtonClickedForwardPendingIntent = PendingIntent.getBroadcast(context, NOTIFICATION_ID, statusNavButtonClickedForwardIntent, PendingIntent.FLAG_UPDATE_CURRENT);
			Intent statusNavButtonClickedBackIntent = new Intent(NotificationBroadcastReceiver.StatusNavButtonClickedAction);
			statusNavButtonClickedBackIntent.putExtra(NotificationBroadcastReceiver.StatusNavButtonClickedIntentExtraKey,false);
			PendingIntent statusNavButtonClickedBackPendingIntent = PendingIntent.getBroadcast(context, NOTIFICATION_ID+1, statusNavButtonClickedBackIntent, PendingIntent.FLAG_UPDATE_CURRENT);
			notification.bigContentView.setOnClickPendingIntent(R.id.status_forwardbutton, statusNavButtonClickedForwardPendingIntent);
			notification.bigContentView.setOnClickPendingIntent(R.id.status_backbutton, statusNavButtonClickedBackPendingIntent);	
		}
		notification.contentView.setTextViewText(R.id.stockname_rtv_small, stockInfo.getName());
		notification.bigContentView.setTextViewText(R.id.stockname_rtv, stockInfo.getName());
		notification.bigContentView.setImageViewBitmap(R.id.stockchart_riv, BitmapFactory.decodeByteArray(stockInfo.chart, 0, stockInfo.chart.length));
	    NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		nm.notify(NOTIFICATION_ID, notification);
	}

	public static void Nav(Context context, boolean forward) {
		if (forward) {
			++currentFocusedStockIndex;
			if (currentFocusedStockIndex >= App.getDataHandler().focusedSize()) {
				currentFocusedStockIndex = App.getDataHandler().focusedSize() - 1;
				return;
			}
		} else {
			--currentFocusedStockIndex;
			if (currentFocusedStockIndex < 0) {
				currentFocusedStockIndex = 0;
				return;
			}
		}
		StockInfo stockInfo = App.getDataHandler().focusedGet(currentFocusedStockIndex);
		notification.contentView.setTextViewText(R.id.stockname_rtv_small, stockInfo.getName());
		notification.bigContentView.setTextViewText(R.id.stockname_rtv, stockInfo.getName());
		notification.bigContentView.setImageViewBitmap(R.id.stockchart_riv, BitmapFactory.decodeByteArray(stockInfo.chart, 0, stockInfo.chart.length));
	    NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		nm.notify(NOTIFICATION_ID, notification);
	}
}
