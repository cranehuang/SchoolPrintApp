package com.xiaoyintong.app.ui;

import com.xiaoyintong.app.R;
import com.xiaoyintong.app.R.layout;
import com.xiaoyintong.app.R.menu;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

public class EditTask extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_task);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.edit_task, menu);
		return true;
	}

}
