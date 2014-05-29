package com.xiaoyintong.app.bean;

public class PushMessage {
	
//	extras = {'type': type}
//	type = user_commit 店家提交订单
//	type = user_upload  用户上传订单
//	type = admin_part_change 分区结构改变
//	type = admin_part_send “佳哥点击发送”

	public static final String USER_COMMIT = "shop_complete";//店家下载订单并打印
	public static final String USER_UPLOAD = "user_upload";
	public static final String USER_DELETE = "user_delete";
	public static final String ADMIN_PART_CHANGE = "extra_modify_partition";
	public static final String ADMIN_PART_SEND = "part_admin_send";

}
