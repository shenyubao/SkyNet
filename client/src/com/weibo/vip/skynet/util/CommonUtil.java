package com.weibo.vip.skynet.util;


import com.weibo.vip.skynet.VipApplication;

import android.content.Context;
import android.text.ClipboardManager;
import android.text.format.DateFormat;

public class CommonUtil {
    static final class Holder {
        static CommonUtil instance = new CommonUtil();
    }

    public static CommonUtil getSingleton() {
        return Holder.instance;
    }

    private Context mContext;

    private CommonUtil() {
        mContext = VipApplication.getInstance().getBaseContext();
    }
    
    /** 年月日时分秒 */
    public final String FORMAT_YMDHMS = "yyyy-MM-dd kk:mm:ss";
    /** 年月日 */
    public final String FORMAT_YMD = "yyyy-MM-dd";
    /** 时分秒 */
    public final String FORMAT_HMS = "kk:mm:ss";

    /** 获得当前时间 */
    public CharSequence currentTime(CharSequence inFormat) {
        return DateFormat.format(inFormat, System.currentTimeMillis());
    }
    
}
