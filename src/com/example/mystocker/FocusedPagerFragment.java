package com.example.mystocker;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class FocusedPagerFragment extends Fragment implements FocusedStockInfoCellView.CellInterface,FocusedStockDetailFragment.StockDetailFragmentButtonClickListener{
	FocusedStockListFragment fslf;
	private static final String SLF_TAG="FSLF";
	private static final String SDF_TAG="FSDF";
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		fslf=new FocusedStockListFragment(this);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view=inflater.inflate(R.layout.pager_frag, container,false);
		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		 FragmentManager fm=getChildFragmentManager();   
		 FragmentTransaction ft=fm.beginTransaction();
		 ft.add(R.id.frag_container,fslf,SLF_TAG);
		 ft.commit();
	}

	@Override
	public void onFocusClicked() {
		// TODO Auto-generated method stub
		FragmentManager fm=getChildFragmentManager();
		FragmentTransaction ft=fm.beginTransaction();
		ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE);
		Fragment frag=fm.findFragmentByTag(SDF_TAG);
		if(frag!=null){
		ft.remove(frag);
		}
		ft.commit();
	}

	@Override
	public void onCloseClicked() {
		// TODO Auto-generated method stub
		FragmentManager fm=getChildFragmentManager();
		FragmentTransaction ft=fm.beginTransaction();
		ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE);
		Fragment frag=fm.findFragmentByTag(SDF_TAG);
		if(frag!=null){
		ft.remove(frag);
		}
		ft.commit();
	}

	@Override
	public void viewStockInfo(int position) {
		// TODO Auto-generated method stub
        FocusedStockDetailFragment fsdf=new FocusedStockDetailFragment(getActivity(),this);
        fsdf.setStockInfo(position);
		FragmentManager fm=getChildFragmentManager();
		FragmentTransaction ft=fm.beginTransaction();
		ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
		ft.addToBackStack("back");
		ft.add(R.id.frag_container,fsdf,SDF_TAG);
		ft.commit();
	}
}
