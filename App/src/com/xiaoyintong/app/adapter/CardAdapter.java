package com.xiaoyintong.app.adapter;

import java.util.List;

import com.xiaoyintong.app.R;
import com.xiaoyintong.app.bean.OrderUnit;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class CardAdapter extends BaseAdapter {

	private Context context;
	private List<OrderUnit> orderUnits;
	private LayoutInflater inflater;

	private boolean isDelivering;
	private boolean isDelivered;
	private boolean isPrinted;

	public CardAdapter(Context context, List<OrderUnit> orderUnits) {
		this.context = context;
		this.orderUnits = orderUnits;
		inflater = LayoutInflater.from(this.context);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return orderUnits.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return orderUnits.get(arg0);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		RelativeLayout layout = (RelativeLayout) inflater.inflate(
				R.layout.order_detail_item, null);
		// layout layout = (layout) layout.findViewById(R.id.layout);
		TextView title = (TextView) layout.findViewById(R.id.title);
		TextView money = (TextView) layout.findViewById(R.id.money);
		TextView fileMsg = (TextView) layout.findViewById(R.id.file_msg);
		// TextView side = (TextView) layout.findViewById(R.id.side);
		// TextView stapleSum = (TextView) layout.findViewById(R.id.staple_num);
		// TextView fileOthers = (TextView)
		// layout.findViewById(R.id.file_others);
		StringBuilder titleBuilder = new StringBuilder();
		TextView userName = (TextView) layout.findViewById(R.id.user_name);
		TextView phoneNum = (TextView) layout.findViewById(R.id.phone_num);
		TextView sendTime = (TextView) layout.findViewById(R.id.send_time);
		TextView note = (TextView) layout.findViewById(R.id.note);
		// System.out.println(orderUnits.get(position).getFileName());
		isDelivered = orderUnits.get(position).isDelivered();
		isDelivering = orderUnits.get(position).isDelivering();
		isPrinted = orderUnits.get(position).isPrinted();

		titleBuilder.append(orderUnits.get(position).getFileName() + "\n");
		if (isDelivered) {
			titleBuilder.append("已派送");
		} else {
			if (isDelivering) {
				titleBuilder.append("正在派送");
			} else {
				if (isPrinted) {
					titleBuilder.append("等待派送");
				} else {
					titleBuilder.append("正在打印");
				}
			}
		}
		titleBuilder.append( "  单号:" + orderUnits.get(position).getTid());
		title.setText(titleBuilder.toString());
		money.setText(orderUnits.get(position).getMoney() + "元");
		// side.setText(orderUnits.get(position).getPageSize()+
		// orderUnits.get(position).getPageType() +
		// orderUnits.get(position).getPageNumber() + "份");
		// fileOthers.setText(orderUnits.get(position).getFileOthers() + "个每面");
		// stapleSum.setText(orderUnits.get(position).getBinder() + "个订书针");
		sendTime.setText(orderUnits.get(position).getSendTime());
		userName.setText(orderUnits.get(position).getUserName());
		phoneNum.setText(orderUnits.get(position).getPhone());
		note.setText(orderUnits.get(position).getNote());
		fileMsg.setText(
				orderUnits.get(position).getFileMsg() + " "
				+ orderUnits.get(position).getUploadTime());

		return layout;
	}

}
