package com.weibo.vip.skynet.dm;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Map;

import org.apache.commons.fileupload.ParameterParser;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.entity.StringEntity;
import org.apache.http.protocol.HttpContext;
import org.json.JSONException;
import org.json.JSONObject;

import com.weibo.vip.skynet.Constants;
import com.weibo.vip.skynet.VipApplication;

import android.content.Context;

public abstract class VIPHttpHandler {
	public abstract void handler(HttpRequest request, HttpResponse response,HttpContext context) throws UnsupportedEncodingException, JSONException;
	
	protected Context mContext = VipApplication.getInstance().getApplicationContext();
	
 	protected String wrap_jsonp(String json,Map<String, String> hashMap){
 		String callback = hashMap.get("callback");
 		if (callback == null) {
			return json;
		}
 		String[] callbacks = callback.split("\\.");
 		return "window."+callbacks[0]+"."+callbacks[1]+"['"+callbacks[2]+"'](" + json + ")";
	}
 	
    /**
     * @brief 解析请求的get信息
     * @param request Http请求
     * @return 名称与值的Map集合
     * @throws IOException
     * @warning 需保证是post请求且不是multipart的。
     */
    protected Map<String, String> parseGetParamters(HttpRequest request) {
        ParameterParser parser = new ParameterParser();
        parser.setLowerCaseNames(true);
        return parser.parse(getContent(request), '&');
    }
    
    protected String parseGetUrl(HttpRequest request) throws UnsupportedEncodingException{
		String url = URLDecoder.decode(request.getRequestLine().getUri(), Constants.ENCODING);
		if (url.contains("?")) {
			url = url.substring(0,url.indexOf("?"));
		}
		return url;
    }

    private String getContent(HttpRequest request) {
        String uri = request.getRequestLine().getUri();
        int index = uri.indexOf('?');
        return index == -1 || index + 1 >= uri.length() ? null : uri.substring(index + 1);
    }
    
	protected void jumpToError(Map<String, String> hashMap,HttpResponse response,String ErrInfo) throws JSONException, UnsupportedEncodingException{
        JSONObject result = new JSONObject();
        result.put("code", "10001");
        result.put("msg", ErrInfo);
        
        StringEntity entity = new StringEntity(wrap_jsonp(result.toString(),hashMap), Constants.ENCODING);
		response.setStatusCode(HttpStatus.SC_OK);
        response.setHeader("Content-Type", "application/javascript");
		response.setEntity(entity);
	}
}
