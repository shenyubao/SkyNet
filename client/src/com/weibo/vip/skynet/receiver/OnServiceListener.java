package com.weibo.vip.skynet.receiver;

public interface OnServiceListener {

    /**
     * 服务可用
     */
    void onServAvailable();

    /**
     * 服务不可用
     */
    void onServUnavailable();

}
