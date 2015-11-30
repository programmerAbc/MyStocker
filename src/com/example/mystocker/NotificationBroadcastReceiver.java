package com.example.mystocker;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public class NotificationBroadcastReceiver extends BroadcastReceiver{
public static final String StatusNavButtonClickedAction="com.example.mystocker.STATUS_NAV_BUTTON_CLICKED";
public static final String StatusNavButtonClickedIntentExtraKey="forward";	
@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		NotificationFactory.Nav(context, intent.getBooleanExtra(StatusNavButtonClickedIntentExtraKey, false));
	}
}
