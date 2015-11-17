package com.example.mystocker;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
public class SlideScreenPagerAdapter extends FragmentStatePagerAdapter{
public static final int PAGE_NUM=2;
private StockListFragment stockListFragment;

	public SlideScreenPagerAdapter(FragmentManager fm) {
		super(fm);
		stockListFragment=new StockListFragment();
		// TODO Auto-generated constructor stub
	}

	@Override
	public Fragment getItem(int position) {
		// TODO Auto-generated method stub
//		switch(position)
//		{
//		case 0:
//			return stockListFragment;
//		default:return null;
//		}
		return new StockListFragment();
	}

	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return PAGE_NUM;
	}

}
