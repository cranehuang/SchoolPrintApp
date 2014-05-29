package com.xiaoyintong.app;



import cn.jpush.android.api.InstrumentedActivity;

import com.xiaoyintong.app.ui.LoginActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;

/**
 * 应用程序启动类：显示欢迎界面并跳转到主界面
 * @author liux (http://my.oschina.net/liux)
 * @version 1.0
 * @created 2012-3-21
 */
public class AppStart extends InstrumentedActivity {
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
     
        final View view = View.inflate(this, R.layout.start, null);
		setContentView(view);
		
		AppContext.getInstance().initLoginInfo();
		
		
		//渐变展示启动屏
		AlphaAnimation aa = new AlphaAnimation(0.3f,1.0f);
		aa.setDuration(2000);
		view.startAnimation(aa);
		aa.setAnimationListener(new AnimationListener()
		{
			@Override
			public void onAnimationEnd(Animation arg0) {
				redirectTo();
			}
			@Override
			public void onAnimationRepeat(Animation animation) {}
			@Override
			public void onAnimationStart(Animation animation) {}
			
		});
		
    }
    
    /**
     * 跳转到...
     */
    private void redirectTo(){ 
    	Intent intent = null;
//    	if (AppContext.getInstance().isLogin()) {
//			intent = new Intent(this , Main.class);
//		}
//    	else {
//    		intent = new Intent(this, LoginActivity.class);
//		}
//    	System.out.println("---------------> into login");
    	intent = new Intent(this,LoginActivity.class);
        startActivity(intent);
        finish();
    }
}