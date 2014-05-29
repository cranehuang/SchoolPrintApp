package com.xiaoyintong.app.bean;

import com.xiaoyintong.app.common.Arith;
import com.xiaoyintong.app.common.MyConst;

public class BuildingBrief {
	
	private int locNum;
	private int buildingNum;
	private int orderSum;
	private double moneySum;
	
	public BuildingBrief(int loc , int build , double moneySum)
	{
		this.locNum = loc;
		this.buildingNum = build;
		this.orderSum = 1;
		this.moneySum = moneySum;
	}
	
	public int getLocNum()
	{
		return this.locNum;
	}
	public int getBuildingNum()
	{
		return this.buildingNum;
	}
	public int getOrderSum()
	{
		return this.orderSum;
	}
	
	public double getMoneySum()
	{
		return this.moneySum;
	}
	
	public void addMoney(double money)
	{
		orderSum += 1;
		moneySum = Arith.add(moneySum, money);
	}
	
	public String getBuildingName()
	{
		String locName = "";
		switch (locNum) {
		case MyConst.NO_YUNYUAN:
			locName = "韵苑";
			break;
		case MyConst.NO_ZISONG:
			locName = "紫菘";
			break;
		case MyConst.NO_QINYUAN:
			locName = "沁苑";
			break;
		default:
			break;
		}
		
		return locName + buildingNum + "栋";
	}

}
