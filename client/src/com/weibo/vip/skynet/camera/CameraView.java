package com.weibo.vip.skynet.camera;

import java.io.IOException;

import android.content.Context;
import android.hardware.Camera;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

//照相视图
public class CameraView extends SurfaceView {

	private Camera mCamera = null;
	private SurfaceHolder holder = null;

	public Camera getCamera() {
		return this.mCamera;
	}
	
	public void takePicture(){
		try {
			if(this.mCamera != null){
				mCamera.takePicture(null, null, new VipPictureCallback());
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	
	public void  startPreview(){
		// 当预览视图创建的时候开启相机
		if(mCamera == null){
			mCamera = Camera.open();
			try {
				// 设置预览
				mCamera.setPreviewDisplay(holder);
			} catch (IOException e) {
				// 释放相机资源并置空
				mCamera.release();
				mCamera = null;
			}
		}else{
			Log.d("CameraView","camera already open");
		}
	}
	
	public void stopPreview(){
		try {
			// 停止预览
			mCamera.stopPreview();
			// 释放相机资源并置空
			mCamera.release();
			mCamera = null;
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	// 构造函数
	public CameraView(Context context) {
		super(context);
		// 操作surface的holder
		holder = this.getHolder();
		// 创建SurfaceHolder.Callback对象
		holder.addCallback(new SurfaceHolder.Callback() {

			@Override
			public void surfaceDestroyed(SurfaceHolder holder) {
				try {
					// 停止预览
					mCamera.stopPreview();
					// 释放相机资源并置空
					mCamera.release();
					mCamera = null;
				} catch (Exception e) {
					// TODO: handle exception
				}
			}

			@Override
			public void surfaceCreated(SurfaceHolder holder) {
				// 当预览视图创建的时候开启相机
				try {
					mCamera = Camera.open();
					// 设置预览
					mCamera.setPreviewDisplay(holder);
				} catch (IOException e) {
					// 释放相机资源并置空
					mCamera.release();
					mCamera = null;
				}

			}
			

			// 当surface视图数据发生变化时，处理预览信息
			@Override
			public void surfaceChanged(SurfaceHolder holder, int format,
					int width, int height) {

				// get Camera parameters
				Camera.Parameters params = mCamera.getParameters();
				// set the focus mode
				params.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
				// set Camera parameters
				mCamera.setParameters(params);

				mCamera.startPreview();
			}

		});
		// 设置Push缓冲类型，说明surface数据由其他来源提供，而不是用自己的Canvas来绘图，在这里是由摄像头来提供数据
		holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
	}

}