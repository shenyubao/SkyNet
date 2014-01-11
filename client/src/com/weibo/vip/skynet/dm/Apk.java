package com.weibo.vip.skynet.dm;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.entity.StringEntity;
import org.apache.http.protocol.HttpContext;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import com.weibo.vip.skynet.Constants;

public class Apk extends VIPHttpHandler{

	@Override
	public void handler(HttpRequest request, HttpResponse response,
			HttpContext context) throws UnsupportedEncodingException,
			JSONException {
		Map<String, String> hashMap = parseGetParamters(request);
		String url = parseGetUrl(request);
		String apkName = hashMap.get("apk");
		String apkPath  = Constants.APP_UPLOAD_DIR + "/apk/" + apkName;
		File apkFile = new File(apkPath);

		if (!apkFile.exists()) {
			jumpToError(hashMap, response,"Error Apk Packeg");
			return ;
		}
		
		Log.v("DEV", "install:"+apkPath);
		apkFile.setWritable(true);
		apkFile.setReadable(true);
		apkFile.setExecutable(true);
		
		String[] urlpart =url.split("/");
        String methodNameString = urlpart[2].toLowerCase();
        JSONObject result = new JSONObject();
        
        if (methodNameString.equals("install")) {
        	Intent intent = new Intent(Intent.ACTION_VIEW); 
        	intent.setDataAndType(Uri.fromFile(new File(apkPath)), "application/vnd.android.package-archive"); 
        	intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        	mContext.startActivity(intent);
        	
            result.put("code", "10000");
            result.put("msg", "success");
            
            StringEntity entity = new StringEntity(wrap_jsonp(result.toString(),hashMap), Constants.ENCODING);
    		response.setStatusCode(HttpStatus.SC_OK);
            response.setHeader("Content-Type", "application/javascript");
    		response.setEntity(entity);
		}		
	}
	

}
