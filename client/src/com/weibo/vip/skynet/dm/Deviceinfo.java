package com.weibo.vip.skynet.dm;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.entity.StringEntity;
import org.apache.http.protocol.HttpContext;
import org.json.JSONException;
import org.json.JSONObject;

import com.weibo.vip.skynet.Constants;
import com.weibo.vip.skynet.VipApplication;
import com.weibo.vip.skynet.util.NetUtil;

import android.content.Context;
import android.os.Environment;
import android.os.StatFs;
import android.telephony.TelephonyManager;

public class Deviceinfo extends VIPHttpHandler{
	
	@Override
	public void handler(HttpRequest request, HttpResponse response,
			HttpContext context) throws UnsupportedEncodingException,
			JSONException {
		
		String url = parseGetUrl(request);
        Map<String, String> params = parseGetParamters(request);

		String[] urlpart =url.split("/");
        String classNameString = urlpart[1].toLowerCase();
        String methodNameString = urlpart[2].toLowerCase();
        
        if(methodNameString.equals("get")){
            StringEntity entity = new StringEntity("", Constants.ENCODING);
    		response.setStatusCode(HttpStatus.SC_NOT_FOUND);
    		response.setEntity(entity);        
    	}
		
		JSONObject result = new JSONObject();
		JSONObject subData = new JSONObject();
		HashMap<String, String> hashMap = Deviceinfo.get(mContext);
		Iterator iterator = hashMap.entrySet().iterator();
		while (iterator.hasNext()) {
			Entry<String, String> entry = (Entry<String, String>) iterator.next();
			subData.put(entry.getKey(), entry.getValue());
		}
	
        result.put("code", "10000");
        result.put("msg", "success");
        result.put("data", subData);
        
        StringEntity entity = new StringEntity(wrap_jsonp(result.toString(),params), Constants.ENCODING);
		response.setStatusCode(HttpStatus.SC_OK);
		response.setEntity(entity);		
	}
	
	public static HashMap<String, String> get(Context context) {
		HashMap<String, String> hashMap = new HashMap<String, String>();
		TelephonyManager tm = (TelephonyManager) context
				.getSystemService(context.TELEPHONY_SERVICE);

		// 设备ID
		hashMap.put("DeviceId", tm.getDeviceId());
		// 手机号
		hashMap.put("LineNumber", tm.getLine1Number());
		// 网络类型
		hashMap.put("NetworkType", String.valueOf(tm.getNetworkType()));
		// 服务商名称
		if (tm.getSimState() == TelephonyManager.SIM_STATE_READY) {
			hashMap.put("OperatorName", tm.getSimOperatorName());
		} else {
			hashMap.put("OperatorName", "");
		}

		// 手机型号
		hashMap.put("PhoneModel", android.os.Build.MODEL);
		// 系统版本
		hashMap.put("AndroidVersion", "Android " + android.os.Build.VERSION.RELEASE);
		//磁盘
		hashMap.put("InterAvailSize", String.valueOf(getAvailableInternalMemorySize()));
		hashMap.put("InterTotalSize", String.valueOf(getTotalInternalMemorySize()));
		hashMap.put("ExternAvailSize", String.valueOf(getAvailableExternalMemorySize()));
		hashMap.put("ExternTotalSize", String.valueOf(getTotalExternalMemorySize()));
		
		//获取电量
		hashMap.put("BatteryLevel", String.valueOf(VipApplication.batteryLevel));
		
		//Wifi名称
		hashMap.put("wifiSSID", NetUtil.getSSID());
		return hashMap;
	}

	/**
	 * 获取手机内部剩余存储空间
	 * 
	 * @return
	 */
	private static long getAvailableInternalMemorySize() {
		File path = Environment.getDataDirectory();
		StatFs stat = new StatFs(path.getPath());
		long blockSize = stat.getBlockSize();
		long availableBlocks = stat.getAvailableBlocks();
		return availableBlocks * blockSize;
	}

	/**
	 * 获取手机内部总的存储空间
	 * 
	 * @return
	 */
	private static long getTotalInternalMemorySize() {
		File path = Environment.getDataDirectory();
		StatFs stat = new StatFs(path.getPath());
		long blockSize = stat.getBlockSize();
		long totalBlocks = stat.getBlockCount();
		return totalBlocks * blockSize;
	}

	/**
	 * 获取SDCARD剩余存储空间
	 * 
	 * @return
	 */
	private static long getAvailableExternalMemorySize() {
		if (externalMemoryAvailable()) {
			File path = Environment.getExternalStorageDirectory();
			StatFs stat = new StatFs(path.getPath());
			long blockSize = stat.getBlockSize();
			long availableBlocks = stat.getAvailableBlocks();
			return availableBlocks * blockSize;
		} else {
			return -1;
		}
	}

	/**
	 * 获取SDCARD总的存储空间
	 * 
	 * @return
	 */
	private static long getTotalExternalMemorySize() {
		if (externalMemoryAvailable()) {
			File path = Environment.getExternalStorageDirectory();
			StatFs stat = new StatFs(path.getPath());
			long blockSize = stat.getBlockSize();
			long totalBlocks = stat.getBlockCount();
			return totalBlocks * blockSize;
		} else {
			return -1;
		}
	}

	/**
	 * SDCARD是否存
	 */
	private static boolean externalMemoryAvailable() {
		return android.os.Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED);
	}
}
