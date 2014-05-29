package com.xiaoyintong.app.ui;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import com.xiaoyintong.app.AppContext;
import com.xiaoyintong.app.R;
import com.xiaoyintong.app.adapter.OrderRecordAdapter;
import com.xiaoyintong.app.bean.BuildingBrief;
import com.xiaoyintong.app.bean.OrderRecord;
import com.xiaoyintong.app.db.DBHelper;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class HistoryActivity extends BaseActivity {

	private static final String TAG = "HistoryActivity";

	private static final long ONE_DAY = 86400000;
	private static final int PAGE_SIZE = 15;

	private static long curDate;
	int count = 0;

	private Button backButton;
	// ProgressBar progressBar;
	private ExpandableListView listView;

	private List<OrderRecord> orderList = new ArrayList<OrderRecord>();

	private OrderRecordAdapter adapter;

	private MyHandler myHandler;

	private DBHelper mHelper;

	private Object lock = new Object();

	private View listView_footer;
	private TextView listView_foot_more;
	private ProgressBar listView_foot_progress;

	private boolean has_more = true;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_history);

		init();

		adapter = new OrderRecordAdapter(this, orderList);
		listView.setAdapter(adapter);
		listView.setGroupIndicator(null);
		
//		backButton.setOnClickListener(new OnClickListener() {
//			
//			@Override
//			public void onClick(View v) {
//				// TODO Auto-generated method stub
//				finish();
//			}
//		});
		
		listView.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				// TODO Auto-generated method stub
				// 数据为空--不用继续下面代码了
				// Log.d(TAG, "onScroll --");
				if (!has_more)
					return;

				// 判断是否滚动到底部

				boolean scrollEnd = false;
				try {
					if (view.getPositionForView(listView_footer) == view
							.getLastVisiblePosition())
						scrollEnd = true;
				} catch (Exception e) {
					scrollEnd = false;
				}

				if (scrollEnd) {
					// listView_foot_more.setVisibility(View.VISIBLE);
					listView_foot_more.setText(R.string.loading);
					listView_foot_progress.setVisibility(View.VISIBLE);
					synchronized (lock) {
						int count = 0;
						for (int i = 0; i < PAGE_SIZE; i++) {
							List<BuildingBrief> briefs = mHelper.queryBriefs(
									AppContext.getInstance().getLoginUid(), curDate - i * ONE_DAY);
							if (briefs.size() > 0) {
								count++;
								System.out.println("-----------> briefs");
								OrderRecord orderRecord = new OrderRecord(briefs);
								orderList.add(orderRecord);
							}
						}
						
						myHandler.sendEmptyMessage(count);
					}
				}
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				// TODO Auto-generated method stub
				// Log.d(TAG, "--firstVisibleItem = " + firstVisibleItem);
				// Log.d(TAG, "-- visibleItemCount = " + visibleItemCount);
				// Log.d(TAG, "-- totalItemCount = " + totalItemCount);
			}
		});
	}

	private void init() {

		mHelper = DBHelper.getInstance(this);

		myHandler = new MyHandler(this);

		curDate = System.currentTimeMillis();

//		backButton = (Button) findViewById(R.id.back_btn);
		listView = (ExpandableListView) findViewById(R.id.listview_history);
		listView_footer = getLayoutInflater().inflate(R.layout.listview_footer,
				null);
		listView_foot_more = (TextView) listView_footer
				.findViewById(R.id.listview_foot_more);
		listView_foot_progress = (ProgressBar) listView_footer
				.findViewById(R.id.listview_foot_progress);
		listView.addFooterView(listView_footer);

		getList();
	}

//	@Override
//	public boolean onCreateOptionsMenu(Menu menu) {
//		// Inflate the menu; this adds items to the action bar if it is present.
//		getMenuInflater().inflate(R.menu.history, menu);
//		return true;
//	}

	private void getList() {

		synchronized (lock) {
			int count = 0;
			for (int i = 0; i < PAGE_SIZE; i++) {
				List<BuildingBrief> briefs = mHelper.queryBriefs(
						AppContext.getInstance().getLoginUid(), curDate - i * ONE_DAY);
				if (briefs.size() > 0) {
					count++;
					System.out.println("-----------> briefs");
					OrderRecord orderRecord = new OrderRecord(briefs);
					orderRecord.setDate(curDate - i * ONE_DAY);
					orderList.add(orderRecord);
				}
			}
			myHandler.sendEmptyMessage(count);
		}
//		for (int i = 0; i < 18; i++) {
//			List<BuildingBrief> briefs = new ArrayList<BuildingBrief>();
//			for (int j = 0; j < 6; j++) {
//				BuildingBrief brief = new BuildingBrief(i, j, 50);
//				briefs.add(brief);
//			}
//			OrderRecord orderRecord = new OrderRecord(briefs);
//			orderList.add(orderRecord);
//		}
//		myHandler.sendEmptyMessage(orderList.size());
	}

	static class MyHandler extends Handler {
		WeakReference<HistoryActivity> mReference;

		public MyHandler(HistoryActivity historyActivity) {
			mReference = new WeakReference<HistoryActivity>(historyActivity);
		}

		public void handleMessage(Message msg) {
			if (msg.what < PAGE_SIZE) {
				HistoryActivity historyActivity = mReference.get();
				if (historyActivity != null) {
					Log.i(TAG, "msg has receivered");
					historyActivity.listView_foot_more
							.setText(R.string.no_more);
					historyActivity.listView_foot_progress
							.setVisibility(View.GONE);
					historyActivity.has_more = false;
					curDate -= PAGE_SIZE * ONE_DAY;
				}

			} else {
				HistoryActivity historyActivity = mReference.get();
				if (historyActivity != null) {
					historyActivity.listView_foot_progress
							.setVisibility(View.VISIBLE);
					historyActivity.listView_foot_more
							.setText(R.string.loading);
					curDate -= PAGE_SIZE * ONE_DAY;
				}
			}
		}
	}

}
