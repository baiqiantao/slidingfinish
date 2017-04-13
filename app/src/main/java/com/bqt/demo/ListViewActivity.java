package com.bqt.demo;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class ListViewActivity extends SwipeBackActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		List<String> list = new ArrayList<String>();
		for (int i = 0; i <= 30; i++) {
			list.add("包含ListView时不会有手势冲突");
		}
		ListView mListView = new ListView(this);
		mListView.setBackgroundColor(Color.GREEN);//设置背景色
		mListView.setAdapter(new ArrayAdapter<String>(ListViewActivity.this, android.R.layout.simple_list_item_1, list));
		setContentView(mListView);
	}
}