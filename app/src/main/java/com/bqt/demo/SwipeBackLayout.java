package com.bqt.demo;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.Scroller;

import java.util.LinkedList;
import java.util.List;

public class SwipeBackLayout extends FrameLayout {
	//常量
	/**手指向右滑动时的最小【滑动】距离（只有滑动超过此距离才滚动view）*/
	public static int X_MIN_DISTANCE_IF_MOVE = 8;
	/**手指向右滑动时的最大【起始】距离（防止误操作，只有从左边缘滑动才有效），这个值最好大于上面的值*/
	public static int X_MIN_START_DISTANCE = 10;
	/**手指向右滑动时的最小【滑动】距离（防止误操作，只有滑动超过一定距离才关闭）*/
	public static int X_MIN_DISTANCE_FROM_LEFT = 90;
	/**自动滚动到左侧(回到初始位置)消耗的时间参数*/
	public static final float TIME_MOVE_TO_LEFT = 1.5f;
	/**自动滚动到右侧(关闭应用前)消耗的时间参数*/
	public static final float TIME_MOVE_TO_RIGHT = 0.5f;

	//一些可以设置的成员
	/**滑动时左边缘是否显示阴影*/
	private boolean isShowShadow = true;
	/**当touch位置有ViewPager，但是ViewPager不是在item0时，是否拦截【从屏幕边缘down】的滑动事件*/
	private boolean isInterceptWhenTouchViewPagerIfNotFirst = false;
	/**滑动时左边缘添加阴影*/
	private Drawable mShadowDrawable = getResources().getDrawable(R.drawable.shadow_left);

	//临时变量
	/**是否要finish掉Activity*/
	private boolean isFinish;
	private Activity mActivity;
	/**记录按下时的触摸点、移动时的触摸点在屏幕上的X坐标*/
	private int downX, touchX;
	private Scroller mScroller = new Scroller(getContext());
	private List<ViewPager> mViewPagers = new LinkedList<ViewPager>();

	public SwipeBackLayout(Context context) {
		this(context, null);
	}

	public SwipeBackLayout(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public SwipeBackLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		DisplayMetrics metric = new DisplayMetrics();
		((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getMetrics(metric);
		X_MIN_DISTANCE_FROM_LEFT = (int) (metric.widthPixels * 0.2f);//屏幕宽的1/5
		//滑动的时候，手的移动大于这个距离才开始移动控件；如果小于这个距离就不触发移动控件。ViewPage就是用这个距离来判断用户是否翻页的
		X_MIN_DISTANCE_IF_MOVE = ViewConfiguration.get(context).getScaledTouchSlop();
		Log.i("bqt", X_MIN_DISTANCE_FROM_LEFT + "---" + X_MIN_DISTANCE_IF_MOVE);//144-16
		X_MIN_START_DISTANCE = X_MIN_DISTANCE_IF_MOVE + 2;//这个值最好大于X_MIN_DISTANCE_IF_MOVE
	}

	//必须手动调用的方法
	public void attachToActivityAndAsRootLayout(Activity activity) {
		mActivity = activity;
		FrameLayout decorView = (FrameLayout) activity.getWindow().getDecorView();//所有窗口的根View
		ViewGroup decorChild = (ViewGroup) decorView.getChildAt(0);//封装内容区域和ActionBar区域的容器
		decorView.removeView(decorChild);
		this.addView(decorChild);
		decorView.addView(this);
	}

	//******************************************************************************************
	@Override
	public boolean onInterceptTouchEvent(MotionEvent event) {
		if (!isInterceptWhenTouchViewPagerIfNotFirst) {
			ViewPager mViewPager = getMyTouchViewPager(mViewPagers, event);
			//如果存在ViewPager并且ViewPager不是处在第一个Item，我们才拦截Touch事件，否则不拦截（Touch事件由ViewPager处理）
			if (mViewPager != null && mViewPager.getCurrentItem() != 0) return super.onInterceptTouchEvent(event);
		}

		switch (event.getActionMasked()) {
			case MotionEvent.ACTION_DOWN:
				downX = (int) event.getRawX();//getRawX获取的是相对父View(也即整个屏幕左上角)的位置坐标
				touchX = downX;
				break;
			case MotionEvent.ACTION_MOVE:
				if (downX <= X_MIN_START_DISTANCE //不是从屏幕左边缘开始的不拦截
						&& event.getRawX() - downX > X_MIN_DISTANCE_IF_MOVE) return true;//滑动距离太小时暂不拦截
				break;
		}
		return super.onInterceptTouchEvent(event);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		switch (event.getActionMasked()) {
			case MotionEvent.ACTION_MOVE:
				if (event.getRawX() - downX > X_MIN_DISTANCE_IF_MOVE) scrollBy(touchX - (int) event.getRawX(), 0);//将View中的内容滚动指定距离
				touchX = (int) event.getRawX();
				break;
			case MotionEvent.ACTION_UP:
				if (Math.abs(getScrollX()) >= X_MIN_DISTANCE_FROM_LEFT) scrollRightOrLeft(true);//当滑动的距离大于我们设定的最小距离时，滑到右侧
				else scrollRightOrLeft(false);//当滑动的距离小于我们设定的最小距离时，回到起始位置
				break;
		}
		return true;
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		super.onLayout(changed, l, t, r, b);
		if (changed) getAlLViewPager(mViewPagers, this);//【递归】遍历整个View树，获取里面的ViewPager的集合
	}

	@Override
	protected void dispatchDraw(Canvas canvas) {//调用View.onDraw为绘制VIew本身，调用dispatchDraw为绘制自己的孩子
		//Called by draw to draw the child views. This may be overridden by derived classes to gain control just before its children are drawn
		super.dispatchDraw(canvas);
		if (isShowShadow && mShadowDrawable != null) {
			int left = getLeft() - mShadowDrawable.getIntrinsicWidth();
			int right = left + mShadowDrawable.getIntrinsicWidth();
			mShadowDrawable.setBounds(left, getTop(), right, getBottom());
			mShadowDrawable.draw(canvas);
		}
	}

	@Override
	public void computeScroll() {
		if (mScroller.computeScrollOffset()) {
			scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
			postInvalidate();
			if (mScroller.isFinished() && isFinish) mActivity.finish();
		}
	}

	//******************************************************************************************
	/**
	 * 【递归】遍历整个View树，获取里面的ViewPager的集合
	 */
	private void getAlLViewPager(List<ViewPager> mViewPagers, ViewGroup parent) {
		int childCount = parent.getChildCount();
		for (int i = 0; i < childCount; i++) {
			View child = parent.getChildAt(i);
			if (child instanceof ViewPager) mViewPagers.add((ViewPager) child);
			else if (child instanceof ViewGroup) getAlLViewPager(mViewPagers, (ViewGroup) child);
		}
	}

	/**
	 * 返回我们touch范围内的那个ViewPager
	 */
	private ViewPager getMyTouchViewPager(List<ViewPager> mViewPagers, MotionEvent ev) {
		if (mViewPagers == null || mViewPagers.size() == 0) return null;
		Rect mRect = new Rect();
		for (ViewPager viewPager : mViewPagers) {
			viewPager.getHitRect(mRect);
			if (mRect.contains((int) ev.getX(), (int) ev.getY())) return viewPager;
		}
		return null;
	}

	/**
	 * 滚动出界面或滚动到起始位置
	 */
	private void scrollRightOrLeft(boolean toRight) {
		isFinish = toRight;
		float density = getResources().getDisplayMetrics().density;
		if (toRight) {
			int duration = (int) (TIME_MOVE_TO_RIGHT * (getWidth() + getScrollX()) / density);
			mScroller.startScroll(getScrollX(), 0, -getWidth() - getScrollX(), 0, duration);// startX,  startY,  dx,  dy,  duration
		} else {
			int duration = (int) (TIME_MOVE_TO_LEFT * getScrollX() / density);
			mScroller.startScroll(getScrollX(), 0, -getScrollX(), 0, duration);
		}
		postInvalidate();//刷新界面
	}

	//get和set方法******************************************************************************************
	public boolean isInterceptWhenTouchViewPagerIfNotFirst() {
		return isInterceptWhenTouchViewPagerIfNotFirst;
	}

	public void setInterceptWhenTouchViewPagerIfNotFirst(boolean isInterceptWhenTouchViewPagerIfNotFirst) {
		this.isInterceptWhenTouchViewPagerIfNotFirst = isInterceptWhenTouchViewPagerIfNotFirst;
	}

	public Drawable getmShadowDrawable() {
		return mShadowDrawable;
	}

	public void setmShadowDrawable(Drawable mShadowDrawable) {
		this.mShadowDrawable = mShadowDrawable;
	}

	public boolean isShowShadow() {
		return isShowShadow;
	}

	public void setShowShadow(boolean isShowShadow) {
		this.isShowShadow = isShowShadow;
	}
}