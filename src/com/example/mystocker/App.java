package com.example.mystocker;

import android.app.Application;
import android.util.Log;

public class App extends Application {
	private static DataHandler dataHandler;
    public static DataHandler getDataHandler()
    {
    	return dataHandler;
    }
	@Override
	public void onCreate() {
     super.onCreate();
     dataHandler=new DataHandler(this.getApplicationContext());
     Log.i("APPLICATION", "app oncreate");
    }
}
