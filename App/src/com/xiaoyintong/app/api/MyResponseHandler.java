package com.xiaoyintong.app.api;

import org.apache.http.Header;
import org.apache.http.HttpStatus;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.loopj.android.http.BaseJsonHttpResponseHandler;
import com.xiaoyintong.app.bean.MyError;
import com.xiaoyintong.app.bean.Response;
import com.xiaoyintong.app.common.FileUtils;
import com.xiaoyintong.app.common.LogUtil;
import com.xiaoyintong.app.common.UIHelper;

public class MyResponseHandler extends BaseJsonHttpResponseHandler<Response> {

	private static boolean DEBUG = true;

	private static final String TAG = MyResponseHandler.class.getSimpleName();

	public static final int COMPLETE_TOKEN = 1;

	private ResponseListener responseListener;
	private Context context;

	private Gson gson;

	public MyResponseHandler(Context context, ResponseListener responseListener) {
		gson = new Gson();
		this.responseListener = responseListener;
		this.context = context;
	}

	@Override
	public void onFailure(int statusCode, Header[] headers,
			Throwable throwable, String rawJsonData, Response errorResponse) {
		// TODO Auto-generated method stub
		if (DEBUG) {
			LogUtil.debugHeaders(TAG, headers);
			LogUtil.debugStatusCode(TAG, statusCode);
			LogUtil.debugThrowable(TAG, throwable);
			if (errorResponse != null) {
				LogUtil.debugResponse(TAG, rawJsonData);
			}
		}
		responseListener.requestFinished(false);
		// FileUtils.write2SD("statusCode = " + statusCode + "Throwable : " +
		// throwable.toString() );
		UIHelper.showToast(context, "网络异常，请稍后再试");
	}

	@Override
	public void onSuccess(int statusCode, Header[] arg1, String jsonData,
			Response response) {
		// TODO Auto-generated method stub
		// FileUtils.write2SD("statusCode = " + statusCode + " jsonData : " +
		// jsonData);
		if (statusCode == HttpStatus.SC_OK) {
			responseListener.requestFinished(true);
			if (response == null) {
				UIHelper.showToast(context, "数据解析异常");
			} else {
				if (response.isSucceeded()) {
					responseListener.processResponse(response.getInfo());
				} else {
					// FileUtils.write2SD("----Error ----\r\n");
					if (!response.getReqStatus()) {
						UIHelper.showToast(context, response.getReqMsg());
					} else {
						dealWithErr(response.getError());
					}
					// UIHelper.showToast(context, response.getError());
				}
			}
		} else {
			UIHelper.showToast(context, "服务器响应异常，请稍后再试");
			responseListener.requestFinished(false);
		}
	}

	@Override
	protected Response parseResponse(String jsonData, boolean isFailure)
			throws Throwable {
		// TODO Auto-generated method stub
		if (DEBUG) {
			Log.i(TAG, jsonData);
		}
		if (!isFailure) {
			try {
				return gson.fromJson(jsonData, Response.class);
			} catch (Exception e) {
				// TODO: handle exception
				return null;
			}
		} else {
			return null;
		}
	}

	/*
	处理错误*/
	private void dealWithErr(String errJson) {
//		System.out.println("errJson = " + errJson);
		try {
			MyError error = gson.fromJson(errJson, MyError.class);
			if (error.isBigErr()) {

				UIHelper.showToast(context, error.getBigErrMsg());
			} else {
				if (error.isAttack()) {
					UIHelper.showToast(context, error.getAttackMsg());
				} else {
					try {
						Message msg = gson.fromJson(error.getMsg(),
								Message.class);
						UIHelper.showToast(context, msg.email + " " + msg.msg);
					} catch (Exception e) {
						// TODO: handle exception
						e.printStackTrace();
						System.out.println("Error while parse Message ");
					}

				}
			}
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("Error while parse MyError");
			e.printStackTrace();
		}

	}

	protected void setResponseListener(ResponseListener responseListener) {
		this.responseListener = responseListener;
	}

	private class Message {
		private String email = "";
		private String msg;

		public Message() {
			super();
		}

		public Message(String msg) {
			this.msg = msg;
		}

		public Message(String email, String msg) {
			this.email = email;
			this.msg = msg;
		}

		public String getMsg() {
			return msg;
		}

		public String getEmail() {
			return email;
		}
	}

}
