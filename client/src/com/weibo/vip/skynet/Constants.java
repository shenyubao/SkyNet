package com.weibo.vip.skynet;

import android.os.Environment;

public final class Constants {
	/**开发模式**/
	public static boolean DEV_MODE = true;
	
	/**统一编码**/
	public static final String ENCODING = "UTF-8";

	/**文件存储**/
	public static String APP_DIR_NAME = "/skynet/";
	public static String APP_DIR =Environment.getExternalStorageDirectory() + APP_DIR_NAME;
	public static String APP_UPLOAD_DIR = APP_DIR + "upload";
    public static final int THRESHOLD_UPLOAD = 1024 * 1024; // 1MB

    
	/** 缓冲字节长度=1024*4B */
	public static final int BUFFER_LENGTH = 4096;
	
	/**服务器端口**/
	public static int PORT = 7676;
	public static String WEBROOT = "/";
	public static final String SERV_ROOT_DIR = APP_DIR + "root/";
	
	/**服务器路径**/
	public static String SERVER_PATH = "http://115.28.16.1/interface/";
}
