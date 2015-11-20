package com.example.mystocker;

import java.text.DecimalFormat;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.support.v4.view.MotionEventCompat;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.animation.OvershootInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;

public class FocusedStockInfoCellView extends FrameLayout {
	private TextView symbolTV;
	private TextView nameTV;
	private TextView currentTV;
	private TextView percentTV;

	private View layer2;
	private View layer3;
	private ImageButton removeButton;
	private ImageButton viewButton;
	private SimpleOnGestureListener simpleOnGestureListener;
	private GestureDetector gestureDetector;
	private AnimatorSet slideLeftAS;
	private AnimatorSet slideRightAS;
	private StockInfo stockInfo = null;
	private int position;
	private CellInterface cellInterface;
	private Context mContext;
	private final int[] backgroundColor = { Color.rgb(119, 138, 170), Color.rgb(48, 92, 131) };

	public FocusedStockInfoCellView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		// TODO Auto-generated constructor stub
		initUI();
		initAnim();
		mContext = context;
	}

	public FocusedStockInfoCellView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		initUI();
		initAnim();
		mContext = context;
	}

	public FocusedStockInfoCellView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		initUI();
		initAnim();
		mContext = context;
	}

	public void setCellInterface(CellInterface cellInterface) {
		this.cellInterface = cellInterface;
	}

	private void initUI() {
		LayoutInflater inflater = LayoutInflater.from(getContext());
		inflater.inflate(R.layout.quote_cell, this);
		layer2 = findViewById(R.id.layer2);
		layer3 = findViewById(R.id.layer3);
		currentTV = (TextView) findViewById(R.id.current);
		nameTV = (TextView) findViewById(R.id.name);
		percentTV = (TextView) findViewById(R.id.percent);
		symbolTV = (TextView) findViewById(R.id.symbol);
		removeButton = (ImageButton) findViewById(R.id.remove_button);
		removeButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
					App.getDataHandler().removeFocusedQuoteByIndex(position);
			}
		});

		viewButton = (ImageButton) findViewById(R.id.view_button);
		viewButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (cellInterface != null) {
					cellInterface.viewStockInfo(position);
				}
			}
		});
		simpleOnGestureListener = new SimpleOnGestureListener() {
			@Override
			public boolean onDown(MotionEvent event) {
				return true;
			}

			@Override
			public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
				// TODO Auto-generated method stub
				if (distanceX < 0) {
					if (slideRightAS != null) {
						slideRightAS.start();
						stockInfo.setSlideLeft(false);
					}
				} else {
					if (slideLeftAS != null) {
						slideLeftAS.start();
						stockInfo.setSlideLeft(true);
					}
				}
				return true;
			}
		};
		gestureDetector = new GestureDetector(getContext(), simpleOnGestureListener);
	}

	private void initAnim() {
		slideLeftAS = new AnimatorSet();
		ObjectAnimator slideLeftAnim1 = ObjectAnimator.ofFloat(layer3, "translationX", getResources().getDimension(R.dimen.slide_left_distance_layer3));
		slideLeftAnim1.setInterpolator(new OvershootInterpolator());
		ObjectAnimator slideLeftAnim2 = ObjectAnimator.ofFloat(layer2, "translationX", getResources().getDimension(R.dimen.slide_left_distance_layer2));
		slideLeftAnim2.setInterpolator(new OvershootInterpolator());
		slideLeftAS.play(slideLeftAnim1).with(slideLeftAnim2);
		slideLeftAS.setDuration(300);

		slideRightAS = new AnimatorSet();
		ObjectAnimator slideRightAnim1 = ObjectAnimator.ofFloat(layer3, "translationX", 0);
		ObjectAnimator slideRightAnim2 = ObjectAnimator.ofFloat(layer2, "translationX", 0);
		slideRightAS.play(slideRightAnim1).with(slideRightAnim2);
		slideRightAS.setDuration(300);
	}

	public void adjustPosition() {
		if (stockInfo.isSlideLeft()) {
			setPosition();
		} else {
			resetPosition();
		}
	}

	public void resetPosition() {
		layer3.setTranslationX(0);
		layer2.setTranslationX(0);
	}

	public void setPosition() {
		layer3.setTranslationX(getResources().getDimension(R.dimen.slide_left_distance_layer3));
		layer2.setTranslationX(getResources().getDimension(R.dimen.slide_left_distance_layer2));
	}

	public void setStockInfo(StockInfo stockInfo, int position) {
		this.stockInfo = stockInfo;
		this.position = position;

		symbolTV.setText(stockInfo.getNo());
		if (stockInfo.isFocused()) {
			symbolTV.setTextColor(0xffff1111);
		} else {
			symbolTV.setTextColor(0xff000000);
		}
		nameTV.setText(stockInfo.getName());
		double current = Double.parseDouble(stockInfo.getCurrent_price());
		double closing_price = Double.parseDouble(stockInfo.getClosing_price());
		DecimalFormat df = new DecimalFormat("#0.00");
		currentTV.setText(df.format(current));
		if (current > closing_price) {
			percentTV.setTextColor(0xffee3b3b);
		} else {
			percentTV.setTextColor(0xff2e8b57);
		}
		percentTV.setText(df.format(((current - closing_price) * 100 / closing_price)) + "%");
		if (stockInfo.isBadNO()) {
			this.setBackgroundColor(0xff880000);
		} else {
			this.setBackgroundColor(backgroundColor[position % 2]);
		}
		adjustPosition();
	}

	@Override
	public void onWindowFocusChanged(boolean hasWindowFocus) {
		// TODO Auto-generated method stub
		super.onWindowFocusChanged(hasWindowFocus);
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		// TODO Auto-generated method stub
		int action = MotionEventCompat.getActionMasked(ev);
		switch(action){
		case MotionEvent.ACTION_MOVE:
			return true;
		default:
			return false;
		}
	}

	interface CellInterface {
		void viewStockInfo(int position);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		gestureDetector.onTouchEvent(event);
		return true;
	}
}
