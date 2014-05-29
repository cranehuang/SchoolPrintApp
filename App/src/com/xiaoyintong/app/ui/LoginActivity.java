package com.xiaoyintong.app.ui;

import cn.jpush.android.api.JPushInterface;

import com.google.gson.Gson;
import com.umeng.update.UmengUpdateAgent;
import com.xiaoyintong.app.AppContext;
import com.xiaoyintong.app.AppManager;
import com.xiaoyintong.app.AppStatusService;
import com.xiaoyintong.app.R;
import com.xiaoyintong.app.api.ApiClient;
import com.xiaoyintong.app.bean.User;
import com.xiaoyintong.app.common.AESEncryptor;
import com.xiaoyintong.app.common.FileUtils;
import com.xiaoyintong.app.common.UIHelper;
import com.xiaoyintong.app.widget.CustomDialog;

import android.annotation.SuppressLint;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends BaseActivity {

	private static final String LOG_TAG = "LoginActivity";
	// private static boolean DEBUG = false;

	private boolean isNetConnected = false;

	AutoCompleteTextView userNameAuto;
	EditText passwordET;
	Button loginBtn;

	Button registerBtn;

	CustomDialog dialog;

	CheckBox savePasswordCB;
	SharedPreferences sharedPreferences;
	String userName;
	String password;

	BroadcastReceiver connectionReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			ConnectivityManager connectMgr = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
			NetworkInfo mobNetInfo = connectMgr
					.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
			NetworkInfo wifiNetInfo = connectMgr
					.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

			if (!mobNetInfo.isConnected() && !wifiNetInfo.isConnected()) {
				// unconnect network
				// Intent mIntent = new Intent("/");
				// ComponentName component = new
				// ComponentName("com.android.settings",
				// "com.android.settings.WirelessSettings");
				// mIntent.setComponent(component);
				// mIntent.setAction("android.intent.action.VIEW");
				// startActivity(mIntent);

			} else {
				isNetConnected = true;
				// connect network
			}
		}
	};

	@SuppressLint("HandlerLeak")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getSupportActionBar().hide();
		setContentView(R.layout.activity_login);
		

		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
		registerReceiver(connectionReceiver, intentFilter);
		
		NotificationManager notificationManager = (
				NotificationManager) getSystemService(
						Context.NOTIFICATION_SERVICE);
		notificationManager.cancel(AppStatusService.ORDER_REACHED_NOTIFY);
		
		AppManager.getAppManager().finishOthersExceptLogin();
//		AppManager.getAppManager().addActivity(this);

		isNetConnected = AppContext.getInstance().isNetworkConnected();

		userNameAuto = (AutoCompleteTextView) findViewById(R.id.username_edit);
		passwordET = (EditText) findViewById(R.id.password_edit);
		loginBtn = (Button) findViewById(R.id.signin_button);
		savePasswordCB = (CheckBox) findViewById(R.id.savePasswordCB);
		registerBtn = (Button) findViewById(R.id.registerBtn);

		sharedPreferences = getSharedPreferences("password_file", MODE_PRIVATE);
		userNameAuto.setThreshold(1);
		savePasswordCB.setChecked(true);
		passwordET.setInputType(InputType.TYPE_CLASS_TEXT
				| InputType.TYPE_TEXT_VARIATION_PASSWORD);

		dialog = new CustomDialog(LoginActivity.this, 300, 300,
				R.layout.loading, R.style.MyDialogStyle);

		if (!AppContext.getInstance().getLoginInfo().getUid().equals("")
				&& AppContext.getInstance().getLoginInfo().isRememberMe()) {
			String account = AppContext.getInstance().getLoginInfo().getEmail();
			userNameAuto.setText(account);
			try {
				String passwordTemp = sharedPreferences.getString(account, "");
				if (!passwordTemp.equals("")) {
					passwordTemp = AESEncryptor.decrypt(AESEncryptor.SEED,
							passwordTemp);
				}
				passwordET.setText(passwordTemp);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				Log.e(LOG_TAG, e.toString());
			}
		}

		userNameAuto.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// TODO Auto-generated method stub
				String[] alluserName = new String[sharedPreferences.getAll()
						.size()];// sp.getAll().size()返回的是有多少个键值对
				alluserName = sharedPreferences.getAll().keySet()
						.toArray(new String[0]);
				// sp.getAll()返回一张hash map
				// keySet()得到的是a set of the keys.
				// hash map是由key-value组成的

				ArrayAdapter<String> adapter = new ArrayAdapter<String>(
						LoginActivity.this,
						android.R.layout.simple_dropdown_item_1line,
						alluserName);

				userNameAuto.setAdapter(adapter);// 设置数据适配器

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
				try {
					String passwordTemp = sharedPreferences.getString(
							userNameAuto.getText().toString(), "");
					if (!passwordTemp.equals("")) {
						passwordTemp = AESEncryptor.decrypt(AESEncryptor.SEED,
								passwordTemp);
					}
					passwordET.setText(passwordTemp);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					// System.out.println("decrypt exception");
					Log.e(LOG_TAG, e.toString());
					e.printStackTrace();
				}// 自动输入密码

			}
		});

		// 登陆
		loginBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				userName = userNameAuto.getText().toString();
				password = passwordET.getText().toString();

				if (userName.trim().equals("")) {
					Toast.makeText(LoginActivity.this, "亲，请输入账号",
							Toast.LENGTH_SHORT).show();
				} else if (password.trim().equals("")) {
					Toast.makeText(LoginActivity.this, "亲，请输入密码",
							Toast.LENGTH_SHORT).show();
				} else {
					if (AppContext.getInstance().isNetworkConnected()) {
						// 登录验证
						dialog.show();
						// System.out.println("---------------> password = "
						// + MD5.getMD5(password));
						ApiClient.login(userName, password, responseHandler);

					} else {
						Toast.makeText(getApplicationContext(), "无网络连接",
								Toast.LENGTH_SHORT).show();
					}

				}

			}
		});

		registerBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Log.i(LOG_TAG, "注册");
				Intent intent = new Intent(LoginActivity.this,
						RegisterActivity.class);
				startActivityForResult(intent, 0);
			}
		});
		
		//wifi环境下提示更新
		if (AppContext.getInstance().getNetworkType() == AppContext.NETTYPE_WIFI) {
			UmengUpdateAgent.update(this);
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode,
			Intent intent) {
		if (requestCode == 0 && resultCode == 0) {
			try {
				userNameAuto.setText(intent.getStringExtra("email"));
				passwordET.setText(intent.getStringExtra("password"));
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
	}
	
	@Override
	public void onPause()
	{
		super.onPause();
//		JPushInterface.onPause(AppContext.getInstance());
	}
	
	@Override
	public void onResume()
	{
		super.onResume();
//		JPushInterface.onResume(AppContext.getInstance());
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (connectionReceiver != null) {
			unregisterReceiver(connectionReceiver);
		}
	}

	@Override
	public void processResponse(String info) {
		// TODO Auto-generated method stub
//		FileUtils.write2SD("info = " + info);
//		Toast.makeText(getApplicationContext(), "info", Toast.LENGTH_SHORT).show();
		try {
			User user = new Gson().fromJson(info, User.class);
			user.setRememberMe(savePasswordCB.isChecked());
			AppContext.getInstance().saveLoginInfo(user);

			if (savePasswordCB.isChecked()) {
				// 存储账号密码信息，供登陆时使用
				try {
					// Log.d(LOG_TAG, "save password");
					sharedPreferences
							.edit()
							.putString(
									userName,
									AESEncryptor.encrypt(AESEncryptor.SEED,
											password)).commit();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else {
				// 清除密码
				try {
					// Log.d(LOG_TAG, "save password");
					sharedPreferences.edit().putString(userName, "").commit();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			Intent intent = new Intent(LoginActivity.this, Main.class);
			startActivity(intent);
			finish();
		} catch (Exception e) {
			// TODO: handle exception
			Log.e(LOG_TAG, e.toString());
			UIHelper.showToast(AppContext.getInstance(), "数据解析异常");
		}
	}

	@Override
	public void requestFinished(boolean isSucceed) {
		// TODO Auto-generated method stub
		dialog.dismiss();
//		if (! isSucceed) {
//			Toast.makeText(LoginActivity.this, "失败", Toast.LENGTH_SHORT).show();
//		}else {
//			Toast.makeText(LoginActivity.this, "成功", Toast.LENGTH_SHORT).show();
//		}
		
	}

}
