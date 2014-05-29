package com.xiaoyintong.app.bean;

import java.util.List;

public class ResultForSubAgent {
	
	private List<OrderUnit> disable;
	private List<OrderUnit> usable;
	
	public ResultForSubAgent(List<OrderUnit> noprint , List<OrderUnit> print)
	{
		this.disable = noprint;
		this.usable = print;
	}
	
	public List<OrderUnit> getNoPrints()
	{
		return this.disable;
	}
	public List<OrderUnit> getOrderUnits()
	{
		return this.usable;
	}

}
