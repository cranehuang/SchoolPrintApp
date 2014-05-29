package com.xiaoyintong.app.ui;



import com.actionbarsherlock.view.Menu;
import com.andreabaccega.widget.FormEditText;
import com.xiaoyintong.app.AppContext;
import com.xiaoyintong.app.AppManager;
import com.xiaoyintong.app.R;
import com.xiaoyintong.app.api.ApiClient;
import com.xiaoyintong.app.common.AESEncryptor;
import com.xiaoyintong.app.common.UIHelper;

import android.os.Bundle;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class ResetActivity extends BaseActivity {
	
	private FormEditText oldPassword;
	private FormEditText newPassword;
	private FormEditText verifyNewPW;
	private Button yesBtn;
	
	private String oldPW;
	
	private SharedPreferences preferences;
	private AppContext appContext;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_reset);
		
		appContext = (AppContext) getApplicationContext();
		preferences = getSharedPreferences("password_file", MODE_PRIVATE);
		
		oldPassword = (FormEditText) findViewById(R.id.old_pw);
		newPassword = (FormEditText) findViewById(R.id.new_pw);
		verifyNewPW = (FormEditText) findViewById(R.id.verify_new_pw);
		
		yesBtn = (Button) findViewById(R.id.yes_btn);
		
		oldPW = preferences.getString(appContext.getLoginInfo().getEmail(), "");
		try {
			oldPassword.setText(AESEncryptor.decrypt(AESEncryptor.SEED, oldPW));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		yesBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (appContext.isNetworkConnected()) {
					if (validate()) {
						
						ApiClient.resetPassword(appContext.getLoginUid(), oldPassword.getText().toString(), newPassword.getText().toString(),responseHandler);
					}
				}else {
					UIHelper.showToast(appContext, "无网络连接");
				}
				
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getSupportMenuInflater().inflate(R.menu.reset, menu);
		return true;
	}
	
	private boolean validate()
	{
		
		boolean validate = false;
		
		FormEditText[] allFields    = { oldPassword , newPassword , verifyNewPW};


        boolean allValid = true;
        for (FormEditText field: allFields) {
            allValid = field.testValidity() && allValid;
        }
        if (allValid) {
        	if (!oldPassword.getText().toString().equals("") && !newPassword.getText().toString().equals("") && !verifyNewPW.getText().toString().equals("")) {
    			if (!newPassword.getText().toString().equals(verifyNewPW.getText().toString())) {
//    				System.out.println("--------------> 111111111111");
    				UIHelper.showToast(appContext, "两次输入的新密码不一致，请重新输入");
    			}else {
//    				System.out.println("--------------> 2");
    				validate = true;
    			}
    		}
		}
		
		return validate;
	}

	@Override
	public void processResponse(String info) {
		// TODO Auto-generated method stub
		UIHelper.showToast(appContext, info);
		AppContext.getInstance().changePassword(newPassword.getText().toString());
		Intent intent = new Intent(ResetActivity.this, LoginActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(intent);
		
	}

	@Override
	public void requestFinished(boolean isSucceed) {
		// TODO Auto-generated method stub
		
	}

}
