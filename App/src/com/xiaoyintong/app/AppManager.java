package com.xiaoyintong.app;

import java.util.Iterator;
import java.util.Stack;

import com.xiaoyintong.app.ui.LoginActivity;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.util.Log;

/**
 * 应用程序Activity管理类：用于Activity管理和应用程序退出
 * @version 1.0
 * @created 2012-3-21
 */
public class AppManager {
	
	private final static String TAG = "AppMagager";
	private static Stack<Activity> activityStack;
	private static AppManager instance;
	
	private AppManager(){}
	/**
	 * 单一实例
	 */
	public static AppManager getAppManager(){
		if(instance==null){
			instance=new AppManager();
			activityStack=new Stack<Activity>();
		}
		return instance;
	}
	/**
	 * 添加Activity到堆栈
	 */
	public void addActivity(Activity activity){
//		if(activityStack==null){
//			activityStack=new Stack<Activity>();
//		}
		activityStack.add(activity);
	}
	/**
	 * 获取当前Activity（堆栈中最后一个压入的）
	 */
	public Activity currentActivity(){
		Activity activity=activityStack.lastElement();
		return activity;
	}
	/**
	 * 结束当前Activity（堆栈中最后一个压入的）
	 */
	public void finishActivity(){
		Activity activity=activityStack.lastElement();
//		Log.d(TAG, activity.getClass().toString());
		finishActivity(activity);
	}
	/**
	 * 结束指定的Activity
	 */
	public void finishActivity(Activity activity){
		if(activity!=null){
			activityStack.remove(activity);
			activity.finish();
			activity=null;
		}
	}
	/**
	 * 结束指定类名的Activity
	 */
	public void finishActivity(Class<?> cls){
		for (Activity activity : activityStack) {
			if(activity.getClass().equals(cls) ){
				finishActivity(activity);
			}
		}
	}
	
	public boolean contains(Class<?> cls)
	{
		boolean contains = false;
		for (Activity activity : activityStack) {
			if (activity.getClass().equals(cls)) {
				contains = true;
				break;
			}
		}
		return contains;
	}
	
	/**
	 * 判断当前栈中有无activity*/
	public boolean isEmpty()
	{
		return activityStack.isEmpty();
	}
	
	public void finishOthersExceptLogin()
	{
		Iterator<Activity> iterator = activityStack.iterator();
		while (iterator.hasNext()) {
			Activity activity = (Activity) iterator.next();
			
			if (!activity.getClass().toString().equals(LoginActivity.class.toString())) {
				Log.d(TAG, activity.getClass().toString());
				activity.finish();
				iterator.remove();
			}
			
		}
//		for (int i = 0 , size = activityStack.size(); i < size; i++) {
//			if (! activityStack.get(i).getClass().equals(LoginActivity.class)) {
//				activityStack.get(i).finish();
//				activityStack.remove(i);
//			}
//		}
	}
//	public void removeAll()
//	{
//		activityStack.clear();
//	}
	/**
	 * 结束所有Activity
	 */
	public void finishAllActivity(){
		
		for (int i = 0, size = activityStack.size(); i < size; i++){
            if (null != activityStack.get(i)){
            	activityStack.get(i).finish();
            }
	    }
		activityStack.clear();
	}
	/**
	 * 退出应用程序
	 */
	@SuppressWarnings("deprecation")
	public void AppExit(Context context) {
		try {
			finishAllActivity();
			ActivityManager activityMgr= (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
			activityMgr.restartPackage(context.getPackageName());
			System.exit(0);
		} catch (Exception e) {	}
	}
}