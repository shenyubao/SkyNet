package com.weibo.vip.skynet.receiver;

import java.util.HashMap;
import java.util.Map;

import com.weibo.vip.skynet.VipApplication;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

public class BatteryReciver extends BroadcastReceiver{

	private OnBatteryListener mlistener;
	private static Map<Context, BroadcastReceiver> mReceiverMap = new HashMap<Context, BroadcastReceiver>();
	public BatteryReciver(OnBatteryListener listener){
		mlistener = listener;
	}
	
	@Override
	public void onReceive(Context context, Intent intent) {
		int rawlevel = intent.getIntExtra("level", -1);//获得当前电量  
		int scale = intent.getIntExtra("scale", -1);  //获得总电量 
		int level = -1;  
        if (rawlevel >= 0 && scale > 0) {  
            level = (rawlevel * 100) / scale;  
        }  
        mlistener.setBatteryLevel(level);
	}
	
    public static void register(Context context, OnBatteryListener listener) {
    	BatteryReciver reciver = new BatteryReciver(listener);
    	final IntentFilter filter = new IntentFilter();
    	filter.addAction(Intent.ACTION_BATTERY_CHANGED);
    	context.registerReceiver(reciver, filter);
    	mReceiverMap.put(context, reciver);
    }
    
    public static void unregister(Context context){
    	BroadcastReceiver receiver = mReceiverMap.get(context);
    	context.unregisterReceiver(receiver);
    }


}
