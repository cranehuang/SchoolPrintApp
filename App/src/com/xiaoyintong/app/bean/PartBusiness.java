package com.xiaoyintong.app.bean;

import java.util.List;

public class PartBusiness {
	
	private int part_name;
	private List<SimpleOrder> part;
	
	public PartBusiness(int part_name , List<SimpleOrder> part)
	{
		this.part_name = part_name;
		this.part = part;
	}
	
	public int getPartName()
	{
		return this.part_name;
	}
	
	public List<SimpleOrder> getSimpleOrders()
	{
		return this.part;
	}

}
