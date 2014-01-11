package com.weibo.vip.skynet.camera;

import android.hardware.Camera;

import com.weibo.vip.skynet.VipApplication;

public class VipPictureCallback implements Camera.PictureCallback{

	@Override
	public void onPictureTaken(byte[] data, Camera camera) {
		VipApplication.getInstance().pictureBytes = data;
	}

}
