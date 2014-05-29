package com.xiaoyintong.app.ui;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import cn.jpush.android.api.JPushInterface;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.xiaoyintong.app.AppContext;
import com.xiaoyintong.app.AppManager;
import com.xiaoyintong.app.AppStatusService;
import com.xiaoyintong.app.R;
import com.xiaoyintong.app.api.MyResponseHandler;
import com.xiaoyintong.app.api.ResponseListener;


public class BaseActivity extends SherlockActivity implements ResponseListener{
	
	protected static final String BASE_TAG = BaseActivity.class.getSimpleName();
	
	protected MyResponseHandler responseHandler = new MyResponseHandler(AppContext.getInstance(), this);
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 添加Activity到堆栈
//		this.requestWindowFeature(Window.FEATURE_NO_TITLE);//去掉标题栏  
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setBackgroundDrawable(
				getResources().getDrawable(R.drawable.top_bg));
		
		AppStatusService.NOTIFY_ISSHOWING = false;
		
		Log.w(BASE_TAG, " onCreate()");
		Intent intent = new Intent(this, AppStatusService.class);
		startService(intent);
		AppManager.getAppManager().addActivity(this);
	}
	

	@Override
	protected void onDestroy() {
		
		super.onDestroy();
		Log.w(BASE_TAG, " onDestroy()");
		Log.w(BASE_TAG, this.getClass().toString());
		// 结束Activity&从堆栈中移除
		if (! (this instanceof Main)) {
			Log.w(BASE_TAG, "is not the Main  so remove");
			AppManager.getAppManager().finishActivity(this);
		}
	}
	
	@Override
	protected void onResume()
	{
		super.onResume();
		NotificationManager notificationManager = (
				NotificationManager) getSystemService(
						Context.NOTIFICATION_SERVICE);
		notificationManager.cancel(0);
		AppStatusService.NOTIFY_ISSHOWING = false;
		JPushInterface.onResume(this);
	}

	@Override
	protected void onPause()
	{
		super.onPause();
		JPushInterface.onPause(this);
	}
	@Override
	public void processResponse(String info) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void requestFinished(boolean isSucceed) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		return super.onCreateOptionsMenu(menu);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// If this callback does not handle the item click,
		// onPerformDefaultAction
		// of the ActionProvider is invoked. Hence, the provider encapsulates
		// the
		// complete functionality of the menu item.
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			break;

		default:
			break;
		}
		return true;
	}

	
}
