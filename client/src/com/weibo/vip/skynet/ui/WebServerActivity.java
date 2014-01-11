package com.weibo.vip.skynet.ui;
import com.weibo.vip.skynet.service.WebService;
import com.weibo.vip.skynet.webserver.WebServer.OnWebServListener;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Looper;

public abstract class WebServerActivity extends Activity implements OnWebServListener{

	static final String TAG = "WebServerActivity";

	protected Intent webServIntent;
    protected WebService webService;
    private boolean isBound = false;
    
    private ServiceConnection servConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            webService = ((WebService.LocalBinder) service).getService();
            webService.setOnWebServListener(WebServerActivity.this);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            webService = null;
        }
    };
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        webServIntent = new Intent(this, WebService.class);
    }

    protected boolean isBound() {
        return this.isBound;
    }
    
    protected void doBindService() {
        // Restore configs of port and root here.
//        PreferActivity.restore(PreferActivity.KEY_SERV_PORT, PreferActivity.KEY_SERV_ROOT);
    	//TODO:偏好设置
        bindService(webServIntent, servConnection, BIND_AUTO_CREATE);
        
        isBound = true;
    }

    protected void doUnbindService() {
        if (isBound) {
            unbindService(servConnection);
            isBound = false;
        }
    }
    
    @Override
    protected void onDestroy() {
        doUnbindService();
        super.onDestroy();
    }
}
