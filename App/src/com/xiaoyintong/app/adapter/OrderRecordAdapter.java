package com.xiaoyintong.app.adapter;

import java.util.List;

import com.xiaoyintong.app.R;
import com.xiaoyintong.app.bean.OrderRecord;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class OrderRecordAdapter extends BaseExpandableListAdapter {

	private LayoutInflater inflater;
	private List<OrderRecord> orderRecords;

	static class ViewHolder {
		public TextView tv_buildingNum;
		public TextView tv_orderSum;
		public TextView tv_moneySum;
	}

	static class GroupHolder {
		public TextView date;
		public TextView orderSum;
		public TextView moneySum;
		public ImageView indicator;
	}

	public OrderRecordAdapter(Context context, List<OrderRecord> orderRecords) {
		this.orderRecords = orderRecords;
		inflater = LayoutInflater.from(context);
	}

	@Override
	public Object getChild(int arg0, int arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getChildView(int groupPosition, int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder viewHolder = null;
		if (convertView == null) {

			viewHolder = new ViewHolder();
			convertView = inflater.inflate(R.layout.brief_item, null);
			viewHolder.tv_buildingNum = (TextView) convertView
					.findViewById(R.id.tv_buildingNum);
			viewHolder.tv_orderSum = (TextView) convertView
					.findViewById(R.id.tv_orderSum);
			viewHolder.tv_moneySum = (TextView) convertView
					.findViewById(R.id.tv_moneySum);
			// 为view设置标签
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		viewHolder.tv_buildingNum.setText(orderRecords.get(groupPosition)
				.getBuildingBriefs().get(childPosition).getBuildingName());
		viewHolder.tv_orderSum.setText(orderRecords.get(groupPosition)
				.getBuildingBriefs().get(childPosition).getOrderSum()
				+ "单");
		viewHolder.tv_moneySum.setText(orderRecords.get(groupPosition)
				.getBuildingBriefs().get(childPosition).getMoneySum()
				+ "元");

		return convertView;
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		// TODO Auto-generated method stub
		if (orderRecords.size() > groupPosition) {
			return orderRecords.get(groupPosition).getBuildingBriefs().size();
		} else {
			return 0;
		}

	}

	@Override
	public Object getGroup(int groupPosition) {
		// TODO Auto-generated method stub
		return orderRecords.get(groupPosition).getDate();
	}

	@Override
	public int getGroupCount() {
		// TODO Auto-generated method stub
		return orderRecords.size();
	}

	@Override
	public long getGroupId(int groupPosition) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		GroupHolder groupHolder = null;
		if (convertView == null) {
			groupHolder = new GroupHolder();
			convertView = inflater.inflate(R.layout.group_item, null);
			groupHolder.date = (TextView) convertView
					.findViewById(R.id.tv_date);
			groupHolder.indicator = (ImageView) convertView
					.findViewById(R.id.indicator);
			groupHolder.moneySum = (TextView) convertView
					.findViewById(R.id.moneysum_tv);
			groupHolder.orderSum = (TextView) convertView
					.findViewById(R.id.ordersum_tv);
			convertView.setTag(groupHolder);

		} else {
			groupHolder = (GroupHolder) convertView.getTag();
		}
		if (isExpanded) {
			groupHolder.indicator.setBackgroundResource(R.drawable.group_img_0);
		} else {
			groupHolder.indicator.setBackgroundResource(R.drawable.group_img_1);
		}
		groupHolder.date.setText(orderRecords.get(groupPosition).getDate());
		groupHolder.moneySum.setText("订单总额："
				+ orderRecords.get(groupPosition).getMoneySum());
		groupHolder.orderSum.setText("总订单数："
				+ orderRecords.get(groupPosition).getOrderSum());

		return convertView;
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

	public void process(List<OrderRecord> orderRecords) {
		this.orderRecords.addAll(orderRecords);
		notifyDataSetChanged();
	}

}
