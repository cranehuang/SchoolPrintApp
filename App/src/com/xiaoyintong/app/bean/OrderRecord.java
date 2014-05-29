package com.xiaoyintong.app.bean;

import java.util.List;

import com.xiaoyintong.app.common.Arith;
import com.xiaoyintong.app.common.StringUtils;

public class OrderRecord {
	
	private int orderSum;
	private double moneySum;
	private List<BuildingBrief>  buildingBriefs;
	private long date;
	
	public OrderRecord()
	{
		super();
	}
	public OrderRecord(List<BuildingBrief> buildingBriefs)
	{
		this.buildingBriefs = buildingBriefs;
		date = System.currentTimeMillis();
		orderSum = 0;
		moneySum = 0;
	}
	
	public List<BuildingBrief> getBuildingBriefs()
	{
		return this.buildingBriefs;
	}
	
	public int getOrderSum()
	{
		orderSum = 0;
		for (int i = 0; i < buildingBriefs.size(); i++) {
			orderSum += buildingBriefs.get(i).getOrderSum();
		}
		return this.orderSum;
	}
	
	public double getMoneySum()
	{
		moneySum = 0;
		for (int i = 0; i < buildingBriefs.size(); i++) {
			moneySum = Arith.add(moneySum, buildingBriefs.get(i).getMoneySum());
		}
		return this.moneySum;
	}
	
	public String getDate()
	{
		return StringUtils.friendlyTime(date);
	}
	
	public void setDate(long date)
	{
		this.date = date;
	}

}
