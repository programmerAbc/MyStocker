package com.example.mystocker;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class IndicatorGridViewAdapter extends BaseAdapter{
private static final String indicatorStrs[]={"È«²¿","¹Ø×¢"};
private Context context;
public IndicatorGridViewAdapter(Context context) {
	// TODO Auto-generated constructor stub
	this.context=context;
}	

@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return indicatorStrs.length;
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return indicatorStrs[position];
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		View view=convertView;
		if(view!=null)
		{
			
			TextView tv=(TextView)view.findViewById(R.id.indicatorTV);
			tv.setText(indicatorStrs[position]);
		}
		else
		{
			view=LayoutInflater.from(context).inflate(R.layout.indicator_item,parent,false);
		    TextView tv=(TextView)view.findViewById(R.id.indicatorTV);
		    tv.setText(indicatorStrs[position]);
		}
		return view;
	}
}
