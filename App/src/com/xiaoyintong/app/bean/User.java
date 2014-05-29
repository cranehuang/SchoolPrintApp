package com.xiaoyintong.app.bean;

import java.util.ArrayList;
import java.util.List;







public class User {
	
	public final static int GENERAL_AGENT = 1001;
	public final static int SUB_AGENT = 1000;
	
	private String uid;
	private String email;
	private String username;
	private String password;
	private String phone;
	private boolean isRememberMe = true;
	private List<Location> location = new ArrayList<Location>();
	private String category;//类别，区分楼栋代理和总代理
	private boolean hasSetTags = false;
	
	public User(){}
	
	public User(String uid , String username ,String email,String password ,String phone , List<Location> location , String category)
	{
		this.uid = uid;
		this.email = email;
		this.username = username;
		this.password = password;
		this.phone = phone;
		this.location = location;
		this.category = category;
	}
	
	public void setUid(String uid)
	{
		this.uid = uid;
	}
	public String getUid()
	{
		return this.uid;
	}
	
	public void setUserName(String username){
		this.username = username;
	}
	public String getUserName()
	{
		return this.username;
	}
	
	public void setPassword(String password)
	{
		this.password = password;
	}
	public String getPassword()
	{
		return this.password;
	}
	
	public void setEmail(String email)
	{
		this.email = email;
	}
	public String getEmail()
	{
		return this.email;
	}
	
	public void setphone(String phone)
	{
		this.phone = phone;
	}
	public String getphone()
	{
		return this.phone;
	}
	
	public void setRememberMe(boolean isRememberMe)
	{
		this.isRememberMe = isRememberMe;
	}
	public boolean isRememberMe()
	{
		return this.isRememberMe;
	}
	
	public void setLocation(List<Location> location)
	{
		this.location = location;
	}
	public List<Location> getLocation()
	{
		return this.location;
	}

	public void setCategory(String category)
	{
		this.category = category;
	}
	public int getCategory()
	{
		return Integer.valueOf(this.category);
	}
	
	public void setHasTags(boolean hasTags)
	{
		this.hasSetTags = hasTags;
	}
	
	public boolean hasSetTags()
	{
		return this.hasSetTags;
	}

	
}
