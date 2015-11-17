package com.example.mystocker;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
public class SlideScreenPagerAdapter extends FragmentStatePagerAdapter{
public static final int PAGE_NUM=2;
private StockListFragment stockListFragment;
private FocusedStockListFragment focusStockListFragment;
	public SlideScreenPagerAdapter(FragmentManager fm) {
		super(fm);
		stockListFragment=new StockListFragment();
		focusStockListFragment=new FocusedStockListFragment();
	}

	@Override
	public Fragment getItem(int position) {
		// TODO Auto-generated method stub
		switch(position)
		{
		case 0:
			return stockListFragment;
		case 1:
			return focusStockListFragment;
		default:return null;
		}
		
	}

	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return PAGE_NUM;
	}

}
