package com.xiaoyintong.app.bean;

import com.xiaoyintong.app.common.MyConst;



public class Location {
	
	private int l;//区域编号
	private int b;//该区域楼栋号
	
	public Location(int l , int b)
	{
		this.l = l;
		this.b = b;
	}
	
	public int getLocationNum()
	{
		return l;
	}
	
	public String getLoacationName()
	{
		String locationName = null ;
		switch (l) {
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
		
		return locationName;
	}
	
	public int getBuildingNum()
	{
		return b;
	}
	
	public String getBulidingName()
	{
			return getLoacationName() + b + "栋";
	}

}
