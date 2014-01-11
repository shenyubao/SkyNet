package com.weibo.vip.skynet.receiver;

import java.util.HashMap;
import java.util.Map;
import com.weibo.vip.skynet.Constants;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

/**
 * 应用广播接收者
 * @author join
 */
public class AppReceiver extends BroadcastReceiver {

    static final String TAG = "AppReceiver";
    static final boolean DEBUG = false || Constants.DEV_MODE;

    public static final String ACTION_SERV_AVAILABLE = "com.weibo.vip.skynet.action.SERV_AVAILABLE";
    public static final String ACTION_SERV_UNAVAILABLE = "com.weibo.vip.skynet.action.SERV_UNAVAILABLE";

    public static final String PERMIT_WS_RECEIVER = "com.weibo.vip.skynet.permission.WS_RECEIVER";

    private static Map<Context, AppReceiver> mReceiverMap = new HashMap<Context, AppReceiver>();

    private OnServiceListener mListener;

    public AppReceiver(OnServiceListener listener) {
        mListener = listener;
    }

    /**
     * 注册
     */
    public static void register(Context context, OnServiceListener listener) {
        if (mReceiverMap.containsKey(context)) {
            if (DEBUG)
                Log.d(TAG, "This context already registered.");
            return;
        }

        AppReceiver receiver = new AppReceiver(listener);

        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_SERV_AVAILABLE);
        filter.addAction(ACTION_SERV_UNAVAILABLE);
        context.registerReceiver(receiver, filter);

        mReceiverMap.put(context, receiver);

        if (DEBUG)
            Log.d(TAG, "AppReceiver registered.");
    }

    /**
     * 注销
     */
    public static void unregister(Context context) {
        AppReceiver receiver = mReceiverMap.remove(context);
        if (receiver != null) {
            context.unregisterReceiver(receiver);
            receiver = null;

            if (DEBUG)
                Log.d(TAG, "AppReceiver unregistered.");
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (DEBUG)
            Log.d(TAG, action);
        if (mListener == null) {
            return;
        }
        if (ACTION_SERV_AVAILABLE.equals(action)) {
            mListener.onServAvailable();
        } else { // ACTION_SERV_UNAVAILABLE
            mListener.onServUnavailable();
        }
    }

}
