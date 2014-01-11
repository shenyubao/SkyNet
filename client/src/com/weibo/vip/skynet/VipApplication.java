package com.weibo.vip.skynet;

import java.io.File;
import java.io.IOException;

import com.weibo.vip.skynet.service.AppService;
import com.weibo.vip.skynet.util.FileUtil;
import com.weibo.vip.skynet.Constants;

import android.app.Application;
import android.content.Intent;
import android.os.Looper;
import android.os.StrictMode;

public class VipApplication extends Application{

	private static VipApplication self;
	private Intent ServIntent;
	public static boolean lockLooper;
	public static float batteryLevel;
	
	public static boolean cameraOpen = false;
	public byte[] pictureBytes;
	
	@Override
	public void onCreate(){
		super.onCreate();
		self = this;
		ServIntent = new Intent(AppService.ACTION);

		
		initAppDir();
		if (!Constants.DEV_MODE) {
			new CrashHandler(this);
		}
	}
	
	public static VipApplication getInstance(){
		return self;
	}
	
    /**
     * @brief 开启全局服务
     */
    public void startAppService() {
        startService(ServIntent);
    }

    /**
     * @brief 停止全局服务
     */
    public void stopAppService() {
        stopService(ServIntent);
    }
	
    /**
     * @brief 初始化应用目录
     */
    private void initAppDir() {        
        if(Constants.DEV_MODE){
        	//清理原有文件
//        	FileUtil.deleteFile(new File(Constants.SERV_ROOT_DIR)); // 清理服务文件目录
        }
       
        try {
            // 重新复制到SDCard，仅当文件不存在时
        	FileUtil.assetsCopy(getApplicationContext(),"skynet", Constants.SERV_ROOT_DIR, true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
}
