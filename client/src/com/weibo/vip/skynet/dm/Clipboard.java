package com.weibo.vip.skynet.dm;

import java.io.Console;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Map;

import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.protocol.HttpContext;
import org.json.JSONException;
import org.json.JSONObject;

import com.weibo.vip.skynet.Constants;
import com.weibo.vip.skynet.VipApplication;

import android.content.ClipboardManager;
import android.content.Context;
import android.os.Looper;
import android.provider.ContactsContract.Contacts.Data;
import android.util.Log;

public class Clipboard extends VIPHttpHandler{	
	
	@Override
	public void handler(HttpRequest request, HttpResponse response,HttpContext context) throws UnsupportedEncodingException, JSONException {
		String url = parseGetUrl(request);
		JSONObject result = new JSONObject();
		String[] urlpart =url.split("/");
        String classNameString = urlpart[1].toLowerCase();
        String methodNameString = urlpart[2].toLowerCase();
        Map<String, String> params = parseGetParamters(request);
           
    	//获取粘贴板内容
		if (methodNameString.equals("get")) {
			String data = Clipboard.get(mContext);
			result.put("data", data);
		}
		//设置粘贴板内容
		if (methodNameString.equals("set")) {
			String text = params.get("text");
			Clipboard.set(mContext,text);
		}
		
        result.put("code", "10000");
        result.put("msg", "success");
        
        StringEntity entity = new StringEntity(wrap_jsonp(result.toString(),params), Constants.ENCODING);
		response.setStatusCode(HttpStatus.SC_OK);
        response.setHeader("Content-Type", "application/javascript");
		response.setEntity(entity);
	}
	
	public static String get(Context context){
		if(!VipApplication.getInstance().lockLooper){
			Looper.prepare();
			VipApplication.getInstance().lockLooper = true;
		}
		ClipboardManager cmb = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
		String data;
		if (cmb.getText() == null) {
			data = "";
		}else {
			data = cmb.getText().toString();
		}

		return data;
	}
	
	public static void set(Context context,String text) {
		if(!VipApplication.getInstance().lockLooper){
			Looper.prepare();
			VipApplication.getInstance().lockLooper = true;
		}
		ClipboardManager cmb = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
		cmb.setText(text);
		Log.v("DEV", "Clipboard set:"+text);
	}
	
}
