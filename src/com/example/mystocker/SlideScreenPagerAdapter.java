package com.example.mystocker;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
public class SlideScreenPagerAdapter extends FragmentStatePagerAdapter{
public static final int PAGE_NUM=2;
private PagerFragment pagerFragment;
private FocusedPagerFragment fpagerFragment;
	public SlideScreenPagerAdapter(FragmentManager fm) {
		super(fm);
		pagerFragment=new PagerFragment();
		fpagerFragment=new FocusedPagerFragment();
	}

	@Override
	public Fragment getItem(int position) {
		// TODO Auto-generated method stub
		switch(position)
		{
		case 0:
			return pagerFragment;
		case 1:
			return fpagerFragment;
		default:return null;
		}
		
	}

	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return PAGE_NUM;
	}

}
