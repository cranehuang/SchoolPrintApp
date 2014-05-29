package com.xiaoyintong.app.ui;

import cn.jpush.android.api.JPushInterface;

import com.actionbarsherlock.app.SherlockPreferenceActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.umeng.fb.FeedbackAgent;
import com.umeng.update.UmengUpdateAgent;
import com.umeng.update.UmengUpdateListener;
import com.umeng.update.UpdateResponse;
import com.xiaoyintong.app.AppContext;
import com.xiaoyintong.app.AppManager;
import com.xiaoyintong.app.R;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.SwitchPreference;
import android.util.Log;
import android.widget.Toast;

public class Setting extends SherlockPreferenceActivity implements
		OnPreferenceChangeListener, OnPreferenceClickListener {

	private String pushKey = "pref_push_key";
	private String modifyPasswordKey = "pref_modify_password_key";
	private String feedbackKey = "pref_feedback_key";
	private String logoutKey = "pref_logout_key";
	private String updateKey = "pref_update_key";

	private Preference resetPassword;
	private SwitchPreference pushMessage;
	private Preference feedback;
	private Preference logout;
	private Preference update;
	
	private ProgressDialog progressDialog;

	@SuppressLint("NewApi")
	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// setContentView(R.layout.activity_setting);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setBackgroundDrawable(
				getResources().getDrawable(R.drawable.top_bg));
		AppManager.getAppManager().addActivity(this);
		addPreferencesFromResource(R.xml.setting);
		resetPassword = (Preference) findPreference(modifyPasswordKey);
		pushMessage = (SwitchPreference) findPreference(pushKey);
		feedback = (Preference) findPreference(feedbackKey);
		logout = (Preference) findPreference(logoutKey);
		update = (Preference) findPreference(updateKey);

		update.setOnPreferenceClickListener(this);
		resetPassword.setOnPreferenceClickListener(this);
		feedback.setOnPreferenceClickListener(this);
		pushMessage.setOnPreferenceChangeListener(this);
		logout.setOnPreferenceClickListener(this);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		return super.onCreateOptionsMenu(menu);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// If this callback does not handle the item click,
		// onPerformDefaultAction
		// of the ActionProvider is invoked. Hence, the provider encapsulates
		// the
		// complete functionality of the menu item.
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			break;

		default:
			break;
		}
		return true;
	}

	@Override
	public boolean onPreferenceClick(Preference preference) {
		// TODO Auto-generated method stub
		Log.w("Setting", preference.getKey());
		if (preference.getKey().equals(modifyPasswordKey)) {
			Intent intent = new Intent(Setting.this, ResetActivity.class);
			startActivity(intent);
		} else if (preference.getKey().equals(feedbackKey)) {
			FeedbackAgent agent = new FeedbackAgent(Setting.this);
			agent.startFeedbackActivity();
			agent.sync();
		} else if (preference.getKey().equals(logoutKey)) {
			AppContext.getInstance().Logout();
			AppManager.getAppManager().finishAllActivity();
		} else if (preference.getKey().equals(pushKey)) {

		}
		else if (preference.getKey().equals(updateKey)) {
			update();
		}
		return true;
	}

	@Override
	public boolean onPreferenceChange(Preference preference, Object newValue) {
		// TODO Auto-generated method stub
		// System.out.println("newValue = " + newValue);
		if (preference.getKey().equals(pushKey)) {

		}
		return true;
	}
	
	private void update()
	{
		progressDialog = ProgressDialog
				.show(this,
				"检查更新",
				"正在检查更新，请稍后…");
		progressDialog.setCanceledOnTouchOutside(true);
		UmengUpdateAgent.update(Setting.this);
		UmengUpdateAgent.setUpdateAutoPopup(false);
		UmengUpdateAgent.setUpdateListener(new UmengUpdateListener() {
			@Override
			public void onUpdateReturned(int updateStatus,
					UpdateResponse updateInfo) {
				switch (updateStatus) {
				case 0: // has update
					progressDialog.dismiss();
					UmengUpdateAgent.showUpdateDialog(Setting.this,
							updateInfo);

					break;
				case 1: // has no update
					progressDialog.dismiss();
					Toast.makeText(Setting.this, "没有更新", Toast.LENGTH_SHORT)
							.show();
					// System.out.println("meiyou");
					break;
				case 2: // none wifi
					progressDialog.dismiss();
					Toast.makeText(Setting.this, "没有wifi连接， 只在wifi下更新",
							Toast.LENGTH_SHORT).show();
					// System.out.println("meiyou wifi");
					break;
				case 3: // time out
					progressDialog.dismiss();
					Toast.makeText(Setting.this, "超时", Toast.LENGTH_SHORT)
							.show();
					// System.out.println("time out ");
					break;
				}
			}

		});
	}
	
	@Override
	public void onPause()
	{
		super.onPause();
		JPushInterface.onPause(this);
	}
	
	@Override
	public void onResume()
	{
		super.onResume();
		JPushInterface.onResume(this);
	}
	
	@Override
	public void onDestroy()
	{
		super.onDestroy();
		AppManager.getAppManager().finishActivity(this);
		
	}

}
