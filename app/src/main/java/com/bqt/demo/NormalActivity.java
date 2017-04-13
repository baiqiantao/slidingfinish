package com.bqt.demo;

import android.app.AlertDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.Window;
import android.widget.TextView;

public class NormalActivity extends SwipeBackActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		//如果子类要requestWindowFeature，必须放在super.onCreate之前，因为父类SwipeBackActivity的onCreate方法已经获取到了DecorView
		//也即子类在调用super.onCreate后，其窗口装饰风格(即相应的根布局文件)已经确定好了(默认的)，如：是否有标题、是否有icon等。
		//由于要求窗口装饰风格一经确定就不能再修改，否则直接抛异常！而requestWindowFeature的作用就是根据你的设置选择匹配的窗口装饰风格
		//所以requestWindowFeature必须在包括setContentView以及getWindow().getDecorView()等行为之前调用！
		//注意：以上规则只适合requestWindowFeature等方法，全屏设置可以放在【任何】位置，比如在点击某个View后调用也是可以的
		requestWindowFeature(Window.FEATURE_LEFT_ICON);//实际上这行代码没任何意义，因为会被super.onCreate中的设置覆盖掉
		super.onCreate(savedInstanceState);

		TextView tv_info = new TextView(this);
		tv_info.setTextColor(Color.BLACK);
		tv_info.setTextSize(TypedValue.COMPLEX_UNIT_SP, 25);
		tv_info.setBackgroundColor(Color.RED);//必须设置背景色，否则是透明的
		tv_info.setGravity(Gravity.CENTER);

		switch (getIntent().getIntExtra("position", 0)) {
			case 0:
				tv_info.setText("默认显示指定的阴影\n对于普通的Activity，不必一定要在屏幕边缘开始滑才能退出");
				break;
			case 1:
				tv_info.setText("不显示阴影");
				rootLayout.setShowShadow(false);
				break;
			case 2:
				tv_info.setText("自定义阴影");
				rootLayout.setmShadowDrawable(getResources().getDrawable(R.drawable.ic_launcher));
				break;
			case 3:
				tv_info.setText("背景透明");
				tv_info.setBackgroundColor(Color.TRANSPARENT);
				break;
			case 4:
				tv_info.setText("Translucent主题");
				new AlertDialog.Builder(this).setTitle("Translucent主题").create().show();
				break;
			case 5:
				tv_info.setText("Holo_Light主题");
				setTheme(android.R.style.Theme_Holo_Light);
				new AlertDialog.Builder(this).setTitle("Holo_Light主题").create().show();
				break;
		}
		setContentView(tv_info);
	}
}