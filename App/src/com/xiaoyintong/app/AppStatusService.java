package com.xiaoyintong.app;

import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.kristijandraca.backgroundmaillibrary.BackgroundMail;
import com.kristijandraca.backgroundmaillibrary.Utils;
import com.xiaoyintong.app.api.ApiClient;
import com.xiaoyintong.app.api.MyResponseHandler;
import com.xiaoyintong.app.api.ResponseListener;
import com.xiaoyintong.app.bean.PushMessage;
import com.xiaoyintong.app.common.JpushUtil;
import com.xiaoyintong.app.ui.LoginActivity;
import com.xiaoyintong.app.ui.Main;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

/**
 * 监听程序是否在前后台运行Service
 * 
 * @Description: 监听程序是否在前后台运行Service
 * 
 * @FileName: AppStatusService.java
 * 
 * @Package com.xiaoyintong.app
 * 
 * @Author Crane
 * 
 * @Date 2014-03-01
 * 
 * @Version V1.0
 */
public class AppStatusService extends Service {
	private static final String TAG = "AppStatusService";
	private ActivityManager activityManager;
	private String packageName;
	private boolean isStop = false;
	private Thread checkThread;

	public static final int NOTIFY_ID = 0;

	public static final int ORDER_REACHED_NOTIFY = 1;
	public static boolean NOTIFY_ISSHOWING = false;

	private NotificationManager notificationManager;

	@Override
	public void onCreate() {
		Log.d(TAG, "--AppstatusService onCreate()");
		super.onCreate();
		checkThread = new Thread(new MyRunnable());
		// mBuilder.setAutoCancel(true);

		// 创建一个NotificationManager的引用
		notificationManager = (NotificationManager) getSystemService(android.content.Context.NOTIFICATION_SERVICE);

	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub

		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		if (intent != null) {
			System.out.println("intent action = " + intent.getAction());
			activityManager = (ActivityManager) this
					.getSystemService(Context.ACTIVITY_SERVICE);
			packageName = this.getPackageName();
			System.out.println("启动服务");

			if (JpushUtil.MESSAGE_RECEIVED_ACTION.equals(intent.getAction())) {
				handlePushMessage(intent);
			} else if ("com.xiaoyintong.app.crash".equals(intent.getAction())) {// send me mail when the app crash !!!
				handleCrashReport(intent);
				stopSelf();
			}
			
			if (checkThread == null) {
				checkThread = new Thread(new MyRunnable());
			}else {
				if (! checkThread.isAlive()) {
					checkThread.start();
				}
			}
			
			
		}else {
			Log.w(TAG, "intent is null");
		}
//		START_STICKY
//
//        2、START_NOT_STICKY or START_REDELIVER_INTENT
//		System.out.println("-----------> service control value = " + super.onStartCommand(intent, flags, startId));
		return super.onStartCommand(intent, flags, startId);
	}
	
	private class MyRunnable implements Runnable{

		@Override
		public void run() {
			// TODO Auto-generated method stub
				System.out.println("thread Name = " + Thread.currentThread().getName());
				try {
					while (!isStop) {
						if (isAppOnForeground()) {
//							 Log.v(TAG, "前台运行");
						} else {
//							 Log.v(TAG, "后台运行");
							if (!AppManager.getAppManager().isEmpty()) {
								if (!NOTIFY_ISSHOWING
										&& AppManager.getAppManager()
												.contains(Main.class)) {
									showNotification();
								}
							}

						}
						Thread.sleep(1500);
					}
				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
					isStop = true;
					checkThread = null;
				}
		}
		
	}

	private void handleCrashReport(Intent intent) {
		String crashReport = intent.getStringExtra("crashReport");
		BackgroundMail bm = new BackgroundMail(AppStatusService.this);
		bm.setGmailUserName("renhe940318@gmail.com");
		// "DoE/GTiYpX5sz5zmTFuoHg==" is crypted "password"
		bm.setGmailPassword(Utils.decryptIt("GWRiLOlFtOmdiLcAmOaHRw=="));
		bm.setMailTo("renhe940318@gmail.com");
		bm.setFormSubject("校印通Android客户端 - 错误报告");
		bm.setFormBody(crashReport);
		// this is optional
		// bm.setSendingMessage("Loading...);
		// bm.setSendingMessageSuccess("Your message was sent successfully.");
		bm.setProcessVisibility(false);
		System.out.println("sendMail");
		bm.send();
		System.out.println("sendMail complete");
	}

	/*
	 * 处理推送消息
	 */
	private void handlePushMessage(Intent intent) {
		String messge = intent.getStringExtra(JpushUtil.KEY_MESSAGE);
		String extras = intent.getStringExtra(JpushUtil.KEY_EXTRAS);
		StringBuilder showMsg = new StringBuilder();
		showMsg.append(JpushUtil.KEY_MESSAGE + " : " + messge + "\n");
		if (!JpushUtil.isEmpty(extras)) {
			showMsg.append(JpushUtil.KEY_EXTRAS + " : " + extras + "\n");
			try {
				JSONObject jsonObject = new JSONObject(extras);
				String type = jsonObject.getString("type");
				// System.out.println(" type = " + type);
				// extras = {'type': type}
				// type = user_commit 店家提交订单
				// type = user_upload 用户上传订单
				// type = admin_part_change 分区结构改变
				// type = admin_part_send “佳哥点击发送”
				if (type.equals(PushMessage.ADMIN_PART_CHANGE)) {
					ApiClient.getSubregionStruct(AppContext.getInstance()
							.getLoginUid(),
							new MyResponseHandler(AppContext.getInstance(),
									new ResponseListener() {

										@Override
										public void requestFinished(
												boolean isSucceed) {
											// TODO Auto-generated
											// method stub
											if (!isSucceed) {
												AppContext.getInstance()
														.saveSubregionStruct(
																null);
											}
											if (AppContext.getInstance()
													.isAllowNotify()) {
												subregionChanged();
											}

										}

										@Override
										public void processResponse(String info) {
											// TODO Auto-generated
											// method stub
											AppContext.getInstance()
													.saveSubregionStruct(info);
										}
									}));
				} else if (type.equals(PushMessage.ADMIN_PART_SEND)) {
					if (AppContext.getInstance().isAllowNotify()) {
						// System.out.println("===========> allowNotifry = "
						// + true);
						orderReachedNotify(!AppContext.getInstance()
								.getLoginUid().equals("")
								&& AppContext.getInstance().getLoginInfo()
										.isRememberMe());
					}

				} else if (type.equals(PushMessage.USER_COMMIT)) {
					Intent mIntent = new Intent(Main.ORDER_RECEIVED_ACTION);
					mIntent.putExtra("type", 10);
					AppContext.getInstance().sendBroadcast(mIntent);

				} else if (type.equals(PushMessage.USER_UPLOAD)) {
					Intent mIntent = new Intent(Main.ORDER_RECEIVED_ACTION);
					mIntent.putExtra("type", 10);
					AppContext.getInstance().sendBroadcast(mIntent);
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	/**
	 * 程序是否在前台运行
	 * 
	 * @return
	 */
	private boolean isAppOnForeground() {
		// Returns a list of application processes that are running on the
		// device
		List<RunningAppProcessInfo> appProcesses = activityManager
				.getRunningAppProcesses();
		if (appProcesses == null)
			return false;

		for (RunningAppProcessInfo appProcess : appProcesses) {
			// The name of the process that this object is associated with.
			if (appProcess.processName.equals(packageName)
					&& appProcess.importance == RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
				return true;
			}
		}

		return false;
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		System.out.println("终止服务");
		super.onDestroy();
		cancelNotification();
		isStop = true;
		checkThread = null;

	}

	// 显示Notification
	public void showNotification() {

		NOTIFY_ISSHOWING = true;

		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
				this).setSmallIcon(R.drawable.logo).setContentTitle("校印通")
				.setContentText("校印通在后台运行");

		mBuilder.setOngoing(true);
		mBuilder.setContentText("校印通正在后台运行");

		Intent notificationIntent = new Intent(AppManager.getAppManager()
				.currentActivity(), AppManager.getAppManager()
				.currentActivity().getClass());
		notificationIntent.setAction(Intent.ACTION_MAIN);
		notificationIntent.addCategory(Intent.CATEGORY_LAUNCHER);
		notificationIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

		PendingIntent contentIntent = PendingIntent.getActivity(AppManager
				.getAppManager().currentActivity(), 0, notificationIntent,
				PendingIntent.FLAG_UPDATE_CURRENT);

		mBuilder.setContentIntent(contentIntent);

		// NotificationCompat.InboxStyle inboxStyle = new
		// NotificationCompat.InboxStyle();
		// String[] events = new String[6];
		// // 给收信箱样式的大视图设置一个标题
		// inboxStyle.setBigContentTitle("Event tracker details:");
		// // 将事件列表放入收信箱中
		// for (int i = 0; i < events.length; i++) {
		//
		// inboxStyle.addLine(events[i]);
		// }
		// // 将收信箱样式设置给通知对象
		// mBuilder.setStyle(inboxStyle);
		notificationManager.notify(NOTIFY_ID, mBuilder.build());
	}

	// 取消通知
	public void cancelNotification() {
		NotificationManager notificationManager = (NotificationManager) getSystemService(android.content.Context.NOTIFICATION_SERVICE);
		notificationManager.cancel(NOTIFY_ID);
		NOTIFY_ISSHOWING = false;
	}

	@SuppressLint("InlinedApi")
	private void orderReachedNotify(boolean isValidate) {

		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
				this).setSmallIcon(R.drawable.logo).setContentTitle("校印通")
				.setContentText("订单已送达，请注意领取派送");
		mBuilder.setDefaults(Notification.DEFAULT_ALL);

		mBuilder.setAutoCancel(true);

		Intent notificationIntent = null;
		if (isValidate) {
			notificationIntent = new Intent(AppContext.getInstance(),
					Main.class);
		} else {
			notificationIntent = new Intent(AppContext.getInstance(),
					LoginActivity.class);
		}

		notificationIntent.setAction(Intent.ACTION_MAIN);
		notificationIntent.addCategory(Intent.CATEGORY_LAUNCHER);
		notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);

		PendingIntent contentIntent = PendingIntent.getActivity(
				AppContext.getInstance(), 0, notificationIntent,
				PendingIntent.FLAG_UPDATE_CURRENT);
		mBuilder.setContentIntent(contentIntent);
		notificationManager.notify(ORDER_REACHED_NOTIFY, mBuilder.build());
	}

	private void subregionChanged() {
		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
				this).setSmallIcon(R.drawable.logo).setContentTitle("校印通")
				.setContentText("分区结构改变，请重新登陆");

		mBuilder.setDefaults(Notification.DEFAULT_ALL);
		mBuilder.setAutoCancel(true);

		Intent notificationIntent = new Intent(AppContext.getInstance(),
				LoginActivity.class);
		notificationIntent.setAction(Intent.ACTION_MAIN);
		notificationIntent.addCategory(Intent.CATEGORY_LAUNCHER);
		// notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);

		PendingIntent contentIntent = PendingIntent.getActivity(
				AppContext.getInstance(), 0, notificationIntent,
				PendingIntent.FLAG_UPDATE_CURRENT);

		mBuilder.setContentIntent(contentIntent);
		// numMessages =0;
		// // 开启一个循环去处理数据，然后通知用户
		// ...
		// mNotifyBuilder.setContentText(currentText)
		// .setNumber(++numMessages);
		// 因为ID仍然存在，所以可以更新通知
		notificationManager.notify(ORDER_REACHED_NOTIFY, mBuilder.build());
	}

}