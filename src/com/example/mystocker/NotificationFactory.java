package com.example.mystocker;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.widget.RemoteViews;
public class NotificationFactory {
public static final int NOTIFICATION_ID=12345;
private static Notification notification=null;	
public static void Notify(Context context,StockInfo stockInfo)
	{
	   if(notification==null)
	   {
		   Notification.Builder builder=new Notification.Builder(context);
			builder.setSmallIcon(R.drawable.ic_launcher)
			       .setTicker("¹ÉÆ±¸üÐÂ")
			       .setWhen(System.currentTimeMillis())
			       .setDefaults(Notification.DEFAULT_SOUND|Notification.DEFAULT_LIGHTS)
			       .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
			       .setVibrate(new long[]{1000,1000,1000,1000,1000})
			       .setLights(Color.RED, 1, 1)
			       .setContent(new RemoteViews(context.getPackageName(),R.layout.notification_remoteview_small))
			       .setOnlyAlertOnce(false); 
			notification=builder.getNotification();
			notification.bigContentView=new RemoteViews(context.getPackageName(),R.layout.notification_remoteview);
	   }
	   notification.contentView.setTextViewText(R.id.stockname_rtv_small,stockInfo.getName());
	   notification.bigContentView.setTextViewText(R.id.stockname_rtv, stockInfo.getName());
	   notification.bigContentView.setImageViewBitmap(R.id.stockchart_riv,BitmapFactory.decodeByteArray(stockInfo.chart, 0, stockInfo.chart.length));
	   NotificationManager nm=(NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
	   nm.notify(NOTIFICATION_ID, notification);
	}
}
