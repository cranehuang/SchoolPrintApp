package com.xiaoyintong.app.common;

import com.kristijandraca.backgroundmaillibrary.BackgroundMail;
import com.kristijandraca.backgroundmaillibrary.Utils;
import com.xiaoyintong.app.AppManager;
import com.xiaoyintong.app.AppStatusService;
import com.xiaoyintong.app.R;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.widget.Toast;

public class UIHelper {

	public static void showToast(Context context, String text) {
		Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
	}

	/**
	 * 发送App异常崩溃报告
	 * 
	 * @param cont
	 * @param crashReport
	 */
	public static void sendAppCrashReport(final Context cont,
			final String crashReport) {
		AlertDialog.Builder builder = new AlertDialog.Builder(cont);
		builder.setIcon(android.R.drawable.ic_dialog_info);
		builder.setTitle(R.string.app_error);
		builder.setMessage(R.string.app_error_message);
		builder.setPositiveButton(R.string.submit_report,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						// 发送异常报告
						// Intent i = new Intent(Intent.ACTION_SEND);
						// // i.setType("text/plain"); //模拟器
						// i.setType("message/rfc822"); // 真机
						// i.putExtra(Intent.EXTRA_EMAIL,
						// new String[] { "renhe940318@gmail.com" });
						// i.putExtra(Intent.EXTRA_SUBJECT,
						// "校印通Android客户端 - 错误报告");
						// i.putExtra(Intent.EXTRA_TEXT, crashReport);
						// cont.startActivity(Intent.createChooser(i,
						// "发送错误报告"));
						//转交给service处理
						Intent intent = new Intent("com.xiaoyintong.app.crash");
						intent.putExtra("crashReport", crashReport);
						intent.setClass(cont, AppStatusService.class);
						cont.startService(intent);
						
						// 退出
						AppManager.getAppManager().AppExit(cont);
					}
				});
		builder.setNegativeButton(R.string.sure,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						// 退出
						AppManager.getAppManager().AppExit(cont);
					}
				});
		builder.show();
	}

}
