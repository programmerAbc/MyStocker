package com.example.mystocker;

import java.util.ArrayList;



import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

public class MainActivity extends FragmentActivity implements OnSharedPreferenceChangeListener {
	private MyViewPager mPager;
	private SlideScreenPagerAdapter mPagerAdapter;
	Context mContext;
	private static final String PrefAutoUpdate = "PREF_AUTO_UPDATE";
	private static final String PrefUpdateFreq = "PREF_UPDATE_FREQ";
	private double updateFreqMin = 0;
	private boolean autoUpdate = false;
	private GridView indicatorGridView;
	private ImageView indicatorImg;
    private static final String TAG="LifeCycle";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.i(TAG,"onCreate");
		setContentView(R.layout.activity_main);
		mContext = this;
	    mPager=(MyViewPager)findViewById(R.id.pager);
	    mPagerAdapter=new SlideScreenPagerAdapter(getSupportFragmentManager());
	    mPager.setAdapter(mPagerAdapter);
	    //问题出在这里，可能是因为DepthPageTransformer的变换导致本该消失的page以不可见的形式浮于当前显示的页面的上方拦截了触摸事件
	    mPager.setPageTransformer(true, new DepthPageTransformer());
	    
	    indicatorGridView=(GridView)findViewById(R.id.indicatorGrid);
	    indicatorImg=(ImageView)findViewById(R.id.indicatorImg);
	    indicatorGridView.setAdapter(new IndicatorGridViewAdapter(this));
	    indicatorGridView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				mPager.setCurrentItem(arg2, true);
			}
	    });
	
	    
	    mPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
			
			@Override
			public void onPageSelected(int index) {
				// TODO Auto-generated method stub
				ObjectAnimator.ofFloat(indicatorImg,"x",indicatorGridView.getWidth()/getResources().getInteger(R.integer.page_num)*index).start();
			}
			
			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onPageScrollStateChanged(int arg0) {
				// TODO Auto-generated method stub
				
			}
		});
     	PreferenceManager.getDefaultSharedPreferences(mContext).registerOnSharedPreferenceChangeListener(this);
	}

	private static final int SHOW_PREFERENCES = 1;

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		super.onCreateOptionsMenu(menu);
		getMenuInflater().inflate(R.menu.main, menu);
		MenuItem menuItem;
		menuItem=menu.findItem(R.id.action_add);
		View view=menuItem.getActionView();
		final AutoCompleteTextView acTextView=(AutoCompleteTextView)view.findViewById(R.id.action_add_actextview);
		acTextView.setAdapter(App.getDataHandler().getSuggestionAdatper());
		acTextView.setThreshold(1);
		ImageButton imageButton=(ImageButton)view.findViewById(R.id.action_add_imagebutton);
		imageButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				addSymbol(acTextView.getText().toString().trim());
				acTextView.setText(null);
				App.getDataHandler().refreshStocks();
			}
		});
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		switch (id) {
		case R.id.settings: {
			startActivityForResult(new Intent(this, FragmentPreferences.class), SHOW_PREFERENCES);
			return true;
		}
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		App.getDataHandler().saveStockToFile();
		
		super.onDestroy();
		Log.i(TAG,"onDestory");
	}

	@Override
	protected void onResume() {
		super.onResume();
		Log.i(TAG,"onResume");

	}

	@Override
	protected void onStop() {
		App.getDataHandler().unregisterAutoUpdate();
		super.onStop();
		Log.i(TAG,"onStop");
	}


	private void addSymbol(String str) {
		if (str.equals("")) {
			Toast.makeText(mContext, "请输入股票代码", Toast.LENGTH_SHORT).show();
			return;
		}
		String symbolArray[] = str.split("\n");
		int count = symbolArray.length;
		ArrayList<String> symbolList = new ArrayList<String>();
		for (int i = 0; i < count; ++i) {
			symbolList.add(symbolArray[i].trim());
		}
		App.getDataHandler().addSymbols(symbolList);
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
		// TODO Auto-generated method stub
		if (key.equals(PrefAutoUpdate)) {
			autoUpdate = sharedPreferences.getBoolean(key, false);
			if (autoUpdate) {
				App.getDataHandler().registerAutoUpdate((long) (updateFreqMin * 1000));
			} else {
				App.getDataHandler().unregisterAutoUpdate();
			}
		} else if (key.equals(PrefUpdateFreq)) {
			updateFreqMin = Double.parseDouble(sharedPreferences.getString(key, "60"));
		}
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		Log.i(TAG,"onPause");
	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		Log.i(TAG,"onStart");
		if (PreferenceManager.getDefaultSharedPreferences(mContext).contains(PrefAutoUpdate) == false) {
			SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(mContext).edit();
			editor.putBoolean(PrefAutoUpdate, false);
			editor.apply();
			autoUpdate = false;
		} else {
			autoUpdate = PreferenceManager.getDefaultSharedPreferences(mContext).getBoolean(PrefAutoUpdate, false);
		}

		if (PreferenceManager.getDefaultSharedPreferences(mContext).contains(PrefUpdateFreq) == false) {
			SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(mContext).edit();
			editor.putString(PrefUpdateFreq, "60");
			editor.apply();
			updateFreqMin = 60;
		} else {
			updateFreqMin = Double.parseDouble(PreferenceManager.getDefaultSharedPreferences(mContext).getString(PrefUpdateFreq, "60"));
		}

		if (autoUpdate) {
			App.getDataHandler().registerAutoUpdate((long) (updateFreqMin * 1000));
		} else {
			App.getDataHandler().unregisterAutoUpdate();
		}
		
		
	}


	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		// TODO Auto-generated method stub
		super.onWindowFocusChanged(hasFocus);
		ViewGroup.LayoutParams layoutParams=indicatorImg.getLayoutParams();
		layoutParams.width=indicatorGridView.getWidth()/getResources().getInteger(R.integer.page_num);
		indicatorImg.setLayoutParams(layoutParams);
	}

}
