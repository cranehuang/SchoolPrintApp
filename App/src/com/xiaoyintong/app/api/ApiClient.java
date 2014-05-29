package com.xiaoyintong.app.api;





import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.xiaoyintong.app.AppException;
import com.xiaoyintong.app.common.MD5;

public class ApiClient {
	
	private static final String TAG = ApiClient.class.getSimpleName();
	
	private static final int DEFAULT_TIME = 10 * 1000;

	private static final String BASE_URL = "http://app.xiaoyintong.com/";

	private static AsyncHttpClient client = new AsyncHttpClient();

	
	public static void get(String url, RequestParams params,
			AsyncHttpResponseHandler responseHandler) {
		client.get(getAbsoluteUrl(url), params, responseHandler);
	}

	public static void post(String url, RequestParams params,
			AsyncHttpResponseHandler responseHandler) {
		
		Log.i(TAG, "url = " + getAbsoluteUrl(url));
		client.setTimeout(DEFAULT_TIME);
		client.post(getAbsoluteUrl(url), params, responseHandler);
	}

	private static String getAbsoluteUrl(String relativeUrl) {
		return BASE_URL + relativeUrl;
	}

	public static AsyncHttpClient getMyClient() {
		return client;
	}

	/**
	 * 用户登录验证
	 * 
	 * @param account
	 * @param pwd
	 * @param responseHandler
	 * @throws AppException
	 */
	public static void login(String account, String password,
			AsyncHttpResponseHandler responseHandler) {
		RequestParams params = new RequestParams();
		params.put("email", account);
		params.put("password", MD5.getMD5(password));
		ApiClient.post(URLs.LOGIN_URL, params, responseHandler);
	}

	
	public static void resetPassword(String uid , String oldPwd , String newPwd , AsyncHttpResponseHandler responseHandler)
	{
		RequestParams params = new RequestParams();
		params.put("uid", uid);
		params.put("old_pwd", MD5.getMD5(oldPwd));
		params.put("new_pwd", MD5.getMD5(newPwd));
		ApiClient.post(URLs.RESET_PWD_URL, params, responseHandler);
	}
	
	/**
	 * 获取分区订单简况
	 */
	public static void getSimpleOrders(String uid,
			AsyncHttpResponseHandler responseHandler) {
		RequestParams params = new RequestParams();
		params.put("uid", uid);
		ApiClient.post(URLs.GET_SIMPLEORDER_URL, params, responseHandler);
	}

	/**
	 * 分区代理获取派送信息
	 */
	public static void getOrders(String uid,
			AsyncHttpResponseHandler responseHandler) {
		RequestParams params = new RequestParams();
		params.put("uid", uid);
		ApiClient.post(URLs.GET_ORDERS_URL, params, responseHandler);
	}

	/**
	 * 派送员发送确认信息
	 */
	public static void sendMessage(String info,
			AsyncHttpResponseHandler responseHandler) {
		RequestParams params = new RequestParams();
		params.put("info", info);
		ApiClient.post(URLs.SEND_MESSAGE_URL, params, responseHandler);
	}
	
	/**
	 * 获取分区结构*/
	public static void getSubregionStruct(String uid, AsyncHttpResponseHandler responseHandler)
	{
		RequestParams params = new RequestParams();
		params.put("uid", uid);
		ApiClient.post(URLs.GET_SUBREGION_URL, params, responseHandler);
	}
	
	/**
	 * 向派送员发送订单抵达信息
	 * @param uid
	 * @param loc 分区号
	 * @param responseHandler
	 * */
	public static void sendDeliveryMsg(String info, AsyncHttpResponseHandler responseHandler)
	{
		RequestParams params = new RequestParams();
		params.put("info", info); 
		ApiClient.post(URLs.DELIVERY_URL, params, responseHandler);
	}
}
