package com.xiaoyintong.app.bean;

import com.google.gson.Gson;

public class Response {
	
	private boolean status;
	private String info;
	private String err;
	private boolean req_status;
	private String req_msg;
	private String req_redirect;
	
//	05-09 13:18:06.002: I/MyResponseHandler(26534):
//	{"status":false,"info":"","err":{"time":1399641482,"msg":{"email":"renhe940318@gmail.com","msg":"您的申请正在处理中，请您耐心等待。如果您任未收到申请的反馈，请联系管理员service@admin.xiaoyintong.com。"},"is_bigerr":false,"bigerr_msg":"","bigerr_redirect":"","is_attack":false,"attack_msg":"","attack_redirect":""}
//	,"req_status":true,"req_msg":"","req_redirect":""}

	public Response(boolean status , String info ,String err)
	{
		this.status = status;
		this.info = info;
		this.err = err;
	}
	
	public Response(boolean status , String info , String err, boolean req_status , String req_msg ,String req_redirect ){
		this(status, info , err);
		this.req_msg = req_msg;
		this.req_status = status;
		this.req_redirect = req_redirect;
	}
	
	public boolean getReqStatus(){
		return req_status;
	}
	public String getReqMsg(){
		return req_msg;
	}
	
	public String getReqRedirect(){
		return req_redirect;
	}
	
	public boolean isSucceeded()
	{
		return this.status;
	}
	
	public String getInfo()
	{
		return this.info;
	}
	
	public String getError()
	{
		return this.err;
	}
	
	@Override
	public String toString(){
		return new Gson().toJson(this);
	}

}
