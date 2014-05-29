package com.xiaoyintong.app.bean;

import com.xiaoyintong.app.common.MyConst;


public class SimpleOrder {
	
	private int location;
	private int building;
	private int count;//订单数量
	private double money;
	private int isDelivered;
	private boolean hasOrder = false;
	
	public SimpleOrder()
	{
		super();
	}
	
	public SimpleOrder(int location , int build)
	{
		this.location = location;
		this.building = build;
		count = 0;
		money = 0;
		isDelivered = 0;
	}
	
	public SimpleOrder(int location , int builidingNum , int count ,double money , int isDelivered)
	{
		this.location = location;
		this.building = builidingNum;
		this.count = count;
		this.money = money;
		this.isDelivered = isDelivered;
	}
	
	public void setHasOrder(boolean hasOrder){
		this.hasOrder = hasOrder;
	}
	public boolean hasOrder(){
		return hasOrder;
	}
	
	public void setLocation(int location)
	{
		this.location = location;
	}
	public int getLocation()
	{
		return this.location;
	}
	
	public void setBuild(int build)
	{
		this.building = build;
	}
	public int getBuild()
	{
		return this.building;
	}
	
	public void setCount(int count)
	{
		this.count = count;
	}
	public int getCount()
	{
		return this.count;
	}
	
	public void setMoney(double money)
	{
		this.money = money;
	}
	public double getMoney()
	{
		return this.money;
	}
	
	public void setIsDelivered(int isDelivered)
	{
		this.isDelivered = isDelivered;
	}
	public boolean isDelivered()
	{
		return this.isDelivered == 1;
	}
	
	public String getBuildingName()
	{
		String locationName = "";
		switch (location) {
		case MyConst.NO_YUNYUAN:
			locationName = "韵苑";
			break;
		case MyConst.NO_ZISONG:
			locationName = "紫菘";
			break;
		case MyConst.NO_QINYUAN:
			locationName = "沁苑";
			break;
		default:
			break;
		}
		return locationName + building + "栋";
	}
	
	public boolean accept(SimpleOrder simpleOrder)
	{
		
		if (building == simpleOrder.getBuild() && location == simpleOrder.getLocation()) {
			money = simpleOrder.getMoney();
			count = simpleOrder.getCount();
			return true;
		}else {
			return false;
		}
	}
	
	public String toString()
	{
		return "The SimpleOrder : " + " location = " + location +  "build = " + building + "; count = " + count + "; money = " + money; 
	}

}
