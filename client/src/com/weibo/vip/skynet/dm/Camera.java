package com.weibo.vip.skynet.dm;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.protocol.HttpContext;
import org.json.JSONException;

import android.content.Intent;

import com.weibo.vip.skynet.Constants;
import com.weibo.vip.skynet.VipApplication;
import com.weibo.vip.skynet.camera.CameraReceiver;

public class Camera extends VIPHttpHandler{

	@Override
	public void handler(HttpRequest request, HttpResponse response,
			HttpContext context) throws UnsupportedEncodingException,
			JSONException {
		String url = parseGetUrl(request);
		
		String[] urlpart =url.split("/");
        String methodNameString = urlpart[2].toLowerCase();
        
        if(methodNameString.equals("picture")){
        	if (VipApplication.cameraOpen == false) {
	    		Intent intent = new Intent(CameraReceiver.START_VIEW);
	    		mContext.sendBroadcast(intent);
	    	}else{
	    		Intent intent = new Intent(CameraReceiver.TAKE_PICTURE);
	    		mContext.sendBroadcast(intent);
	    	}
        	
	    	response.setStatusCode(HttpStatus.SC_OK);
			response.setHeader("Content-Type", "image/jpeg");
			response.setHeader("Connection", "Keep-Alive");
			byte content[] = VipApplication.getInstance().pictureBytes;
			if(content != null){
				ByteArrayEntity arrayEntity = new ByteArrayEntity(content);
				response.setEntity(arrayEntity);
			}
        }else if(methodNameString.equals("close")){
        	
        	Intent intent = new Intent(CameraReceiver.STOP_VIEW);
    		mContext.sendBroadcast(intent);
	    	
	    	response.setStatusCode(HttpStatus.SC_OK);
			response.setHeader("Content-Type", "image/jpeg");
			response.setHeader("Connection", "Keep-Alive");
			byte content[] = VipApplication.getInstance().pictureBytes;
			VipApplication.getInstance().pictureBytes = null;
			if(content != null){
				ByteArrayEntity arrayEntity = new ByteArrayEntity(content);
				response.setEntity(arrayEntity);
			}
        }		
	}

}
