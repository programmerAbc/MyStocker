package com.example.mystocker;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

public class FocusedStockListFragment extends Fragment {
	private ListView stockListView;
	private SwipeRefreshLayout swipeRefreshLayout;
	private FocusQuoteAdapter fquoteAdapter;
	private Handler stopRefreshHandler;
    private FocusedStockInfoCellView.CellInterface cellInterface;
	
    public FocusedStockListFragment(FocusedStockInfoCellView.CellInterface cellInterface) {
		// TODO Auto-generated constructor stub
    	this.cellInterface=cellInterface;
	}
    
    @Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		stopRefreshHandler = new Handler(Looper.getMainLooper());
		fquoteAdapter = (FocusQuoteAdapter) App.getDataHandler().getFocusedAdapter();
		fquoteAdapter.setCellInterface(cellInterface);
		fquoteAdapter.setActivityContext(getActivity());    
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.stocklist_frag, container, false);
		stockListView = (ListView) view.findViewById(R.id.stocklist);
		stockListView.setEmptyView(view.findViewById(R.id.stocklistempty));
		stockListView.setAdapter(fquoteAdapter);
		swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeContainer);
		swipeRefreshLayout.setColorSchemeColors(0xff5c87eb);
		swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

			@Override
			public void onRefresh() {
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

		swipeRefreshLayout.post(new Runnable() {

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
}
