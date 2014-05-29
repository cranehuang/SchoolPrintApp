package com.xiaoyintong.app.ui;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.lang.ref.WeakReference;

import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshExpandableListView;
import com.xiaoyintong.app.AppContext;
import com.xiaoyintong.app.R;
import com.xiaoyintong.app.adapter.OrderAdapter;
import com.xiaoyintong.app.adapter.SubregionAdapter;
import com.xiaoyintong.app.api.ApiClient;
import com.xiaoyintong.app.bean.Location;
import com.xiaoyintong.app.bean.Order;
import com.xiaoyintong.app.bean.OrderUnit;
import com.xiaoyintong.app.bean.ResultForSubAgent;
import com.xiaoyintong.app.bean.Subregion;
import com.xiaoyintong.app.bean.User;
import com.xiaoyintong.app.common.JpushUtil;
import com.xiaoyintong.app.common.UIHelper;
import com.xiaoyintong.app.db.DBHelper;
import com.xiaoyintong.app.widget.BadgeView;

import de.keyboardsurfer.android.widget.crouton.Configuration;
import de.keyboardsurfer.android.widget.crouton.Crouton;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class Main extends BaseActivity {
	private static final String LOG_TAG = "Main";

	private boolean setTagSucceed = false;

	private int catagory;// 区分总代理和楼栋代理，对UI及相关变量进行不同的初始化

	private DBHelper dbHelper;
	
	private BadgeView notifyBadgeView;

//	private TextView waitPrintedNum;
//	private TextView orderReceiverdTV;
//	private RelativeLayout topLayout;

	private PullToRefreshExpandableListView ptrListView;
	private ExpandableListView listView;// 下拉刷新的控件封装的ExpandableListView
	private SubregionAdapter subregionAdapter;
	private OrderAdapter orderAdapter;

	private RelativeLayout emptyLayout;
	// private TextView title;

	Object lock = new Object();

	MyHandler myHandler;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		getSupportActionBar().setDisplayHomeAsUpEnabled(false);
		getSupportActionBar().setDisplayShowHomeEnabled(false);
		getSupportActionBar().setDisplayShowCustomEnabled(true);
		View actionbar_view = (RelativeLayout) LayoutInflater.from(this)
				.inflate(R.layout.actionbar_main, null);
		getSupportActionBar().setCustomView(actionbar_view);
		// getSupportActionBar().setTitle(AppContext.getInstance().getLoginInfo().getUserName());
//		System.out.println("thread in main name = " + Thread.currentThread().getName());
		
		Button historyBtn = (Button) actionbar_view
				.findViewById(R.id.history_btn);
		TextView title = (TextView) actionbar_view.findViewById(R.id.title);
		Button setttings = (Button) actionbar_view
				.findViewById(R.id.setting_btn);

		title.setText(AppContext.getInstance().getLoginInfo().getUserName());
		
		notifyBadgeView = new BadgeView(AppContext.getInstance() , title);
		notifyBadgeView.setBackgroundResource(R.drawable.newnotice);
		notifyBadgeView.setBadgePosition(BadgeView.POSITION_TOP_RIGHT);
//		notifyBadgeView.show();
		
		historyBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(Main.this, HistoryActivity.class);
				startActivity(intent);
			}
		});

		setttings.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(Main.this, Setting.class);
				startActivity(intent);
			}
		});

		setContentView(R.layout.main);

		dbHelper = DBHelper.getInstance(AppContext.getInstance());

		myHandler = new MyHandler(this);

//		topLayout = (RelativeLayout) findViewById(R.id.top);
//		waitPrintedNum = (TextView) findViewById(R.id.top_hint);
//		orderReceiverdTV = (TextView) findViewById(R.id.order_received);

		catagory = AppContext.getInstance().getLoginInfo().getCategory();

		if (catagory == User.GENERAL_AGENT) {
			historyBtn.setBackgroundResource(R.drawable.logo);
			historyBtn.setEnabled(false);
		}else {
//			topLayout.setVisibility(View.VISIBLE);
//			waitPrintedNum.setText(getResources().getString(R.string.wait_printed_num , 0));
		}

		setTags(AppContext.getInstance().getLoginInfo().getCategory());

		emptyLayout = (RelativeLayout) LayoutInflater.from(this).inflate(
				R.layout.empty_layout, null);

		ptrListView = (PullToRefreshExpandableListView) findViewById(R.id.pull_refresh_expandable_list);

		listView = ptrListView.getRefreshableView();

		ptrListView
				.setOnRefreshListener(new OnRefreshListener<ExpandableListView>() {

					@Override
					public void onRefresh(
							PullToRefreshBase<ExpandableListView> refreshView) {
						// TODO Auto-generated method stub
						
						String label = DateUtils.formatDateTime(getApplicationContext(), System.currentTimeMillis(),
								DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);

						// Update the LastUpdatedLabel
						refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);

						
						if (catagory == User.GENERAL_AGENT) {
							if (subregionAdapter != null) {
								ApiClient.getSimpleOrders(AppContext
										.getInstance().getLoginInfo().getUid(),
										responseHandler);
							} else {
								init();
							}
						} else {
							if (orderAdapter != null) {
								notifyBadgeView.hide();
								ApiClient.getOrders(AppContext.getInstance()
										.getLoginInfo().getUid(),
										responseHandler);
							}
						}

					}
				});

		listView.setGroupIndicator(null);
		// Log.d(LOG_TAG, "---------------->>>>");
		if (catagory == User.GENERAL_AGENT) {
			// Log.d(LOG_TAG, "GeneralAgent");
			initGeneralAgent();
		} else {
			// Log.d(LOG_TAG, "SubAgent");
			initSubAgent();
		}

		registerMessageReceiver(); // used for receive msg
	}

	// 初始化分区adapter
	public void init() {
		// System.out.println("-----------> before list = ");
		List<Subregion> list = AppContext.getInstance().getSubregionList();
//		System.out.println("after list");
		// ApiClient.getSubregionStruct(
		// AppContext.getInstance().getLoginUid(), responseHandler);
		if (list == null || list.size() == 0) {
			Log.i(LOG_TAG, "-----> init()");
			ApiClient.getSubregionStruct(
					AppContext.getInstance().getLoginUid(), responseHandler);

		} else {
			// System.out.println("-----------> have init()");
			subregionAdapter = new SubregionAdapter(AppContext.getInstance(),
					list);
			listView.setAdapter(subregionAdapter);
			ptrListView.onRefreshComplete();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		// getSupportMenuInflater().inflate(R.menu.main, menu);
		// menu.add("settings").setIcon(R.drawable.settings).setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		if (item.getTitle().equals("settings")) {
			// AppContext.getInstance().Logout();
			// AppManager.getAppManager().finishAllActivity();
			Intent intent = new Intent(Main.this, Setting.class);
			startActivity(intent);
		} else if (item.getItemId() == android.R.id.home) {
			if (catagory == User.SUB_AGENT) {
				Intent intent = new Intent(Main.this, HistoryActivity.class);
				startActivity(intent);
			}
		}
		return true;
	}

	private void initGeneralAgent() {
		init();
	}

	private void initSubAgent() {

		List<String> buildingList = new ArrayList<String>();
		User user = AppContext.getInstance().getLoginInfo();
		for (int i = 0; i < user.getLocation().size(); i++) {
			buildingList.add(user.getLocation().get(i).getBulidingName());
		}
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				Message msg = new Message();
				msg.obj = dbHelper.queryOrderUnits(AppContext.getInstance()
						.getLoginUid(), System.currentTimeMillis());
				msg.what = 2;
				myHandler.sendMessage(msg);
			}
		}).start();
		orderAdapter = new OrderAdapter(AppContext.getInstance(), AppContext
				.getInstance().getLoginInfo().getLocation());
		orderAdapter.setDBHelper(dbHelper);
		listView.setAdapter(orderAdapter);
		listView.setOnChildClickListener(new OnChildClickListener() {

			@Override
			public boolean onChildClick(ExpandableListView parent, View v,
					int groupPosition, int childPosition, long id) {
				// TODO Auto-generated method stub
				Order order = orderAdapter.getOrderList().get(groupPosition)
						.get(childPosition);
				// JpushUtil.showToast("childClick" ,AppContext.getInstance() );
				Intent intent = new Intent(Main.this, OrderDetailActivity.class);
				intent.putExtra("order", order);
				startActivity(intent);
				return true;
			}
		});

	}

	@Override
	public void onDestroy() {
		unregisterReceiver(mMessageReceiver);
		super.onDestroy();
	}

	@Override
	public void onNewIntent(Intent intent) {
		 System.out.println(" -----> onNewIntent has triggered");
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			// Intent intent = new Intent(Intent.ACTION_MAIN);
			// intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);//
			// 注意，这个地方最重要，关于解释，自己google吧
			// intent.addCategory(Intent.CATEGORY_HOME);
			// this.startActivity(intent);
			moveTaskToBack(false);
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void processResponse(String info) {
		// TODO Auto-generated method stub

		showCrouton();
		if (catagory == User.SUB_AGENT) {
			// Log.i("processResponse", "process");
			final ResultForSubAgent result = new Gson().fromJson(info,
					ResultForSubAgent.class);
//			System.out.println("result = " + result);
//			int waitPrintedOrder = result.getNoPrints();
			// System.out.println("-----> waitSum = " + waitPrintedNum);
			// System.out.println("waitPrint = " + waitPrintedNum);
//			waitPrintedNum.setText(getResources().getString(
//					R.string.wait_printed_num, waitPrintedOrder));
//			List<OrderUnit> orderUnits = result.getOrderUnits();
//			for (int i = 0; i < orderUnits.size(); i++) {
//				// System.out.println("-----------> orderUnit.loc  =  " +
//				// orderUnits.get(i).getLoc() + " buildNum = " +
//				// orderUnits.get(i).getBuildingNum() + " task_id = " +
//				// orderUnits.get(i).getId());
//				System.out.println(orderUnits.get(i).toString());
//			}
			orderAdapter.processResponse(result.getNoPrints() , result.getOrderUnits());
			myHandler.sendEmptyMessage(1);
			new Thread(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					dbHelper.processOrderUnits(result.getOrderUnits(),
							AppContext.getInstance().getLoginUid(),
							System.currentTimeMillis());
				}
			}).start();
		} else {

			if (subregionAdapter == null) {
				Type typeOfT = new TypeToken<List<Subregion>>() {
				}.getType();
				AppContext.getInstance().saveSubregionStruct(info);
				List<Subregion> list = new Gson().fromJson(info, typeOfT);
				// System.out.println("------------->subregion list.size = "
				// + list.size());
				subregionAdapter = new SubregionAdapter(
						AppContext.getInstance(), list);
				listView.setAdapter(subregionAdapter);
			} else {
				try {
					Type typeOfT = new TypeToken<List<Location>>() {
					}.getType();
					// System.out.println("-------------->333");
					List<Location> locations = new Gson().fromJson(info,
							typeOfT);
//					 System.out.println("--------------> partList.size() = "
//					 + locations.size());
//					
					subregionAdapter.processResponse(locations);
					// System.out.println("-----------> after process");
					ptrListView.onRefreshComplete();
				} catch (Exception e) {
					// TODO: handle exception
					UIHelper.showToast(getApplicationContext(), "数据解析异常");
					Log.e(LOG_TAG, e.toString());
					e.printStackTrace();
					ptrListView.onRefreshComplete();
				}

			}

		}
	}

	@Override
	public void requestFinished(boolean isSucceed) {
		// TODO Auto-generated method stub
		Log.i(LOG_TAG, "requestFinished");
		ptrListView.onRefreshComplete();

	}

	private void showCrouton() {
		if (catagory == User.GENERAL_AGENT) {
			Configuration croutonConfiguration = new Configuration.Builder()
					.setDuration(600).build();
			View view = LayoutInflater.from(this).inflate(
					R.layout.crouton_view, null);
			Crouton.make(this, view, R.id.view_group, croutonConfiguration)
					.show();
		} else {
			Configuration croutonConfiguration = new Configuration.Builder()
					.setDuration(600).build();
			View view = LayoutInflater.from(this).inflate(
					R.layout.crouton_view, null);
			TextView textView = (TextView) view.findViewById(R.id.hint);
			textView.setText(R.string.has_refreshed);
			Crouton.make(this, view, R.id.view_group, croutonConfiguration)
					.show();
		}

	}

	// 为推送设置tags
	private void setTags(int category) {

		Set<String> tagSet = new LinkedHashSet<String>();

		if (category == User.GENERAL_AGENT) {
			tagSet.add("agent_message");
		} else {
			List<Location> locations = AppContext.getInstance().getLoginInfo()
					.getLocation();
			for (int i = 0; i < locations.size(); i++) {
				tagSet.add(locations.get(i).getLocationNum() + "_"
						+ locations.get(i).getBuildingNum());
			}
		}

		// 调用JPush API设置Tag
		mHandler.sendMessage(mHandler.obtainMessage(MSG_SET_TAGS, tagSet));

	}

	private final TagAliasCallback mTagsCallback = new TagAliasCallback() {

		@Override
		public void gotResult(int code, String alias, Set<String> tags) {
			String logs;
			switch (code) {
			case 0:
				logs = "Set tag and alias success";
				Log.i(LOG_TAG, logs);
				setTagSucceed = true;
				break;

			case 6002:
				logs = "Failed to set alias and tags due to timeout. Try again after 60s.";
				Log.i(LOG_TAG, logs);
				if (JpushUtil.isConnected(getApplicationContext())) {
					mHandler.sendMessageDelayed(
							mHandler.obtainMessage(MSG_SET_TAGS, tags),
							1000 * 60);
				} else {
					Log.i(LOG_TAG, "No network");
					// if (! setTagSucceed) {
					// setTags(catagory);
					// }
				}
				break;

			default:
				logs = "Failed with errorCode = " + code;
				Log.e(LOG_TAG, logs);
			}

			// JpushUtil.showToast(logs, getApplicationContext());
		}

	};

	private static final int MSG_SET_TAGS = 1002;

	@SuppressLint("HandlerLeak")
	private final Handler mHandler = new Handler() {
		@SuppressWarnings("unchecked")
		@Override
		public void handleMessage(android.os.Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			// case MSG_SET_ALIAS:
			// Log.d(TAG, "Set alias in handler.");
			// JPushInterface.setAliasAndTags(getApplicationContext(), (String)
			// msg.obj, null, mAliasCallback);
			// break;
			//
			case MSG_SET_TAGS:
				Log.d(LOG_TAG, "Set tags in handler.");
				JPushInterface.setAliasAndTags(getApplicationContext(), null,
						(Set<String>) msg.obj, mTagsCallback);
				break;

			default:
				Log.i(LOG_TAG, "Unhandled msg - " + msg.what);
			}
		}
	};

	static class MyHandler extends Handler {

		private WeakReference<Main> mReference;

		public MyHandler(Main main) {
			this.mReference = new WeakReference<Main>(main);
		}

		@Override
		public void handleMessage(Message msg) {
			if (msg.what == 1) {
				Main main = mReference.get();
				main.ptrListView.onRefreshComplete();
			} else if (msg.what == 2) {
				Main main = mReference.get();
				main.orderAdapter.processResponse(new ArrayList<OrderUnit>() , (List<OrderUnit>) msg.obj);
				System.out.println("subagent init");
			}
		}

	}

	// for receive customer msg from jpush server
	private MessageReceiver mMessageReceiver;
	public static final String ORDER_RECEIVED_ACTION = "com.xiaoyintong.app.ORDER_RECEIVED_ACTION";
	public static final String TYPE = "type";

	public void registerMessageReceiver() {
		mMessageReceiver = new MessageReceiver();
		IntentFilter filter = new IntentFilter();
		filter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);
		filter.addAction(ORDER_RECEIVED_ACTION);
		registerReceiver(mMessageReceiver, filter);
	}

	public class MessageReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			if (ORDER_RECEIVED_ACTION.equals(intent.getAction())) {
				int type = intent.getIntExtra(TYPE, 0);
				// Log.d(LOG_TAG, "-------received");
				if (type == 10) {
					notifyBadgeView.show();
//					orderReceiverdTV.setText(R.string.order_receiverd_notify);
					 System.out.println(" I have received");
				}
			}
		}
	}

}
