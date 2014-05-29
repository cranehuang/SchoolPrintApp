package com.xiaoyintong.app.bean;

import com.google.gson.Gson;

public class MyError {
//	{"time":1399641482,"msg":{"email":"renhe940318@gmail.com","msg":"您的申请正在处理中，请您耐心等待。如果您任未收到申请的反馈，请联系管理员
//	。"},"is_bigerr":false,"bigerr_msg":"","bigerr_redirect":"","is_attack":false,"attack_msg":"","attack_redirect":""}
	
	private long time;
	private String msg;
	private boolean is_bigerr;
	private String bigerr_msg;
	private String bigerr_redirect;
	
	private boolean is_attack;
	private String attack_msg;
	private String attack_redirect;
	
	public MyError(){
		super();
	}
	
	public MyError(long time , String msg , boolean is_bigerr , String bigerr_msg , String bigerr_redirect , boolean is_attack , String attack_msg , String attack_redirect){
		this.time = time;
		this.msg = msg;
		this.is_bigerr = is_bigerr;
		this.bigerr_msg = bigerr_msg;
		this.bigerr_redirect = bigerr_redirect;
		this.is_attack = is_attack;
		this.attack_msg = attack_msg;
		this.attack_redirect = attack_redirect;
	}
	
	public long getTime(){
		return time;
	}
	
	public String getMsg(){
		return msg;
	}
	public boolean isBigErr(){
		return is_bigerr;
	}
	public String getBigErrMsg(){
		return bigerr_msg;
	}
	public boolean isAttack(){
		return is_attack;
	}
	public String getAttackMsg(){
		return attack_msg;
	}
	
	public String getBigErrRedirect(){
		return bigerr_redirect;
	}
	public String getAttackRedirect(){
		return attack_redirect;
	}
	
	@Override
	public String toString(){
		return new Gson().toJson(this);
	}
	
	
}
