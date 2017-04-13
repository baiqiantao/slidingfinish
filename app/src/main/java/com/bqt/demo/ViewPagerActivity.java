package com.bqt.demo;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class ViewPagerActivity extends SwipeBackActivity {
	private List<View> list = new ArrayList<View>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ViewPager viewPager = new ViewPager(this);

		for (int i = 0; i < 5; i++) {
			TextView tv_info = new TextView(this);
			tv_info.setTextColor(Color.RED);
			tv_info.setTextSize(TypedValue.COMPLEX_UNIT_SP, 300);
			tv_info.setBackgroundColor(Color.GREEN);//必须设置背景色，否则是透明的
			tv_info.setGravity(Gravity.CENTER);
			tv_info.setText("" + i);
			list.add(tv_info);
		}

		viewPager.setAdapter(new Adapter(this, list));
		setContentView(viewPager);
		if (getIntent().getIntExtra("position", 0) == 8) rootLayout.setInterceptWhenTouchViewPagerIfNotFirst(true);
	}

	public class Adapter extends PagerAdapter {
		private List<View> list;

		public Adapter(Context context, List<View> list) {
			this.list = list;
		}

		@Override
		public int getCount() {
			return list.size();
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView(list.get(position));
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			View v = list.get(position);
			container.addView(v);
			return v;
		}
	}
}