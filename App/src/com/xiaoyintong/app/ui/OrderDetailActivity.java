package com.xiaoyintong.app.ui;

import java.util.List;


import com.actionbarsherlock.view.Menu;
import com.xiaoyintong.app.R;
import com.xiaoyintong.app.adapter.CardAdapter;
import com.xiaoyintong.app.bean.Order;
import com.xiaoyintong.app.bean.OrderUnit;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;

public class OrderDetailActivity extends BaseActivity {
	

	private ListView detailListView;
	private Button backBtn;
	private List<OrderUnit> orderList;
	private CardAdapter adapter;

	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_order_detail);
		
		
		Order order = (Order)getIntent().getSerializableExtra("order");
		
		orderList = order.getOrderUnits();
		
		init();

		adapter = new CardAdapter(this, orderList);
		detailListView.setAdapter(adapter);
		detailListView.setSelector(R.drawable.list_shape);
		
	}

	private void init() {
		detailListView = (ListView) findViewById(R.id.detail_lv);
//		backBtn = (Button) findViewById(R.id.back);
//		backBtn.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				// TODO Auto-generated method stub
//				finish();
//			}
//		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		return super.onCreateOptionsMenu(menu);
	}

}
