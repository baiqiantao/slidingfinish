package com.bqt.demo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;

/**
 * 想要实现向右滑动删除Activity效果只需要继承SwipeBackActivity即可
 */
public class SwipeBackActivity extends Activity {
	protected SwipeBackLayout rootLayout;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);//可以放在super.onCreate之后，但必须放在attachToActivityAndAsRootLayout之前
		super.onCreate(savedInstanceState);
		rootLayout = new SwipeBackLayout(this);
		rootLayout.attachToActivityAndAsRootLayout(this);
	}

	@Override
	public void startActivity(Intent intent) {
		super.startActivity(intent);
		overridePendingTransition(R.anim.base_slide_right_in, R.anim.base_slide_remain);
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		overridePendingTransition(0, R.anim.base_slide_right_out);
	}

	@Override
	public void finish() {
		super.finish();
		overridePendingTransition(0, R.anim.base_slide_right_out);
	}
}