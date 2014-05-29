package com.xiaoyintong.app.bean;

import java.util.List;

public class CompleteMessage {
	
	private String uid;
	private List<IdAndType> task;
	
	public CompleteMessage(String uid,List<IdAndType> idAndTypes)
	{
		this.uid = uid;
		this.task = idAndTypes;
	}
	

}
