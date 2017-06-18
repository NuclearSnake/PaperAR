package com.neoproductionco.paperar;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.hardware.camera2.CameraManager;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.TextView;
import android.widget.Toast;

import net.sourceforge.zbar.Config;
import net.sourceforge.zbar.Image;
import net.sourceforge.zbar.ImageScanner;
import net.sourceforge.zbar.Symbol;
import net.sourceforge.zbar.SymbolSet;

import java.io.IOException;
import java.util.List;

/**
 * Created by Neo on 18.06.2017.
 */

public class ScannerActivity extends Activity implements SurfaceHolder.Callback {
	private final static String DEBUG_TAG = "MakePhotoActivity";
	private static final String TAG = "myTag";
	private Camera camera;
	private int cameraId = 0;
	private boolean scan = true;

	ImageScanner scanner;
	SurfaceView svPreview;
	TextView tvScanned;
	MyPreview previewcallback;


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		System.loadLibrary("zbarjni");
		System.loadLibrary( "iconv" );
		scanner = new ImageScanner();
		scanner.setConfig(Symbol.QRCODE, Config.ENABLE, 1);

		setContentView(R.layout.scan_activity);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		svPreview = (SurfaceView)findViewById(R.id.svPreview);

		previewcallback = new MyPreview(tvScanned);
	}

	@Override
	protected void onResume() {
		super.onResume();
		// do we have a camera?
		if (!getPackageManager()
				.hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
			Toast.makeText(this, "No camera on this device", Toast.LENGTH_LONG)
					.show();
		} else {
			cameraId = findFrontFacingCamera();
			if (cameraId < 0) {
				Toast.makeText(this, "No front facing camera found.",
						Toast.LENGTH_LONG).show();
			} else {
				camera = Camera.open();
				Camera.Parameters parameters = camera.getParameters();
				parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
				parameters.setSceneMode(Camera.Parameters.SCENE_MODE_AUTO);
				parameters.setWhiteBalance(Camera.Parameters.WHITE_BALANCE_AUTO);
				parameters.setPictureFormat(ImageFormat.JPEG);
				parameters.setJpegQuality(100);
				List<Camera.Size> sizes = parameters.getSupportedPictureSizes();
				Camera.Size size = sizes.get(0);
				for(int i=0;i<sizes.size();i++)
				{
					if(sizes.get(i).width > size.width)
						size = sizes.get(i);
				}
				parameters.setPictureSize(size.width, size.height);
				camera.setParameters(parameters);
			}
		}
		svPreview.getHolder().addCallback(this);
	}

	private int findFrontFacingCamera() {
		int cameraId = -1;
		// Search for the front facing camera
		int numberOfCameras = Camera.getNumberOfCameras();
		for (int i = 0; i < numberOfCameras; i++) {
			Camera.CameraInfo info = new Camera.CameraInfo();
			Camera.getCameraInfo(i, info);
			if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
				Log.d(DEBUG_TAG, "Camera found");
				cameraId = i;
				break;
			}
		}
		return cameraId;
	}

	@Override
	protected void onPause() {
		if (camera != null) {
			svPreview.getHolder().removeCallback(this);
			camera.setPreviewCallback(null);
			camera.stopPreview();
			camera.release();
			camera = null;
		}
		super.onPause();
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {

	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
		camera.setPreviewCallback(previewcallback);
		try {
			camera.setPreviewDisplay(svPreview.getHolder());
		} catch (IOException e) {
			e.printStackTrace();
		}
		camera.startPreview();

	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {

	}

	private class MyPreview implements Camera.PreviewCallback {

		TextView tvScanned;

		public MyPreview(TextView textView){
			tvScanned = textView;
		}

		@Override
		public void onPreviewFrame(byte[] data, Camera camera) {
			Camera.Parameters parameters = camera.getParameters();
			Camera.Size size = parameters.getPreviewSize();

			Image barcode = new Image(size.width, size.height, "Y800");
			barcode.setData(data);

			int result = scanner.scanImage(barcode);

			if (result != 0) {
//            tvScanned.setText("YES");
				//mPreviewing = false;
				SymbolSet syms = scanner.getResults();
				for (Symbol sym : syms) {
					String symData = sym.getData();
					if (!TextUtils.isEmpty(symData)) {
						Log.d("LOGS", "Result = "+symData);
						if(Scenario.prepareScenario(symData) != null) {
							textOutput(symData);
							ToneGenerator toneG = new ToneGenerator(AudioManager.STREAM_ALARM, 100);
							toneG.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD, 200);
							camera.cancelAutoFocus();
							camera.setPreviewCallback(null);
							camera.stopPreview();
						} else
							Toast.makeText(ScannerActivity.this, "Bad QR", Toast.LENGTH_SHORT).show();

//						Toast.makeText(ScannerActivity.this, symData, Toast.LENGTH_SHORT).show();
//                    Intent dataIntent = new Intent();
						//dataIntent.putExtra(SCAN_RESULT, symData);
						//dataIntent.putExtra(SCAN_RESULT_TYPE, sym.getType());
						//setResult(Activity.RESULT_OK, dataIntent);
						//finish();
						break;
					}
				}
			} else;
//            tvScanned.setText("NO");
		}

		private void textOutput(String script){
			Intent intent = new Intent(ScannerActivity.this, InstructionActivity.class);
			intent.putExtra("script", script);
			ScannerActivity.this.startActivityForResult(intent, 985);
		}

	}
}