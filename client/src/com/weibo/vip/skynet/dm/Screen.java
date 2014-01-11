package com.weibo.vip.skynet.dm;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.protocol.HttpContext;
import org.json.JSONException;

import com.weibo.vip.skynet.Constants;

public class Screen extends VIPHttpHandler{

	@Override
	public void handler(HttpRequest request, HttpResponse response,
			HttpContext context) throws UnsupportedEncodingException,
			JSONException {
		String url = URLDecoder.decode(request.getRequestLine().getUri(), Constants.ENCODING);
		
		String[] urlpart =url.split("/");
        String methodNameString = urlpart[2].toLowerCase();
        
        
        if(methodNameString.equals("shot")){
        	
        	try {
				Process shProcess =  Runtime.getRuntime().exec("su",null,null);
				OutputStream os = shProcess.getOutputStream();
				os.write(("/system/bin/screencap -p " + "/sdcard/img.png").getBytes("ASCII"));
				os.flush();
				os.close();
				shProcess.waitFor();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        	
	    	response.setStatusCode(HttpStatus.SC_OK);
			response.setHeader("Content-Type", "image/jpeg");
			response.setHeader("Connection", "Keep-Alive");
			File file = new File("/sdcard/img.png");
			if(file.exists()){
				byte content[] = new byte[(int) file.length()];
				try {
					BufferedInputStream bufferedInputStream = new BufferedInputStream(new FileInputStream(file));
					bufferedInputStream.read(content);
					bufferedInputStream.close();
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				if(content != null){
					ByteArrayEntity arrayEntity = new ByteArrayEntity(content);
					response.setEntity(arrayEntity);
				}
			}
        }	
	}

}
