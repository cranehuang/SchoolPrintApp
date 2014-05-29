package com.xiaoyintong.app.bean;

public class IdAndType {
	private int task_id;
	private int type;
	public IdAndType(int task_id , int type)
	{
		this.task_id = task_id;
		this.type = type;
	}
	
	public int getId()
	{
		return this.task_id;
	}
	public int getType()
	{
		return this.type;
	}
}
