package com.neoproductionco.paperar;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.ImageFormat;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Size;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

	private static final String LOG_TAG = "PaperAR logs";
	private CameraManager mCameraManager = null;
	private CameraDevice activeCameraDevice = null;
	private String activeCameraID = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		mCameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);

		try {
			// Получения списка камер в устрйстве
			String[] cameraList = mCameraManager.getCameraIdList();
			for (String cameraID : cameraList) {
				Log.i(LOG_TAG, "cameraID: " + cameraID);
			}
			activeCameraID = cameraList[0];

			Log.i(LOG_TAG, "Cameras' characteristics to be printed");
			for (String cameraID : cameraList) {
				printCharacteristics(cameraID);
			}
		} catch (CameraAccessException e) {
			Log.e(LOG_TAG, e.getMessage());
			e.printStackTrace();
		}

		openCamera(mCameraManager, activeCameraID, new CameraDevice.StateCallback() {
			@Override
			public void onOpened(@NonNull CameraDevice camera) {
				Log.i(MainActivity.LOG_TAG, "Open camera  with id:"+activeCameraDevice.getId());
				activeCameraDevice = camera;
			}

			@Override
			public void onDisconnected(@NonNull CameraDevice camera) {
				Log.i(MainActivity.LOG_TAG, "Disconnected camera  with id:"+activeCameraDevice.getId());
				activeCameraDevice.close();
				activeCameraDevice = null;
			}

			@Override
			public void onError(@NonNull CameraDevice camera, int error) {
				Log.i(MainActivity.LOG_TAG, "Error on camera with id:"+activeCameraDevice.getId()+", error: "+error);
			}
		});
	}

	private void printCharacteristics(String cameraID) throws CameraAccessException {
		// Получения характеристик камеры
		CameraCharacteristics cc = mCameraManager.getCameraCharacteristics(cameraID);
		// Получения списка выходного формата, который поддерживает камера
		StreamConfigurationMap configurationMap =
				cc.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);

		// Получения списка разрешений которые поддерживаются для формата jpeg
		Size[] sizesJPEG = configurationMap.getOutputSizes(ImageFormat.JPEG);

		if (sizesJPEG != null) {
			for (Size item : sizesJPEG) {
				Log.i(LOG_TAG, "w:" + item.getWidth() + " h:" + item.getHeight());
			}
		} else {
			Log.e(LOG_TAG, "camera with id: " + cameraID + " don`t support JPEG");
		}
	}

	public void openCamera(CameraManager mCameraManager, String mCameraID, CameraDevice.StateCallback mCameraCallback) {
		try {
			if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
				Toast.makeText(this, "NOT ENOUGH PERMISSIONS!", Toast.LENGTH_LONG).show();
				finish();
			}

			mCameraManager.openCamera(mCameraID, mCameraCallback, null);
		} catch (CameraAccessException e) {
			Log.e(MainActivity.LOG_TAG,e.getMessage());
			//e.printStackTrace();
		}
	}
}
