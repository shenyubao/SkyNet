package com.weibo.vip.skynet.dm;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Map;

import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.protocol.HttpContext;
import org.json.JSONException;
import org.json.JSONObject;

import com.weibo.vip.skynet.Constants;
import com.weibo.vip.skynet.VipApplication;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

public class URL extends VIPHttpHandler{
	
	@Override
	public void handler(HttpRequest request, HttpResponse response,
			HttpContext context) throws UnsupportedEncodingException,
			JSONException {
		String url = parseGetUrl(request);
        Map<String, String> params = parseGetParamters(request);
		
		JSONObject result = new JSONObject();
		String[] urlpart =url.split("/");
        String methodNameString = urlpart[2].toLowerCase();
        String URLString = params.get("url");
        
        
        if (URLString == null || URLString == "") {
			jumpToError(params, response, "Null Url");
			return;
		}
        
        URLString = URLDecoder.decode(URLString);

        
        if (methodNameString.equals("set")) {
            result.put("code", "10000");
			result.put("msg", "success");
			URL.set(VipApplication.getInstance().getApplicationContext(), URLString);
		}		
	}
	
	public static void set(Context context,String url){
		Intent intent = new Intent();
		if (!url.startsWith("http://")) {
			url = "http://" + url;
		}
		
		Log.v("DEV", "Open URL:"+ Uri.parse(url));
		intent.setData(Uri.parse(url));
		intent.setAction(Intent.ACTION_VIEW);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(intent);
	}


}
