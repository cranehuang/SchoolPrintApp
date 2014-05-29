package com.xiaoyintong.app.ui;



import com.actionbarsherlock.view.Menu;
import com.andreabaccega.widget.FormEditText;
import com.loopj.android.http.RequestParams;
import com.xiaoyintong.app.AppContext;
import com.xiaoyintong.app.R;
import com.xiaoyintong.app.api.ApiClient;
import com.xiaoyintong.app.api.URLs;
import com.xiaoyintong.app.common.MD5;
import com.xiaoyintong.app.common.UIHelper;
import com.xiaoyintong.app.widget.CustomDialog;

import android.os.Bundle;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class RegisterActivity extends BaseActivity{
	
	private FormEditText userNameEditText;
	private FormEditText emailEditText;
	private FormEditText phoneEditText;
	private FormEditText passwordEditText;
	private FormEditText pwAgainEditText;
	
	Button registerBtn;
	
	CustomDialog dialog;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register);
		
		
		dialog = new CustomDialog(RegisterActivity.this, 300, 300, R.layout.register_dialog, R.style.MyDialogStyle);
		emailEditText = (FormEditText) findViewById(R.id.et_email);
		userNameEditText = (FormEditText) findViewById(R.id.et_username);
		phoneEditText = (FormEditText) findViewById(R.id.et_phone);
		passwordEditText = (FormEditText) findViewById(R.id.et_password);
		pwAgainEditText = (FormEditText) findViewById(R.id.et_pw_again);
		
		registerBtn = (Button) findViewById(R.id.register_btn);
		registerBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				 FormEditText[] allFields    = { emailEditText  , userNameEditText , phoneEditText , passwordEditText , pwAgainEditText};


			        boolean allValid = true;
			        for (FormEditText field: allFields) {
			            allValid = field.testValidity() && allValid;
			        }

			        if (allValid) {
			            // YAY
			        	if (passwordEditText.getText().toString().equals(pwAgainEditText.getText().toString())) {
			        		RequestParams params = new RequestParams();
							params.put("username",userNameEditText.getText().toString());
							params.put("email", emailEditText.getText().toString());
							params.put("password", MD5.getMD5(passwordEditText.getText().toString()));
							params.put("phone", phoneEditText.getText().toString());
							params.put("imei", AppContext.getInstance().getIMEI());
							params.put("facility", AppContext.getInstance().getFacility());
							dialog.show();
							ApiClient.post(URLs.REGISTER_URL, params, responseHandler);
//			        		UIHelper.showToast(RegisterActivity.this, "验证成功");
						}else {
							UIHelper.showToast(getApplicationContext(), "两次输入的密码不一致");
						}
			        	
			        	
			        } else {
			            // EditText are going to appear with an exclamation mark and an explicative message.
			        	
			        }
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getSupportMenuInflater().inflate(R.menu.register, menu);
		return true;
	}

	@Override
	public void processResponse(String info) {
		// TODO Auto-generated method stub
		dialog.dismiss();
		UIHelper.showToast(getApplicationContext(), info);
		Intent intent = new Intent(RegisterActivity.this , LoginActivity.class);
		intent.putExtra("email", emailEditText.getText().toString());
		intent.putExtra("password", passwordEditText.getText().toString());
		RegisterActivity.this.setResult(0, intent);
		finish();
	}

	@Override
	public void requestFinished(boolean isSucceed) {
		// TODO Auto-generated method stub
		
	}

}
