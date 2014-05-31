package com.fenchtose.asyncamera;

import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.content.pm.ActivityInfo;
import android.hardware.Camera.PreviewCallback;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.os.Build;

public class Asyncam extends Activity {
	
	private AsynCamPreview camPreview;
	private ImageView myCameraPreview = null;
	private RelativeLayout mainLayout;
	private int preivewSizeWidth = 640;
	private int preivewSizeHeight = 480;
	private final String TAG = "Asyncam";
	private SurfaceView camView;
	private int camId = 1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		/*
		ActionBar actionBar = getActionBar();
		actionBar.setDisplayShowTitleEnabled(false);
		actionBar.setDisplayShowHomeEnabled(false);
		this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		*/
		Log.i(TAG, "setting content view");
		setContentView(R.layout.activity_asyncam);
		
		mainLayout = (RelativeLayout) findViewById(R.id.container);
		//myCameraPreview = (ImageView) findViewById(R.id.result_view);
		myCameraPreview = new ImageView(this);
		
		camView = new SurfaceView(this);
		SurfaceHolder camHolder = this.camView.getHolder();
		
		camPreview = new AsynCamPreview(preivewSizeWidth, preivewSizeHeight,
										myCameraPreview);
		camHolder.addCallback(camPreview);
		camHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
				preivewSizeWidth, preivewSizeHeight); 
		params.leftMargin = 10;
		params.topMargin = 10;

		mainLayout.addView(camView, params);
		
		params = new RelativeLayout.LayoutParams(
				preivewSizeWidth/2, preivewSizeHeight/2); 
		params.leftMargin = 10;
		params.topMargin = 10;
		
		mainLayout.addView(myCameraPreview, params);
	}
	
	protected void onPause() {
		super.onPause();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.asyncam, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
