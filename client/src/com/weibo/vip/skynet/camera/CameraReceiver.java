package com.weibo.vip.skynet.camera;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;

import com.weibo.vip.skynet.ui.MainActivity;

public class CameraReceiver extends BroadcastReceiver {

	private Handler mHandler;


	public static final String TAKE_PICTURE = "com.weibo.vip.skynet.camera.TAKE_PICTURE";
	public static final String START_VIEW = "com.weibo.vip.skynet.camera.START_VIEW";
	public static final String STOP_VIEW = "com.weibo.vip.skynet.camera.STOP_VIEW";
	
	
	public CameraReceiver( Handler handler) {
		this.mHandler = handler;
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		String action = intent.getAction();
		if (START_VIEW.equals(action)) {
			// 开启预览
			mHandler.sendEmptyMessage(MainActivity.OPEN_CAMERA);
		} else if (STOP_VIEW.equals(action)) {
			// 结束预览
			mHandler.sendEmptyMessage(MainActivity.CLOSE_CAMERA);
		} else if (TAKE_PICTURE.equals(action)) {
			//开始拍照
			mHandler.sendEmptyMessage(MainActivity.TAKE_PICTURE);
		}

	}

//	/**
//	 * 注册
//	 */
//	public static void register(Context context, Camera camera,
//			VipPictureCallback callback, Handler handler) {
//		if (mReceiverMap.containsKey(context)) {
//			return;
//		}
//
//		CameraReceiver receiver = new CameraReceiver(camera, callback, handler);
//		IntentFilter filter = new IntentFilter();
//		filter.addAction(TAKE_PICTURE);
//		filter.addAction(START_VIEW);
//		filter.addAction(STOP_VIEW);
//		context.registerReceiver(receiver, filter);
//
//		mReceiverMap.put(context, receiver);
//	}
//
//	/**
//	 * 注销
//	 */
//	public static void unregister(Context context) {
//		CameraReceiver receiver = mReceiverMap.remove(context);
//		if (receiver != null) {
//			context.unregisterReceiver(receiver);
//			receiver = null;
//		}
//	}
}
