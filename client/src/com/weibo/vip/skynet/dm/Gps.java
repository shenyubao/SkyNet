package com.weibo.vip.skynet.dm;

import java.io.UnsupportedEncodingException;
import java.util.Map;

import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.entity.StringEntity;
import org.apache.http.protocol.HttpContext;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;

import com.weibo.vip.skynet.Constants;
import com.weibo.vip.skynet.VipApplication;

public class Gps extends VIPHttpHandler{	
	
	 private LocationListener locationListener = new LocationListener() {

			@Override
			public void onLocationChanged(Location location) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onStatusChanged(String provider, int status,
					Bundle extras) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onProviderEnabled(String provider) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onProviderDisabled(String provider) {
				// TODO Auto-generated method stub
				
			}
    };
	
	@Override
	public void handler(HttpRequest request, HttpResponse response,HttpContext context) throws UnsupportedEncodingException, JSONException {
		
		JSONObject result = new JSONObject();
        
     
        
        Map<String, String> params = parseGetParamters(request);
        
//        if(VipApplication.lockLooper != true){
//        	Looper.prepare();
//        	VipApplication.lockLooper = true;
//        }
        
        // 获取系统LocationManager服务  
        LocationManager locationManager = (LocationManager) VipApplication.getInstance().getSystemService(Context.LOCATION_SERVICE);  
        //locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, locationListener);
        // 从GPS获取最近的定位信息  
        Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        int index = 100;
        while(location == null){
        	if(index-- < 0)break;
        	location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER); 
        }
       
//        if(location != null){
//    	    result.put("code", "10000");
//            result.put("msg", "success");
//        	result.put("altitude",  116.30665539913639);
//        	result.put("latitude",  39.984177917880714);
//        }else{
//        	result.put("code", "10000");
//            result.put("msg", "success");
//         	result.put("altitude",  116.30665539913639);
//         	result.put("latitude",  39.984177917880714);
//        }
        
        result.put("code", "10000");
        result.put("msg", "success");
     	result.put("altitude",  116.30584084655766);
     	result.put("latitude",  39.98477991531608);
        
        StringEntity entity = new StringEntity(wrap_jsonp(result.toString(),params), Constants.ENCODING);
		response.setStatusCode(HttpStatus.SC_OK);
        response.setHeader("Content-Type", "application/javascript");
		response.setEntity(entity);
	}
	
	
}
