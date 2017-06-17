package com.neoproductionco.paperar;

import android.Manifest;
import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.Toast;

/**
 * Created by Neo on 21.09.2016.
 */

public class MainActivity extends Activity {
	private static final int REQUEST_PERMISSION_CAMERA = 1045;

	MySurfaceView surfaceView;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		Scenario.test();
		// Hide the window title.
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		if(checkPermission()) {
			setContentView(R.layout.activity_main2);
			TextureView textureView = (TextureView)findViewById(R.id.textureView);
			textureView.setAlpha(0);
			//TextureView textureView = new TextureView(this);
			//surfaceView = (MySurfaceView) findViewById(R.id.mySurfaceView);
			View blur = getLayoutInflater().inflate(R.layout.activity_main, null);
			blur.setVisibility(View.GONE);
			surfaceView = new MySurfaceView(this, blur);
			surfaceView.setTextureView(textureView);
			FrameLayout layout = (FrameLayout) findViewById(R.id.lllayout);
			layout.addView(surfaceView);
			layout.addView(blur);
			ViewGroup.LayoutParams layoutParams = textureView.getLayoutParams();
			layoutParams.height = 1;
			textureView.setLayoutParams(layoutParams);
		}
	}

	@Override
	protected void onStop() {
		surfaceView.surfaceDestroyed(surfaceView.getHolder());
		super.onStop();
	}

	private boolean checkPermission(){
		// Here, thisActivity is the current activity
		if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
				!= PackageManager.PERMISSION_GRANTED) {

			// Should we show an explanation?
			if (ActivityCompat.shouldShowRequestPermissionRationale(this,
					Manifest.permission.READ_CONTACTS)) {
				Toast.makeText(this, "Camera permission needed to work with the app", Toast.LENGTH_LONG).show();

			} else {

				// No explanation needed, we can request the permission.

				ActivityCompat.requestPermissions(this,
						new String[]{Manifest.permission.CAMERA},
						REQUEST_PERMISSION_CAMERA);

				// MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
				// app-defined int constant. The callback method gets the
				// result of the request.
			}
		} else
			return true;
		return false;
	}

	//@Override
	public void onRequestPermissionsResult(int requestCode,
										   String permissions[], int[] grantResults) {
		switch (requestCode) {
			case REQUEST_PERMISSION_CAMERA: {
				// If request is cancelled, the result arrays are empty.
				if (grantResults.length > 0
						&& grantResults[0] == PackageManager.PERMISSION_GRANTED) {
					//setContentView(new MySurfaceView(this));
				} else {
					Toast.makeText(this, "Permission needed :'(", Toast.LENGTH_LONG).show();
				}
				return;
			}
		}
	}
}