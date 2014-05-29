package com.xiaoyintong.app.adapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.xiaoyintong.app.AppContext;
import com.xiaoyintong.app.AppManager;
import com.xiaoyintong.app.R;
import com.xiaoyintong.app.api.ApiClient;
import com.xiaoyintong.app.api.MyResponseHandler;
import com.xiaoyintong.app.api.ResponseListener;
import com.xiaoyintong.app.bean.Location;
import com.xiaoyintong.app.bean.Order;
import com.xiaoyintong.app.bean.OrderUnit;
import com.xiaoyintong.app.common.Arith;
import com.xiaoyintong.app.common.UIHelper;
import com.xiaoyintong.app.db.DBHelper;
import com.xiaoyintong.app.widget.CustomDialog;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
public class OrderAdapter extends BaseExpandableListAdapter {

	private static final String TAG = "OrderAdapter";

	// private Lock lock = new ReentrantLock();

	private int counter = 0;
	private Context context;
	private LayoutInflater inflater;
	private List<Location> buildingList;
	private List<List<Order>> orderList = new ArrayList<List<Order>>();
	private List<List<Order>> sendableOrders = new ArrayList<List<Order>>();
	private List<List<Order>> unsendableOrders = new ArrayList<List<Order>>();

	// private MyResponseHandler responseHandler;
	private String info;
	private CustomDialog mDialog;
	private List<String> list = new LinkedList<String>();

	private Button button;
	private int curGroupPos;
	private int curChildPos;
	private ProgressBar curProgressBar;

	private boolean isGroup;//

	List<Integer> deliveryStateList = new ArrayList<Integer>();// 保存派送状态
	List<Integer> requestState = new ArrayList<Integer>();// 保存是否正在发送请求这一状态 (0/1
															// no/yes)

	private static final int STATUS_DEFAULT = 0;
	private static final int STATUS_FAILURE = 1;
	private static final int STATUS_SUCCEED = 2;

	private DBHelper mHelper;

	private List<Map<String, Object>> statusMap = new ArrayList<Map<String, Object>>();

	// private List<Map<String, Object>> groupStatusList = new
	// ArrayList<Map<String, Object>>();

	static class ViewHolder {
		public TextView roomTV;
		public TextView userNameTV;
		public TextView orderSumTV;
		public TextView moneySumTV;
		public Button yesBtn;
		public ProgressBar progressBar;
	}

	public OrderAdapter(Context context, List<Location> buildingList) {
		this.context = context;
		this.buildingList = buildingList;
		counter = 0;
		for (int i = 0; i < buildingList.size(); i++) {
			List<Order> orders = new ArrayList<Order>();
			List<Order> cannotBeSendOrders = new ArrayList<Order>();
			List<Order> canBeSendOrders = new ArrayList<Order>();
			sendableOrders.add(canBeSendOrders);
			unsendableOrders.add(cannotBeSendOrders);
			orderList.add(orders);
		}
		inflater = LayoutInflater.from(this.context);
		init();
	}

	private void init() {
		mDialog = new CustomDialog(
				AppManager.getAppManager().currentActivity(), 300, 280,
				R.layout.confirm_dialog, R.style.MyDialogStyle);
		mDialog.setCanceledOnTouchOutside(true);

		for (int i = 0; i < buildingList.size(); i++) {
			deliveryStateList.add(STATUS_DEFAULT);
			requestState.add(0);
		}
		Button cancelBtn = (Button) mDialog.findViewById(R.id.cancel);
		Button yesBtn = (Button) mDialog.findViewById(R.id.yes);
		cancelBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				mDialog.dismiss();
			}
		});
		yesBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				mDialog.dismiss();
				if (AppContext.getInstance().isNetworkConnected()) {
					if (isGroup) {
						Map<String, Object> map = new HashMap<String, Object>();
						map.put("curGroupPos", curGroupPos);
						map.put("curProgress", curProgressBar);
						map.put("curButton", button);
						map.put("curTids", list);
						statusMap.add(map);
						ApiClient.sendMessage(info, getMyResponseHandler());
						requestState.set(curGroupPos, 1);
						changeUIbyState(true);

					} else {
						Map<String, Object> map = new HashMap<String, Object>();
						map.put("curGroupPos", curGroupPos);
						map.put("curChildPos", curChildPos);
						map.put("curProgress", curProgressBar);
						map.put("curButton", button);
						map.put("curTids", list);
						changeUIbyState(true);
						statusMap.add(map);
						ApiClient.sendMessage(info, getMyResponseHandler());
						orderList.get(curGroupPos).get(curChildPos)
								.setRequestState(true);
					}

				} else {
					UIHelper.showToast(context, "无网络连接，请开启网络");
				}

			}
		});
	}

	@Override
	public Object getChild(int groupPosition, int childPosition) {
		// TODO Auto-generated method stub
		Log.i(TAG, "getChild");
		if (orderList.get(groupPosition).size() > childPosition) {
			return orderList.get(groupPosition).get(childPosition);
		} else {
			return null;
		}

		// return orderList.get(groupPosition).get(childPosition);
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getChildView(final int groupPosition, final int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder holder = null;
		if (convertView == null) {
			// System.out.println("------------> getChildView 2 convertView is null");
			holder = new ViewHolder();
			convertView = inflater.inflate(R.layout.sub_child_item, null);
			LinearLayout linearLayout = (LinearLayout) convertView
					.findViewById(R.id.layout);
			holder.yesBtn = (Button) convertView.findViewById(R.id.yes_btn);
			holder.roomTV = (TextView) linearLayout.findViewById(R.id.room_num);
			holder.userNameTV = (TextView) linearLayout
					.findViewById(R.id.user_name);
			holder.orderSumTV = (TextView) linearLayout
					.findViewById(R.id.order_sum);
			holder.moneySumTV = (TextView) linearLayout
					.findViewById(R.id.money_sum);
			holder.progressBar = (ProgressBar) convertView
					.findViewById(R.id.progress);
			convertView.setTag(holder);

		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		holder.roomTV.setText(orderList.get(groupPosition).get(childPosition)
				.getRoomNum());
		holder.userNameTV.setText(orderList.get(groupPosition)
				.get(childPosition).getUserName());
		holder.orderSumTV.setText(orderList.get(groupPosition)
				.get(childPosition).getOrderSum()
				+ "份");
		holder.moneySumTV.setText(orderList.get(groupPosition)
				.get(childPosition).getMoneySum()
				+ "元");
		switch (orderList.get(groupPosition).get(childPosition)
				.getDeliveryState()) {
		case 0:
			holder.yesBtn.setText("发送");
			holder.yesBtn.setEnabled(true);
			break;
		case 1:
			holder.yesBtn.setEnabled(true);
			holder.yesBtn.setText("重发");
			break;
		case 2:
			holder.yesBtn.setEnabled(false);
			holder.yesBtn.setText("完成");
			holder.yesBtn.setTextColor(context.getResources().getColor(
					R.color.unable_text_color));
			break;
		default:
			break;
		}

		// 未取货的订单，发送按钮不能点击
		if (!orderList.get(groupPosition).get(childPosition).isDelivering()) {
			holder.yesBtn.setEnabled(false);
			holder.yesBtn.setTextColor(context.getResources().getColor(
					R.color.unable_text_color));
			convertView.setBackgroundColor(context.getResources().getColor(
					R.color.Lightcyan));
		} else {
			convertView
					.setBackgroundResource(R.drawable.sub_child_item_selector);
		}

		if (orderList.get(groupPosition).get(childPosition).isRequesting()) {
			System.out.println("isRequesting ---------->>>");
			holder.progressBar.setVisibility(View.VISIBLE);
			holder.yesBtn.setVisibility(View.INVISIBLE);
		} else {
			holder.progressBar.setVisibility(View.GONE);
			holder.yesBtn.setVisibility(View.VISIBLE);
		}
		// System.out.println("-----------> name = "
		// + orderList.get(groupPosition).get(childPosition).getUserName()
		// + "  state = "
		// + orderList.get(groupPosition).get(childPosition)
		// .getDeliveryState());
		holder.yesBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				isGroup = false;
				curProgressBar = (ProgressBar) ((RelativeLayout) v.getParent())
						.findViewById(R.id.progress);
				curChildPos = childPosition;
				curGroupPos = groupPosition;
				button = (Button) v;

				System.out.println("in onClick groupPos = " + groupPosition
						+ " childPos =" + childPosition);
				list.clear();
				JSONArray jsonArray = new JSONArray();
				int size = orderList.get(groupPosition).get(childPosition)
						.getOrderUnits().size();
				for (int i = 0; i < size; i++) {
					String tid = orderList.get(groupPosition)
							.get(childPosition).getOrderUnits().get(i).getTid();
					jsonArray.put(tid);
					list.add(tid);
				}
				// System.out.println("-------> list size = " + list.size());
				// CompleteMessage msg = new CompleteMessage(AppContext
				// .getInstance().getLoginUid(), list);
				JSONObject jsonObject = new JSONObject();
				try {
					jsonObject.put("task", jsonArray);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				info = jsonObject.toString();
				mDialog.show();
			}
		});
		return convertView;
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		// TODO Auto-generated method stub
		// Log.i(TAG, "getChildCount");
		System.out
				.println("childSize = " + orderList.get(groupPosition).size());
		return orderList.get(groupPosition).size();
	}

	@Override
	public Object getGroup(int groupPosition) {
		// TODO Auto-generated method stub
		// Log.i(TAG, "getGroup");
		return buildingList.get(groupPosition).getBuildingNum();
	}

	@Override
	public int getGroupCount() {
		// TODO Auto-generated method stub
		return buildingList.size();
	}

	@Override
	public long getGroupId(int groupPosition) {
		// TODO Auto-generated method stub
		return groupPosition;
	}

	@Override
	public View getGroupView(final int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		Log.i(TAG, "getGroupView");
		RelativeLayout layout = (RelativeLayout) inflater.inflate(
				R.layout.sub_group_item, null);
		ImageView indicator = (ImageView) layout.findViewById(R.id.indicator_1);
		TextView buildingNumTV = (TextView) layout
				.findViewById(R.id.buildingNum_tv);
		TextView orderSumTV = (TextView) layout.findViewById(R.id.order_sum);
		TextView moneySumTV = (TextView) layout.findViewById(R.id.money_sum);

		Button yesBtn = (Button) layout.findViewById(R.id.yes_btn);

		final ProgressBar progressBar = (ProgressBar) layout
				.findViewById(R.id.progress);

		if (isExpanded) {
			indicator.setBackgroundResource(R.drawable.group_img_0);
		} else {
			indicator.setBackgroundResource(R.drawable.group_img_1);
		}

		buildingNumTV
				.setText(buildingList.get(groupPosition).getBulidingName());
		int orderSum = 0;
		double moneySum = 0;
		for (int i = 0; i < orderList.get(groupPosition).size(); i++) {
			orderSum += orderList.get(groupPosition).get(i).getOrderSum();
			moneySum = Arith.add(
					moneySum,
					Double.valueOf(orderList.get(groupPosition).get(i)
							.getMoneySum()));
		}
		orderSumTV.setText(orderList.get(groupPosition).size()+ "单");
		moneySumTV.setText(moneySum + "元");

		switch (deliveryStateList.get(groupPosition)) {
		case STATUS_DEFAULT:
			yesBtn.setText("发送");
			if (sendableGroup(groupPosition)) {
				yesBtn.setEnabled(true);
			} else {
				yesBtn.setTextColor(context.getResources().getColor(
						R.color.unable_text_color));
				yesBtn.setEnabled(false);
			}

			break;
		case STATUS_FAILURE:
			yesBtn.setText("重发");
			yesBtn.setEnabled(true);
			break;
		case STATUS_SUCCEED:
			yesBtn.setText("完成");
			yesBtn.setEnabled(false);
			yesBtn.setTextColor(context.getResources().getColor(
					R.color.unable_text_color));
			break;

		default:
			break;
		}

		switch (requestState.get(groupPosition)) {
		case 0:
			yesBtn.setVisibility(View.VISIBLE);
			progressBar.setVisibility(View.GONE);
			break;
		case 1:
			yesBtn.setVisibility(View.GONE);
			progressBar.setVisibility(View.VISIBLE);
			break;
		default:
			break;
		}

		yesBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				isGroup = true;
				button = (Button) v;
				curProgressBar = progressBar;
				curGroupPos = groupPosition;

				list.clear();

				JSONArray jsonArray = new JSONArray();
				int size = sendableOrders.get(groupPosition).size();
				for (int i = 0; i < size; i++) {
					int childSize = sendableOrders.get(groupPosition).get(i)
							.getOrderUnits().size();
					// System.out.println("childSize = " + childSize);
					for (int j = 0; j < childSize; j++) {
						// System.out.println(" j = " + j);
						String tid = sendableOrders.get(groupPosition).get(i)
								.getOrderUnits().get(j).getTid();
						jsonArray.put(tid);
						list.add(tid);
					}
				}

				JSONObject jsonObject = new JSONObject();
				try {
					jsonObject.put("task", jsonArray);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				info = jsonObject.toString();
				mDialog.show();
			}
		});

		return layout;
	}

	@Override
	public boolean hasStableIds() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		// TODO Auto-generated method stub`
		return true;
	}

	// 20 19 19 20 20 20
	public void processResponse(List<OrderUnit> cannotBeSendOrders,
			List<OrderUnit> canBeSendOrders) {
		System.out.println("--------------> sendable size = "
				+ canBeSendOrders.size());
		// System.out.println(" can be send : " +
		// canBeSendOrders.get(0).toString());
		System.out
				.println(" ==================== processResponse has called =============== \n");
		for (int i = 0; i < buildingList.size(); i++) {
			unsendableOrders.get(i).clear();
			orderList.get(i).clear();
			// System.out.println("-------> init size send = " +
			// sendableOrders.get(i).size());
			// System.out.println("----------> init size unsend = " +
			// unsendableOrders.get(i).size());
		}

		processSendableOrders(canBeSendOrders);
		processUnsendableOrders(cannotBeSendOrders);

//		System.out.println("--------------> unsendable ====== size = "
//				+ unsendableOrders.get(0).size());
		for (int i = 0; i < buildingList.size(); i++) {
			// System.out.println("sendable size = "
			// + sendableOrders.get(i).size());
			// System.out.println("unsendable size = "
			// + unsendableOrders.get(i).size());
			// orderList.get(i).clear();
			Collections.sort(sendableOrders.get(i));
			Collections.sort(unsendableOrders.get(i));
			orderList.get(i).addAll(sendableOrders.get(i));
			orderList.get(i).addAll(unsendableOrders.get(i));
			// System.out.println("add boolean = " +
			// orderList.get(i).addAll(unsendableOrders.get(i)));
			// System.out.println("unsendable orders size = " +
			// unsendableOrders.get(i).size());
			// System.out.println("orderList size = " +
			// orderList.get(i).size());
		}

		for (int i = 0; i < orderList.size(); i++) {
			System.out.println("orderList  : " + orderList.get(i).size());
		}

		notifyDataSetChanged();
	}

	private void processSendableOrders(List<OrderUnit> sendableOrderUnits) {
		int size = sendableOrderUnits.size();
		System.out.println("size in send  = " + size);
		int count = 0;

		for (int i = 0; i < size; i++) {
			// System.out.println("order = " + sendableOrderUnits.get(i));
			for (int j = 0; j < buildingList.size(); j++) {
				if (sendableOrderUnits.get(i).getBuildingNum() == Integer
						.parseInt(getGroup(j).toString())) {
					// System.out.println("---------> sendSize = "
					// + sendableOrders.get(j).size());

					// System.out.println("order building num = " +
					// sendableOrderUnits.get(i).getBuildingNum());
					boolean isExist = false;
					for (int k = 0; k < sendableOrders.get(j).size(); k++) {
						if (sendableOrders.get(j).get(k)
								.accept(sendableOrderUnits.get(i))) {
							// System.out.println("----------> k = " + k);
							isExist = true;
							break;
						}

					}
					if (!isExist) {
						List<OrderUnit> orderUnits2 = new ArrayList<OrderUnit>();
						orderUnits2.add(sendableOrderUnits.get(i));
						Order order = new Order(orderUnits2);
						sendableOrders.get(j).add(order);
						count++;
					}
					break;
				}
			}
		}
		System.out.println("count = " + count);

	}

	private boolean sendableGroup(int groupPos) {
		int size = sendableOrders.get(groupPos).size();
		boolean sendable = false;
		for (int i = 0; i < size; i++) {
			if (sendableOrders.get(groupPos).get(i).isDelivering() && sendableOrders.get(groupPos).get(i).getDeliveryState() != 2) {
				sendable = true;
				break;
			}
		}
		return sendable;
	}

	private void processUnsendableOrders(List<OrderUnit> unsendableOrderUnits) {
		int size = unsendableOrderUnits.size();
		System.out.println("size in unSend = " + size);
		for (int i = 0; i < size; i++) {
			System.out.println("order = " + unsendableOrderUnits.get(i));
			for (int j = 0; j < buildingList.size(); j++) {
				if (unsendableOrderUnits.get(i).getBuildingNum() == Integer
						.parseInt(getGroup(j).toString())) {
					boolean isExist = false;
					for (int k = 0; k < unsendableOrders.get(j).size(); k++) {

						if (unsendableOrders.get(j).get(k)
								.accept(unsendableOrderUnits.get(i))) {
							isExist = true;
							break;
						}

					}
					if (!isExist) {
						List<OrderUnit> orderUnits2 = new ArrayList<OrderUnit>();
						orderUnits2.add(unsendableOrderUnits.get(i));
						Order order = new Order(orderUnits2);
						unsendableOrders.get(j).add(0, order);
					}
					break;
				}
			}
		}
		// System.out.println("unsendableOrder Size = " +
		// unsendableOrders.get(0).size());
	}

	public List<List<Order>> getOrderList() {
		return this.orderList;
	}

	public void setDBHelper(DBHelper mDbHelper) {
		this.mHelper = mDbHelper;
	}

	private void changeUIbyState(boolean isRequesting) {
		if (isRequesting) {
			button.setVisibility(View.INVISIBLE);
			curProgressBar.setVisibility(View.VISIBLE);
		} else {
			button.setVisibility(View.VISIBLE);
			curProgressBar.setVisibility(View.GONE);
		}
	}

	private MyResponseHandler getMyResponseHandler() {
		return new MyResponseHandler(context, new ResponseListener() {

			private int id = counter++;
			private Button button;
			private int groupPos;
			private int childPos;
			private List<String> tids;

			@SuppressWarnings("unchecked")
			@Override
			public void requestFinished(boolean isSucceed) {
				// TODO Auto-generated method stub
				try {
					groupPos = (Integer) statusMap.get(id).get("curGroupPos");
					// childPos = (Integer)
					// statusMap.get(id).get("curChildPos");
					button = (Button) statusMap.get(id).get("curButton");
					ProgressBar progressBar = (ProgressBar) statusMap.get(id)
							.get("curProgress");
					button.setVisibility(View.VISIBLE);

					tids = (List<String>) statusMap.get(id).get("curTids");

					progressBar.setVisibility(View.GONE);

					if (isGroup) {
						requestState.set(groupPos, 0);
						if (!isSucceed) {
							button.setText("重发");
							deliveryStateList.set(groupPos, STATUS_FAILURE);
						}
					} else {
						childPos = (Integer) statusMap.get(id).get(
								"curChildPos");
						System.out.println("groupPos = " + groupPos
								+ " childPos = " + childPos);
						orderList.get(groupPos).get(childPos)
								.setRequestState(false);

						if (!isSucceed) {
							button.setText("重发");
							orderList.get(groupPos).get(childPos)
									.setDeliveryState(1);
							mHelper.updateOrderUnit(AppContext.getInstance()
									.getLoginUid(), tids, 1);
						}
					}

				} catch (Exception e) {
					// TODO: handle exception
					Log.e(TAG + " id = " + id, "requestFinished id =  " + id
							+ " --- " + e.toString());
				}

			}

			@Override
			public void processResponse(String info) {
				// TODO Auto-generated method stub
				try {
					button.setText("完成");
					button.setEnabled(false);
					button.setTextColor(context.getResources().getColor(
							R.color.unable_text_color));
					if (!isGroup) {
						orderList.get(groupPos).get(childPos)
								.setDeliveryState(2);
						mHelper.updateOrderUnit(AppContext.getInstance()
								.getLoginUid(), tids, 2);
					} else {
						updateOrderStatus(groupPos);
						new Thread(new Runnable() {

							@Override
							public void run() {
								// TODO Auto-generated method stub
								mHelper.updateOrderByBuilding(AppContext
										.getInstance().getLoginUid(),
										buildingList.get(groupPos)
												.getLocationNum(),
										buildingList.get(groupPos)
												.getBuildingNum(), 2);
							}
						}).start();

					}

				} catch (Exception e) {
					// TODO: handle exception
					Log.e(TAG + " id = " + id, "processResponse id =  " + id
							+ " --- " + e.toString());
				}

			}
		});
	}

	private void updateOrderStatus(int groupPos) {
		int size = sendableOrders.get(groupPos).size();
		for (int i = 0; i < size; i++) {
			sendableOrders.get(groupPos).get(i).setDeliveryState(2);
		}
		notifyDataSetChanged();
	}
}
