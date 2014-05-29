package com.xiaoyintong.app.bean;

import java.io.Serializable;
import java.util.List;

import com.xiaoyintong.app.common.Arith;

public class Order implements Serializable , Comparable<Order>{

	/**
	 * 
	 */
	private static final long serialVersionUID = -7994008711841948780L;
	
	private List<OrderUnit> orderUnits;
	
	private boolean isRequesting;

	public Order(List<OrderUnit> orderUnits) {
		this.orderUnits = orderUnits;
		isRequesting = false;
	}

	public int getOrderSum() {
		return orderUnits.size();
	}

	public String getMoneySum() {
		double sum = 0;
		for (int i = 0; i < orderUnits.size(); i++) {
//			System.out.println("money = " + orderUnits.get(i).getMoney());
			if (!orderUnits.get(i).getMoney().equals("")) {
				sum = Arith.add(sum, Double.valueOf(orderUnits.get(i).getMoney()));
			}
			
		}
		return String.valueOf(sum);
	}

	public int getBuildingNum() {
		
		return orderUnits.get(0).getBuildingNum();
	}

	public String getRoomNum() {
		return String.valueOf(orderUnits.get(0).getDormNum());
	}
	
	public String getUserName()
	{
		return orderUnits.get(0).getUserName();
	}
	
	
	public List<OrderUnit> getOrderUnits()
	{
		return this.orderUnits;
	}
	
	public void setDeliveryState(int state)
	{
		for (int i = 0; i < orderUnits.size(); i++) {
			orderUnits.get(i).setIsDelivered(state);
		}
	}
	
	public int getDeliveryState()
	{
		return orderUnits.get(0).getDelivered();
	}
	
	public void setRequestState(boolean isRequesting)
	{
		this.isRequesting = isRequesting;
	}
	public boolean isRequesting()
	{
		return this.isRequesting;
	}
	
	public boolean isDelivering(){
		return orderUnits.get(0).isDelivering();
	}
	//判断订单是否接纳某个订单元
	public boolean accept(OrderUnit orderUnit)
	{
		boolean accepted = false;
		if (orderUnit.getDormNum() == orderUnits.get(0).getDormNum() && orderUnit.getUserName().equals(orderUnits.get(0).getUserName())) {
			boolean isNew = true;
//			System.out.println("------------> in accept " + orderUnit.toString());
			for (int i = 0; i < orderUnits.size(); i++) {
				if (orderUnit.getTid().equals(orderUnits.get(i).getTid())) {
//					System.out.println("! isNew " + orderUnit.toString());
					isNew = false;
					accepted = true;
					break;
				}
			}
			if (isNew) {
				if (orderUnit.isDelivering() == orderUnits.get(0).isDelivering() && getDeliveryState() == orderUnit.getDelivered()) {
					accepted = true;
					orderUnits.add(orderUnit);
				}
			}
		}
		return accepted;
	}

	@Override
	public int compareTo(Order another) {
		// TODO Auto-generated method stub
		return getRoomNum().compareTo(another.getRoomNum());
	}

}
