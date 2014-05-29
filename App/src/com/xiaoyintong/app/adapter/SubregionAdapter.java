package com.xiaoyintong.app.adapter;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.xiaoyintong.app.api.ApiClient;
import com.xiaoyintong.app.api.MyResponseHandler;
import com.xiaoyintong.app.api.ResponseListener;
import com.xiaoyintong.app.bean.Location;
import com.xiaoyintong.app.bean.PartBusiness;
import com.xiaoyintong.app.bean.SimpleOrder;
import com.xiaoyintong.app.bean.Subregion;
import com.xiaoyintong.app.common.UIHelper;
import com.xiaoyintong.app.widget.CustomDialog;
import com.xiaoyintong.app.AppContext;
import com.xiaoyintong.app.AppManager;
import com.xiaoyintong.app.R;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class SubregionAdapter extends BaseExpandableListAdapter {

	private static final String LOG_TAG = "SubregionAdapter";
	// private static final boolean DEBUG = true;

	private List<Subregion> subregions;
	private AppContext context;
	private LayoutInflater inflater;

	List<Integer> deliveryStateList = new ArrayList<Integer>();// 保存派送状态
	List<Integer> requestState = new ArrayList<Integer>();// 保存是否正在发送请求这一状态 (0/1
															// no/yes)

	private static final int STATUS_DEFAULT = 0;
	private static final int STATUS_FAILURE = 1;
	private static final int STATUS_SUCCEED = 2;

	private List<PartBusiness> partBusinesses = new ArrayList<PartBusiness>();

	private int counter = 0;
	private CustomDialog mDialog;
	private Button curButton;
	private ProgressBar curProgressBar;
	private int curGroupPos;
	private List<Map<String, Object>> statusMap = new ArrayList<Map<String, Object>>();
	private Gson gson = new Gson();

	private String info = "";
	// private DecimalFormat mDecimalFormat = new DecimalFormat("###.0");

	static class ViewHolder {
		public TextView tv_buildingNum;
		public TextView tv_detail;
		// public TextView tv_moneySum;
	}

	public SubregionAdapter(AppContext context, List<Subregion> subregions) {
		this.context = context;
		this.inflater = (LayoutInflater) this.context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.subregions = subregions;
		init();
		initDialog();
		// System.out.println("-------------> after init()");
	}

	private void initDialog() {
		mDialog = new CustomDialog(
				AppManager.getAppManager().currentActivity(), 300, 280,
				R.layout.confirm_dialog, R.style.MyDialogStyle);
		mDialog.setCanceledOnTouchOutside(true);
		TextView title = (TextView) mDialog.findViewById(R.id.title);
		title.setText(R.string.agent_delivered);
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
					Map<String, Object> map = new HashMap<String, Object>();
					map.put("curGroupPos", curGroupPos);
					map.put("curProgress", curProgressBar);
					map.put("curButton", curButton);
					curButton.setVisibility(View.GONE);
					curProgressBar.setVisibility(View.VISIBLE);
					statusMap.add(map);

					requestState.set(curGroupPos, 1);
					ApiClient.sendDeliveryMsg(info , getMyResponseHandler());

				} else {
					UIHelper.showToast(context, "无网络连接，请开启网络");
				}
			}
		});

	}

	private void init() {

		partBusinesses.clear();
		deliveryStateList.clear();
		requestState.clear();

		for (int i = 0; i < subregions.size(); i++) {
			List<SimpleOrder> list = new ArrayList<SimpleOrder>();
			for (int j = 0; j < subregions.get(i).getLocations().size(); j++) {
				SimpleOrder simpleOrder = new SimpleOrder(subregions.get(i)
						.getLocations().get(j).getLocationNum(), subregions
						.get(i).getLocations().get(j).getBuildingNum());
				list.add(simpleOrder);
			}
			PartBusiness partBusiness = new PartBusiness(subregions.get(i)
					.getPartNum(), list);
			partBusinesses.add(partBusiness);
			deliveryStateList.add(STATUS_DEFAULT);
			requestState.add(0);
		}

	}

	public void setSubRegions(List<Subregion> subregions) {
		this.subregions = subregions;
		init();
		notifyDataSetChanged();
	}

	@Override
	public Object getChild(int groupPosition, int childPosition) {
		// TODO Auto-generated method stub

		return null;
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		// TODO Auto-generated method stub
		return childPosition;
	}

	@Override
	public View getChildView(int groupPosition, int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		// System.out.println("------------>run ->" + "getView()");
		ViewHolder viewHolder = null;
		if (convertView == null) {

			viewHolder = new ViewHolder();
			convertView = inflater.inflate(R.layout.child_item, null);
			viewHolder.tv_buildingNum = (TextView) convertView
					.findViewById(R.id.building_num);
			viewHolder.tv_detail = (TextView) convertView
					.findViewById(R.id.detail);
			// viewHolder.tv_moneySum = (TextView) convertView
			// .findViewById(R.id.tv_moneySum);
			// 为view设置标签
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		viewHolder.tv_buildingNum.setText(partBusinesses.get(groupPosition)
				.getSimpleOrders().get(childPosition).getBuildingName());
		if (partBusinesses.get(groupPosition).getSimpleOrders()
				.get(childPosition).hasOrder()) {
			viewHolder.tv_detail.setText("有单");
		} else {
			viewHolder.tv_detail.setText("无单");
		}
		// viewHolder.tv_moneySum.setText(mDecimalFormat.format(partBusinesses
		// .get(groupPosition).getSimpleOrders().get(childPosition)
		// .getMoney())
		// + "元");

		return convertView;
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		// TODO Auto-generated method stub
		return partBusinesses.get(groupPosition).getSimpleOrders().size();
	}

	@Override
	public Object getGroup(int groupPosition) {
		// TODO Auto-generated method stub
		return subregions.get(groupPosition).getPartNum();
	}

	@Override
	public int getGroupCount() {
		// TODO Auto-generated method stub
		return subregions.size();
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
		RelativeLayout groupLayout = (RelativeLayout) inflater.inflate(
				R.layout.fenqu_item, null);
		ImageView imageView = (ImageView) groupLayout
				.findViewById(R.id.indicator);
		TextView textView = (TextView) groupLayout.findViewById(R.id.fenqu_num);
		Button button = (Button) groupLayout.findViewById(R.id.send_btn);
		final ProgressBar progressBar = (ProgressBar) groupLayout
				.findViewById(R.id.progress);

		if (!isExpanded) {
			imageView.setBackgroundResource(R.drawable.group_img_1);
		} else {
			imageView.setBackgroundResource(R.drawable.group_img_0);
		}
		textView.setText("分区" + subregions.get(groupPosition).getPartNum());
		// if (orderLists.get(groupPosition).get(ch).isDelivered()) {
		// button.setBackgroundResource(R.drawable.ok_btn);
		// }
		switch (deliveryStateList.get(groupPosition)) {
		case STATUS_DEFAULT:
			button.setText("发送");
			if (SubregionAdapter.this.hasOrders(groupPosition)) {
				button.setEnabled(true);
			} else {
				button.setTextColor(context.getResources().getColor(
						R.color.unable_text_color));
				button.setEnabled(false);
			}

			break;
		case STATUS_FAILURE:
			button.setText("重发");
			button.setEnabled(true);
			break;
		case STATUS_SUCCEED:
			button.setText("完成");
			button.setEnabled(false);
			button.setTextColor(context.getResources().getColor(
					R.color.unable_text_color));
			break;

		default:
			break;
		}

		switch (requestState.get(groupPosition)) {
		case 0:
			button.setVisibility(View.VISIBLE);
			progressBar.setVisibility(View.GONE);
			break;
		case 1:
			button.setVisibility(View.GONE);
			progressBar.setVisibility(View.VISIBLE);
			break;
		default:
			break;
		}

		button.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				curButton = (Button) v;
				curProgressBar = progressBar;
				curGroupPos = groupPosition;
				int groupSize = partBusinesses.get(groupPosition).getSimpleOrders().size();
//				int partNum = partBusinesses.get(groupPosition).getPartName();
//				List<Location> locations = new ArrayList<Location>();
				JSONArray jsonArray = new JSONArray();
				for (int i = 0; i < groupSize; i++) {
					int loc = partBusinesses.get(groupPosition).getSimpleOrders().get(i).getLocation();
					int buildNum = partBusinesses.get(groupPosition).getSimpleOrders().get(i).getBuild();
//					locations.add(new Location(loc, buildNum));
					try {
						jsonArray.put(new JSONObject(gson.toJson(new Location(loc, buildNum))));
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
//				Type typeOfT = new TypeToken<List<Location>>() {
//				}.getType();
				// System.out.println("-------------->333");
				
				
				JSONObject jsonObject = new JSONObject();
				try {
					jsonObject.put("area", jsonArray);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				info = jsonObject.toString();
				
				mDialog.show();

			}
		});
		return groupLayout;
	}

	private boolean hasOrders(int groupPosition) {
		boolean hasOrders = false;
		for (int i = 0; i < partBusinesses.get(groupPosition).getSimpleOrders()
				.size(); i++) {
			if (partBusinesses.get(groupPosition).getSimpleOrders().get(i)
					.hasOrder()) {
				hasOrders = true;
				break;
			}
		}
		return hasOrders;
	}

	@Override
	public boolean hasStableIds() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		// TODO Auto-generated method stub
		return true;
	}

	public void processResponse(List<Location> locations) {
		for (int i = 0; i < partBusinesses.size(); i++) {
			deliveryStateList.set(i, STATUS_DEFAULT);
//			System.out.println(" i = " + i);
			int size = partBusinesses.get(i).getSimpleOrders().size();
			for (int j = 0; j < size; j++) {
//				System.out.println(" j = " + j);
				partBusinesses.get(i).getSimpleOrders().get(j)
						.setHasOrder(false);
			}
		}
		for (int i = 0; i < locations.size(); i++) {
			for (int j = 0; j < partBusinesses.size(); j++) { 
				int size = partBusinesses.get(j).getSimpleOrders().size();
				for (int j2 = 0; j2 < size; j2++) {
					if (partBusinesses.get(j).getSimpleOrders().get(j2)
							.getBuild() == locations.get(i).getBuildingNum() && partBusinesses.get(j).getSimpleOrders().get(j2).getLocation() == locations.get(i).getLocationNum()) {
						partBusinesses.get(j).getSimpleOrders().get(j2)
								.setHasOrder(true);
					}
				}
			}
		}
		notifyDataSetChanged();
	}

	// public void processResponse(List<PartBusiness> partList) {
	// for (int i = 0; i < partList.size(); i++) {
	// for (int j = 0; j < partBusinesses.size(); j++) {
	// if (partBusinesses.get(j).getPartName() == partList.get(i)
	// .getPartName()) {
	// for (int j2 = 0; j2 < partList.get(i).getSimpleOrders()
	// .size(); j2++) {
	// for (int k = 0; k < partBusinesses.get(j)
	// .getSimpleOrders().size(); k++) {
	// if (partBusinesses
	// .get(j)
	// .getSimpleOrders()
	// .get(k)
	// .accept(partList.get(i).getSimpleOrders()
	// .get(j2))) {
	// break;
	// }
	// }
	// }
	//
	// break;
	// }
	// }
	// }
	//
	// notifyDataSetChanged();
	// }

	private MyResponseHandler getMyResponseHandler() {
		return new MyResponseHandler(context, new ResponseListener() {

			private int id = counter++;
			private Button button;
			private int curGroupPos;

			@Override
			public void requestFinished(boolean isSucceed) {
				// TODO Auto-generated method stub
				try {
					button = (Button) statusMap.get(id).get("curButton");
					ProgressBar progressBar = (ProgressBar) statusMap.get(id)
							.get("curProgress");
					curGroupPos = (Integer) statusMap.get(id)
							.get("curGroupPos");

					requestState.set(curGroupPos, 0);

					button.setVisibility(View.VISIBLE);
					progressBar.setVisibility(View.GONE);
					if (!isSucceed) {
						button.setText("重发");
						deliveryStateList.set(curGroupPos, STATUS_FAILURE);
					}
				} catch (Exception e) {
					// TODO: handle exception
					Log.e(LOG_TAG + " id = " + id, "requestFinished id =  "
							+ id + " --- " + e.toString());
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
					deliveryStateList.set(curGroupPos, STATUS_SUCCEED);
				} catch (Exception e) {
					// TODO: handle exception
					Log.e(LOG_TAG + " id = " + id, "processResponse id =  "
							+ id + " --- " + e.toString());
				}

			}
		});
	}

}
