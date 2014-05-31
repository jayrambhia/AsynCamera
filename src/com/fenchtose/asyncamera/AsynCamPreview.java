package com.fenchtose.asyncamera;

import java.io.IOException;
import java.util.List;

import android.graphics.Bitmap;
import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.SurfaceHolder;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;

public class AsynCamPreview extends Asyncam 
			 implements SurfaceHolder.Callback,
			 			Camera.PreviewCallback {
	
	private Camera mCamera;
	public Camera.Parameters params;
	private SurfaceHolder sHolder;
	private ImageView myCameraPreview = null;
	private Bitmap mBitmap = null;
	
	// data arrays
	private int[] pixels = null;
	private float[] floatpixels = null;
	private byte[] FrameData = null;
	
	// Camera params
	public float maxZoom;
	private int imageFormat;
	private int previewSizeWidth;
	private int previewSizeHeight;
	private int camId = 1;
	
	// flags and counts
	private boolean bProcessing = false;
	private int frameCount = 0;
	private boolean doProcessing = false;
	public boolean mProcessInProgress = false;
	
	public static final String TAG = "AsynCamPreview";
	
	private long mTiming[] = new long[50];
    private int mTimingSlot = 0;
	
	Handler mHandler = new Handler(Looper.getMainLooper());
	
	static {
		System.loadLibrary("ImageProcessing");
		Log.i(TAG, "Native library loaded");
	}
	
	public AsynCamPreview(int previewLayoutWidth, int previewLayoutHeight,
						ImageView cameraPreview){
		previewSizeWidth = previewLayoutWidth;
		previewSizeHeight = previewLayoutHeight;
		myCameraPreview =cameraPreview;
	}
	
	
	@Override
	public void onPreviewFrame(byte[] data, Camera camera) {
		// TODO Auto-generated method stub
		if (imageFormat == ImageFormat.NV21){
			Log.i(TAG, "onPreviewFrame");
			if(mProcessInProgress){
				mCamera.addCallbackBuffer(data);
			}
			if (data == null){
				return;
			}
			
			mProcessInProgress = true;
			if (mBitmap == null) {
				mBitmap = Bitmap.createBitmap(previewSizeWidth, previewSizeHeight,
											  Bitmap.Config.ARGB_8888);
				myCameraPreview.setImageBitmap(mBitmap);
			}
			myCameraPreview.invalidate();
			
			mCamera.addCallbackBuffer(data);
			mProcessInProgress = true;
			
			new ProcessPreviewDataTask().execute(data);
		}
		
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		sHolder = holder;
		if (camId == 0) {
			mCamera = Camera.open(Camera.CameraInfo.CAMERA_FACING_FRONT);
		}
		else {
			mCamera = Camera.open(Camera.CameraInfo.CAMERA_FACING_BACK);
		}
		
		try {
			mCamera.setPreviewDisplay(holder);
			mCamera.setPreviewCallback(this);
		} catch (IOException e) {
			mCamera.release();
			mCamera= null;
		}
		
		params = mCamera.getParameters();
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		// TODO Auto-generated method stub
		params = mCamera.getParameters();
		List<Camera.Size> sizes = params.getSupportedPictureSizes();
		this.params.setPreviewSize(previewSizeWidth, previewSizeHeight);
		
		mBitmap = Bitmap.createBitmap(previewSizeWidth, previewSizeHeight,
				  					  Bitmap.Config.ARGB_8888);
		myCameraPreview.setImageBitmap(mBitmap);
		
		pixels = new int[previewSizeWidth * previewSizeHeight];
		//floatpixels = new float[previewSizeWidth * previewSizeHeight * 3];
		imageFormat = params.getPreviewFormat();
		mCamera.setParameters(params);
		mCamera.startPreview();
		
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		mCamera.setPreviewCallback(null);
		mCamera.stopPreview();
		mCamera.release();
		mCamera = null;
	}
	
	@Override
	public void onPause() {
		this.mCamera.stopPreview();
	}
	
	private class ProcessPreviewDataTask extends AsyncTask<byte[], Void, Boolean> {

		@Override
		protected Boolean doInBackground(byte[]... datas) {
			// TODO Auto-generated method stub
			Log.i(TAG, "background process started");
			byte[] data = datas[0];
			
			long t1 = java.lang.System.currentTimeMillis();
			
			// process data function
			convertGray(previewSizeWidth, previewSizeHeight, data, pixels);
			Log.i(TAG, "convertGray done");
			
			long t2 = java.lang.System.currentTimeMillis();
			mTiming[mTimingSlot++] = t2 - t1;
			if (mTimingSlot >= mTiming.length) {
				float total = 0;
				for(int i=0; i < mTiming.length; i++) {
					total += (float)mTiming[i];
				}
				total /= mTiming.length;
				Log.e(TAG, "time + " + String.valueOf(total));
				mTimingSlot = 0;
			}
			Log.i(TAG, "processing time = " + String.valueOf(t2 - t1));
			
			mCamera.addCallbackBuffer(data);
			mProcessInProgress = false;
			Log.i(TAG, "doInBackground "+String.valueOf(isCancelled()));
			return true;
		}
		
		@Override
		protected void onPostExecute(Boolean result){
			Log.i(TAG, "running onPostExecute");
			// set pixels 
			//myCameraPreview.
			myCameraPreview.invalidate();
			mBitmap.setPixels(pixels, 0, previewSizeWidth,
					0, 0, previewSizeWidth, previewSizeHeight);
			myCameraPreview.setImageBitmap(mBitmap);
			Log.i(TAG, "bitmap set in imageview");
			
		}
	}
	
	public native boolean convertGray(int width, int height, byte[] NV21FrameData, int[] pixels);
}