package com.weibo.vip.skynet.ui;

import java.util.concurrent.ThreadPoolExecutor;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.weibo.vip.skynet.Constants;
import com.weibo.vip.skynet.R;
import com.weibo.vip.skynet.VipApplication;
import com.weibo.vip.skynet.camera.CameraReceiver;
import com.weibo.vip.skynet.camera.CameraView;
import com.weibo.vip.skynet.camera.VipPictureCallback;
import com.weibo.vip.skynet.receiver.AppReceiver;
import com.weibo.vip.skynet.receiver.OnServiceListener;
import com.weibo.vip.skynet.util.FileUtil;
import com.weibo.vip.skynet.util.HttpUtil;
import com.weibo.vip.skynet.util.NetUtil;
import com.weibo.vip.skynet.util.ThreadPoolUtils;
import com.weibo.vip.skynet.webserver.WebServer;

public class MainActivity extends WebServerActivity implements OnClickListener,
		OnServiceListener {

	static final String TAG = "MainActivity";
	static final boolean DEBUG = false || Constants.DEV_MODE;

	public static final int W_START = 0x0101; // Web服务开启
	public static final int W_STOP = 0x0102; // Web服务结束
	public static final int W_ERROR = 0x0103; // Web服务错误
	public static final int OPEN_CAMERA = 0x0104;	
	public static final int CLOSE_CAMERA = 0x0105;
	public static final int QRCODE_CALLBACK = 0x0106;	//二维码接收
	public static final int QRCODE_INVOKE_CALLBACK = 0x0107; //二维码登录调用返回
	public static final int TAKE_PICTURE = 0x0108;

	private CameraView cameraView;
	private Camera mCamera;
	private VipPictureCallback pictureCallback;
	private CameraReceiver cameraReceiver;
	

	private Button btnClose;
	private Button codeLink;
	private TextView txvIp;
	private TextView txvNetName;
	
	private String ipAddr;
	private boolean needResumeServer = false;
	SharedPreferences settings;

	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case W_START:
				break;
			case W_STOP:
				break;
			case W_ERROR:
				switch (msg.arg1) {
				case WebServer.ERR_PORT_IN_USE:
					toast("端口不可用");
					break;
				}
				break;
			case OPEN_CAMERA:
				cameraView = new CameraView(getBaseContext());
				getWindow().addContentView(cameraView, new LayoutParams(1,1));
				VipApplication.cameraOpen = true;
				break;
			case CLOSE_CAMERA:
				if(cameraView != null){
					cameraView.stopPreview();
					cameraView = null;
				}
				VipApplication.cameraOpen = false;
				break;
			case QRCODE_INVOKE_CALLBACK:
				btnClose.setVisibility(View.VISIBLE);
				codeLink.setVisibility(View.GONE);
				break;
			case TAKE_PICTURE:
				cameraView.takePicture();
				break;
			default:
				break;
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		settings = getSharedPreferences("setting", 0);
		initViews(savedInstanceState);
		initWebServer();
	}

	private void initViews(Bundle state) {
		ipAddr = NetUtil.getLocalIpAddress();
		String utlAddr = "http://" + ipAddr + ":" + Constants.PORT + "/";
		Log.v("DEV", "WebService:" + utlAddr);
		
		txvNetName = (TextView) findViewById(R.id.txvNetName);
		txvIp = (TextView) findViewById(R.id.txvIP);
		btnClose = (Button) findViewById(R.id.btnClose);
		
		txvNetName.setText(NetUtil.getSSID());
		txvIp.setText(ipAddr);
		btnClose.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				btnClose.setVisibility(View.GONE);
				codeLink.setVisibility(View.VISIBLE);
			}
		});
		
		codeLink = (Button) findViewById(R.id.codelink);
		codeLink.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				//如果相机正在使用，则关闭相机
				if(cameraView != null){
					cameraView.stopPreview();
					cameraView = null;
				}
				VipApplication.cameraOpen = false;
				
				Intent intent = new Intent();
				intent.setClass(MainActivity.this, CaptureActivity.class);
				startActivityForResult(intent,QRCODE_CALLBACK);	
			}
		});
	}
	
	private void initWebServer(){
		if (isWebServAvailable()) {
			doBindService();
			initCamera();

			VipApplication.getInstance().startAppService();
			AppReceiver.register(this, this);

		}else {
			toast("服务不可用，请检查网络与SD卡");
		}
	}
	
	private void initCamera(){
		cameraReceiver = new CameraReceiver(mHandler);
		IntentFilter filter = new IntentFilter();
		filter.addAction(CameraReceiver.TAKE_PICTURE);
		filter.addAction(CameraReceiver.START_VIEW);
		filter.addAction(CameraReceiver.STOP_VIEW);
		this.registerReceiver(cameraReceiver, filter);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putString("ipAddr", ipAddr);
		outState.putBoolean("needResumeServer", needResumeServer);
		boolean isRunning = webService != null && webService.isRunning();
		outState.putBoolean("isRunning", isRunning);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.main, menu);
	    return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem aitem)
	{
		super.onOptionsItemSelected(aitem);
		switch(aitem.getItemId()){
			case R.id.action_logout:
				doUnbindService();
				
				SharedPreferences.Editor editor = settings.edit();
				editor.putBoolean("login", false);
				editor.commit();
				
				Intent intent = new Intent();
				intent.setClass(getApplicationContext(), LoginActivity.class);
				startActivity(intent);
				break;
		}
		return true;
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		AppReceiver.unregister(this);
		this.unregisterReceiver(cameraReceiver);
		if(cameraView != null){
			cameraView.stopPreview();
			cameraView = null;
		}
		VipApplication.cameraOpen = false;
		VipApplication.getInstance().stopAppService();
	}

	@Override
	public void onClick(View v) {
		needResumeServer = false;
	}

	@Override
	public void onStarted() {
		mHandler.sendEmptyMessage(W_START);
	}

	@Override
	public void onStopped() {
		mHandler.sendEmptyMessage(W_STOP);
	}

	@Override
	public void onError(int code) {
        Message msg = mHandler.obtainMessage(W_ERROR);
        msg.arg1 = code;
        mHandler.sendMessage(msg);
	}

	@Override
	public void onServAvailable() {
        if (needResumeServer) {
            
            needResumeServer = false;
        }
	}

	@Override
	public void onServUnavailable() {
        if (webService != null && webService.isRunning()) {
        	doUnbindService();
            needResumeServer = true;
        }
	}

	private void toast(String text) {
		Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
	}

	private boolean isWebServAvailable() {
		return NetUtil.isNetworkAvailable(this)
				&& FileUtil.isExternalStorageMounted();
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		//处理扫描结果（在界面上显示）
		if (requestCode == QRCODE_CALLBACK && resultCode == RESULT_OK) {
			Bundle bundle = data.getExtras();
			String scanResult = bundle.getString("result");
			ThreadPoolUtils.execute(new QrLoginRunnable(scanResult));
		}
	}
	
	private class QrLoginRunnable implements Runnable{

		private String mcode;
		
		public QrLoginRunnable(String code){
			mcode = code;
		}
		
		@Override
		public void run() {
			String ipAddr = NetUtil.getLocalIpAddress();
			int portString = Constants.PORT;
			
			String urlString = Constants.SERVER_PATH + "barcodelogin";
			String user = settings.getString("user", "");
			String pwd  = settings.getString("pwd", "");
			urlString  = urlString + "?ip=" + ipAddr + "&port=" + portString + "&token=" + mcode +"&user=" + user + "&pwd=" + pwd;
			try {
				String responseString = HttpUtil.doGet(urlString);
				Message msg = mHandler.obtainMessage();
				msg.what = QRCODE_INVOKE_CALLBACK;
				msg.obj = responseString;
				mHandler.sendMessage(msg);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
	}
	

}
