package com.xiaoyintong.app.bean;

import java.io.Serializable;

import org.json.JSONArray;
import org.json.JSONException;

import com.google.gson.Gson;



public class OrderUnit implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -1161240779411613759L;
	private String tid;
	private int upload_time_t;
	private String upload_time;
	private String member_type;
	private String file_name;
	private String order_type;
	private String user;//用户名
	private int pos_loc;
	private int pos_dorm;
	private int pos_build;
	private String message = "";//留言
	private String file_msg;
	private String money;
	private String loc;
	private String loc_t;
	private String send_time;
	private String order_status;
	
	private boolean isPrinted = false;//是否打印
	private boolean isDelivering = false;//是否正在派送
	private String p_time = "";//打印时间
	private String delivering_t = "";//取货时间
	private String d_time = "";//派送时间
	
//	private String page_size;
//	private int page_number;//份数
//	private String page_type;//单双面
//	
//	private int file_others;//单面打印件的个数
//	private String money;//金额
//	private String send_time;//收货时间
//	private int binder;//订书针数
	private int isDelivered = 0;//派送完毕
//	private int type;//判断打印件的类型，登陆打印或未登录的打印件（2/4）
//	private int isPrinted;
	
	private boolean dealStatusCalled = false;
	
	public OrderUnit()
	{
		super();
		order_status = "[\"1\", \"\", \"1\", \"\", \"1\", \"\"]";
	}
//	public OrderUnit(String tid , String member_type , String upload_time , String file_name ){
//		
//	}
//	public OrderUnit(int tid ,int upload_time, String user ,String phone , int pos_loc , int pos_build ,int pos_dorm , String sendMsg ,String file_name ,int page_number ,String money
//			,int send_time , int binder ,String page_type ,int type , int file_others , String page_size)
//	{
//		this.tid = tid;
//		this.upload_time = upload_time;
//		this.user = user;
//		this.phone = phone;
//		this.pos_loc = pos_loc;
//		this.pos_build = pos_build;
//		this.pos_dorm = pos_dorm;
//		this.message = sendMsg;
//		this.file_name = file_name;
//		this.page_number = page_number;
//		this.binder = binder;
//		this.money = money;
//		this.page_type = page_type;
//		this.send_time = send_time;
//		this.type = type;
//		this.file_others = file_others;
//		this.page_size = page_size;
//	}
	
	public boolean isPrinted(){
		if (!dealStatusCalled) {
			dealOrderStatus();
		}
		return isPrinted;
	}
	
	public boolean isDelivering(){
		if (!dealStatusCalled) {
			dealOrderStatus();
		}
		return isDelivering;
	}
	
	
	private void dealOrderStatus(){
		JSONArray jsonArray = null;
		try {
			jsonArray = new JSONArray(order_status);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			isPrinted = jsonArray.getString(0).equals("1");
			isDelivering = jsonArray.getString(2).equals("1");
			if (jsonArray.getString(4).equals("1")) {
				isDelivered = 2;
			}
			
			p_time = jsonArray.getString(1);
			delivering_t = jsonArray.getString(3);
			d_time = jsonArray.getString(5);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	
	public void setFileMsg(String msg){
		this.file_msg = msg;
	}
	
	public String getUserName(){
		return this.user.split(",")[0];
	}
	
	public String getMemberType(){
		return member_type;
	}
	public String getOrderType(){
		return order_type;
	}
	
	public String getFileMsg(){
		return file_msg;
	}
	
	public String getTid()
	{
		return this.tid;
	}
	public void setTid(String tid)
	{
		this.tid = tid;
	}
	public String getUser()
	{
		return this.user;
	}
	public void setUser(String user)
	{
		this.user = user;
	}
	
	public void setSendTime(String sendTime){
		this.send_time = sendTime;
	}
	
	public String getPhone()
	{
		return this.user.split(",")[1];
	}
	
	public void setLocT(String loc_t){
		this.loc_t = loc_t;
	}
	
	public int getLoc()
	{
		pos_loc = Integer.valueOf(loc_t.split(",")[0]);
		return this.pos_loc;
	}
	public void setLoc(int loc)
	{
		this.pos_loc = loc;
	}
	
	public int getBuildingNum()
	{
		pos_build = Integer.valueOf(loc_t.split(",")[1]);
		return this.pos_build;
	}
	public void setBuildingNum(int buildingNum)
	{
		this.pos_build = buildingNum;
	}
	
    public int getDormNum()
    {
    	pos_dorm = Integer.valueOf(loc_t.split(",")[2]);
    	return this.pos_dorm;
    }
    public void setDormNum(int dormNum)
    {
    	this.pos_dorm = dormNum;
    }
    
    public String getNote()
    {
    	return this.message;
    }
    public void setNote(String note)
    {
    	this.message = note;
    }
    
    public String getFileName()
    {
    	return this.file_name;
    }
    public void setFileName(String file_name)
    {
    	this.file_name = file_name;
    }
    
//    public String getPageNumber()
//    {
//    	return this.file_msg.split(" ")[2];
//    }
//    
//    public String getBinder()
//    {
//    	return this.file_msg.split(" ")[4];
//    }
    
    public String getMoney()
    {
    	return this.money;
    }
    public void setMoney(String money)
    {
    	this.money = money;
    }
    
    public String getSendTime()
    {
    	return send_time;
    }
    
//    
//    public String getPageType()
//    {
//    	return this.file_msg.split(" ")[0];
//    }
//    public void setPageType(String page_type)
//    {
//    	this.page_type = page_type;
//    }
    
    public void setIsDelivered(int isDelivered)
    {
    	this.isDelivered = isDelivered;
    }
    public int getDelivered()
    {
    	return this.isDelivered;
    }
    
    public boolean isDelivered()
    {
    	if (dealStatusCalled) {
			dealOrderStatus();
		}
    	return this.isDelivered == 2;
    }
    
    public String getUploadTime()
    {
    	return this.upload_time;
    }
    
    public void setUploadTime(String uploadTime)
    {
    	this.upload_time = uploadTime;
    }
    
    
//    public String getPageSize()
//    {
//    	System.out.println("file_msg = " + file_msg);
//    	return this.file_msg.split(" ")[1];
//    }
    
    @Override 
    public String toString()
    {
    	return new Gson().toJson(this);
    }

}
