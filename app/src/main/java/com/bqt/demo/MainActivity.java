package com.bqt.demo;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends ListActivity {

	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		String[] array = { "普通的Activity", //
				"普通的Activity：不显示阴影",//
				"普通的Activity：自定义阴影", //
				"普通的Activity：背景透明",//
				"普通的Activity：Translucent主题",//
				"普通的Activity：Holo_Light主题",//
				"有ListView的Activity", //
				"当touch位置有ViewPager，但ViewPager不是在item0时，不拦截滑动事件",//
				"当touch位置有ViewPager，即使ViewPager不是在item0时，也拦截【从屏幕边缘down】的滑动事件" };//我感觉这种方式的用户体验更好
		setListAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, new ArrayList<String>(Arrays.asList(array))));
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		if (position <= 5) {
			Intent intent = new Intent(this, NormalActivity.class);
			intent.putExtra("position", position);
			startActivity(intent);
		} else if (position == 6) startActivity(new Intent(MainActivity.this, ListViewActivity.class));
		else {
			Intent intent = new Intent(this, ViewPagerActivity.class);
			intent.putExtra("position", position);
			startActivity(intent);
		}
		overridePendingTransition(R.anim.base_slide_right_in, R.anim.base_slide_remain);
	}
}