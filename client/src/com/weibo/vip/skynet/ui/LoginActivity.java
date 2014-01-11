package com.weibo.vip.skynet.ui;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.weibo.vip.skynet.Constants;
import com.weibo.vip.skynet.R;
import com.weibo.vip.skynet.util.HttpUtil;
import com.weibo.vip.skynet.util.NetUtil;
import com.weibo.vip.skynet.util.ThreadPoolUtils;

public class LoginActivity extends Activity {

	private Handler myHandler;
	private Button mBtnLogin;
	private EditText mTxvUser;
	private EditText mTxvPwd;
	
	private final int EVENT_LOGIN = 0x01;
	SharedPreferences settings;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		settings = getSharedPreferences("setting", 0);
		boolean islogin = settings.getBoolean("login", false);
		if (islogin) {
			jumpToMain();
		}
		
		setContentView(R.layout.activity_login);	
		super.onCreate(savedInstanceState);
		initViews();
	}

	private void initViews(){
		ActionBar actionBar = getActionBar();
		actionBar.setTitle("Skynet");
		actionBar.setDisplayUseLogoEnabled(false);
		
		myHandler = new MyHandler();
		mTxvUser = (EditText) findViewById(R.id.txvUser);
		mTxvPwd = (EditText) findViewById(R.id.txvPwd);
		
		mBtnLogin = (Button) findViewById(R.id.btnLogin);
		mBtnLogin.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				ThreadPoolUtils.execute(new MyRunnable());				
			}
		});
		

	}
	
	private class MyRunnable implements Runnable{
		@Override
		public void run() {
			String requestString;
			try {
				String mIp = NetUtil.getLocalIpAddress() + ":" + String.valueOf(Constants.PORT);
				requestString = Constants.SERVER_PATH + "clientlogin?" + "user="+ mTxvUser.getText() + "&pwd=" + mTxvPwd.getText() + "&ip="+ mIp; 
				String result = HttpUtil.doGet(requestString);
				Message msg = myHandler.obtainMessage();
				msg.what = EVENT_LOGIN;
				msg.obj = result;
				myHandler.sendMessage(msg);			
			} catch (Exception e) {
				e.printStackTrace();
			}

		}	
	}
	
	private class MyHandler extends Handler{
		public void dispatchMessage(Message msg) {
			try {
				switch (msg.what) {
				case EVENT_LOGIN:
						JSONObject object = new JSONObject(String.valueOf(msg.obj));
						String code =  object.getString("code");
						if (code.equals("10000")) {
							SharedPreferences.Editor editor = settings.edit();
							editor.putBoolean("login", true);
							editor.putString("user", mTxvUser.getText().toString());
							editor.putString("pwd", mTxvPwd.getText().toString());
							editor.commit();
							jumpToMain();
						}else {
							jumpToError("您的账户活密码错误");
						}
					break;
				default:
					break;
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}
	
	private void jumpToMain(){
		Intent intent = new Intent();
		intent.setClass(getApplicationContext(), MainActivity.class);
		startActivity(intent);
	}
	
	private void jumpToError(String errorText){
		Toast.makeText(getApplicationContext(), errorText, Toast.LENGTH_LONG).show();
	}

}
