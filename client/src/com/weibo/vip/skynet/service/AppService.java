package com.weibo.vip.skynet.service;

import com.weibo.vip.skynet.Constants;
import com.weibo.vip.skynet.VipApplication;
import com.weibo.vip.skynet.receiver.BatteryReciver;
import com.weibo.vip.skynet.receiver.NetworkReceiver;
import com.weibo.vip.skynet.receiver.OnBatteryListener;
import com.weibo.vip.skynet.receiver.OnNetworkListener;
import com.weibo.vip.skynet.receiver.OnStorageListener;
import com.weibo.vip.skynet.receiver.AppReceiver;
import com.weibo.vip.skynet.receiver.StorageReceiver;
import com.weibo.vip.skynet.util.FileUtil;
import com.weibo.vip.skynet.util.NetUtil;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class AppService extends Service implements OnNetworkListener,OnStorageListener,OnBatteryListener{

    static final String TAG = "AppService";
    static final boolean DEBUG = false || Constants.DEV_MODE;
    
    public static final String ACTION = "com.weibo.vip.skynet.service.AppService";
    
    public boolean isWebServAvailable = false;

    private boolean isNetworkAvailable;
    private boolean isStorageMounted;
	
    @Override
    public void onCreate() {
    	super.onCreate();
    	NetworkReceiver.register(this, this);
    	StorageReceiver.register(this, this);
    	BatteryReciver.register(this, this);
    	
    	isNetworkAvailable = NetUtil.isNetworkAvailable(this);
    	isStorageMounted = FileUtil.isExternalStorageMounted();
    	
    	isWebServAvailable = isNetworkAvailable && isStorageMounted;
        notifyWebServAvailable(isWebServAvailable);
    }
    
    @Override
    public void onDestroy() {
        super.onDestroy();
        NetworkReceiver.unregister(this);
        StorageReceiver.unregister(this);
        BatteryReciver.unregister(this);
    }
    
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onMounted() {
        isStorageMounted = true;
        notifyWebServAvailableChanged();		
	}

	@Override
	public void onUnmounted() {
        isStorageMounted = false;
        notifyWebServAvailableChanged();		
	}

	@Override
	public void onConnected(boolean isWifi) {
        isNetworkAvailable = true;
        notifyWebServAvailableChanged();		
	}

	@Override
	public void onDisconnected() {
        isNetworkAvailable = false;
        notifyWebServAvailableChanged();		
	}
	
    private void notifyWebServAvailableChanged() {
        boolean isAvailable = isNetworkAvailable && isStorageMounted;
        if (isAvailable != isWebServAvailable) {
            notifyWebServAvailable(isAvailable);
            isWebServAvailable = isAvailable;
        }
    }
	
    private void notifyWebServAvailable(boolean isAvailable) {
        if (DEBUG)
            Log.d(TAG, "isAvailable:" + isAvailable);
        // Notify if web service is available.
        String action = isAvailable ? AppReceiver.ACTION_SERV_AVAILABLE
                : AppReceiver.ACTION_SERV_UNAVAILABLE;
        Intent intent = new Intent(action);
        sendBroadcast(intent, AppReceiver.PERMIT_WS_RECEIVER);
    }

	@Override
	public void setBatteryLevel(float value) {
		VipApplication.batteryLevel = value;
	}

	@Override
	public float getBatteryLevel() {
		return VipApplication.batteryLevel;
	}

}
