package com.weibo.vip.skynet.receiver;

public interface OnBatteryListener {
	
	public void setBatteryLevel(float value);
	
	public float getBatteryLevel();
}
