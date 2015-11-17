package com.example.mystocker;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

public class StockListFragment extends Fragment {
private ListView stockListView;
private SwipeRefreshLayout swipeRefreshLayout;
private QuoteAdapter quoteAdapter;
private Handler stopRefreshHandler;

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
	
	}

	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		stopRefreshHandler=new Handler(Looper.getMainLooper());
		quoteAdapter=(QuoteAdapter)App.getDataHandler().getAdatper();
		quoteAdapter.setActivityContext(getActivity());
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view=inflater.inflate(R.layout.stocklist_frag,container,false);
		stockListView=(ListView)view.findViewById(R.id.stocklist);
		stockListView.setEmptyView(view.findViewById(R.id.stocklistempty));
		stockListView.setAdapter(quoteAdapter);
		swipeRefreshLayout=(SwipeRefreshLayout)view.findViewById(R.id.swipeContainer);
		swipeRefreshLayout.setColorSchemeColors(0xff5c87eb);
		swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
			
			@Override
			public void onRefresh() {
				// TODO Auto-generated method stub
	            swipeRefreshLayout.setRefreshing(true);
	            App.getDataHandler().refreshStocks();
	            stopRefreshHandler.postDelayed(new Runnable(){
					@Override
					public void run() {
						// TODO Auto-generated method stub
						swipeRefreshLayout.setRefreshing(false);
					}
	            }, 1500);
			}
		});
		
		swipeRefreshLayout.post(new Runnable(){

			@Override
			public void run() {
				// TODO Auto-generated method stub
				swipeRefreshLayout.setRefreshing(true);
				App.getDataHandler().refreshStocks();
				stopRefreshHandler.postDelayed(new Runnable() {
					
					@Override
					public void run() {
						// TODO Auto-generated method stub
						swipeRefreshLayout.setRefreshing(false);
					}
				}, 1500);
			}
		});
		
		return view;
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		App.getDataHandler().saveStockToFile();
		super.onDestroy();
	}

	@Override
	public void onDestroyView() {
		// TODO Auto-generated method stub
		super.onDestroyView();
	}

	@Override
	public void onDetach() {
		// TODO Auto-generated method stub
		super.onDetach();
	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		// TODO Auto-generated method stub
		super.onSaveInstanceState(outState);
	}

	@Override
	public void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
	}

	@Override
	public void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
	}

}
