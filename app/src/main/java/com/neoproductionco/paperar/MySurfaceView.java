package com.neoproductionco.paperar;

/**
 * Created by Neo on 11.11.2016.
 */

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ImageFormat;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicYuvToRGB;
import android.renderscript.Type;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.TextureView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import net.sourceforge.zbar.Image;
import net.sourceforge.zbar.ImageScanner;
import net.sourceforge.zbar.Symbol;
import net.sourceforge.zbar.SymbolSet;

import java.io.IOException;
import java.util.List;

//import com.google.firebase.crash.FirebaseCrash;

public class MySurfaceView extends SurfaceView implements SurfaceHolder.Callback, View.OnTouchListener, TextureView.SurfaceTextureListener {
	private Toast toast;
	private View blur;
	private boolean QRmode = false;
	private Scenario scenario = new Scenario("Cook coffee");

	public MySurfaceView(Context context, AttributeSet attrs, View blur) {
		super(context, attrs);
		this.context = context;
		this.blur = blur;
		scenario.addStep(new ScenarioStep("Boil water", new ScenarioStep.Color(0, 2, 28)));
		scenario.addStep(new ScenarioStep("Melt coffee", new ScenarioStep.Color(39, 155, 97)));
		scenario.addStep(new ScenarioStep("Bring together", new ScenarioStep.Color(0, 138, 184)));
		scenario.addStep(new ScenarioStep("Bring together", new ScenarioStep.Color(153, 51, 51)));
		//emulatedSurface = new SurfaceView(context);

		callback = new MyPreviewCallback();
		setOnTouchListener(this);
		options.inPreferredConfig = Bitmap.Config.ARGB_8888;
		options.inSampleSize = 8;

		holder = this.getHolder();
		holder.addCallback(this);
		holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		//this.textureView = textureView;//new TextureView(context);
		//this.textureView.setSurfaceTextureListener(this);
//        if (textureView.isAvailable()) {
//            onSurfaceTextureAvailable(textureView.getSurfaceTexture(), textureView.getWidth(), textureView.getHeight());
//        }
		//holder.setFormat(ImageFormat.RG);
		WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		Display display = wm.getDefaultDisplay();
		size = new Point();
		display.getSize(size);
		size.set(1280,720);
		RGBData = new byte[size.x * size.y * 4];
		GrayScaleData = new int[size.x * size.y];
		holder.setFixedSize(size.x, size.y);

	}


	private static final int REQUEST_PERMISSION_CAMERA = 1045;
	private static Camera mCamera;
	private MyPreviewCallback callback;
	public TextureView textureView;
	private SurfaceHolder holder;
	private Paint paint = new Paint();
	private Canvas canvas;
	private Point size;

	Bitmap bitmap;
	Allocation bmData;
	BitmapFactory.Options options = new BitmapFactory.Options();
	byte previewBuffer[];

	byte RGBData[];
	int GrayScaleData[];
	boolean binarization = false;
	boolean sobel = false;
	boolean grayscale = false;
	Context context = null;
	//SurfaceView emulatedSurface;

	public MySurfaceView(Context context, View blur){//, Size size) {
		super(context);
		this.context = context;
		this.blur = blur;
		scenario.addStep(new ScenarioStep("Boil water", new ScenarioStep.Color(0, 2, 28)));
		scenario.addStep(new ScenarioStep("Melt coffee", new ScenarioStep.Color(0, 50, 15)));
		scenario.addStep(new ScenarioStep("Bring together", new ScenarioStep.Color(0, 20, 80)));
		scenario.addStep(new ScenarioStep("Have a nice day!", new ScenarioStep.Color(50, 0, 0)));
		blur.setVisibility(VISIBLE);
		//emulatedSurface = new SurfaceView(context);

		callback = new MyPreviewCallback();
		setOnTouchListener(this);
		options.inPreferredConfig = Bitmap.Config.ARGB_8888;
		options.inSampleSize = 8;

		holder = this.getHolder();
		holder.addCallback(this);
		holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		//this.textureView = textureView;//new TextureView(context);
		//this.textureView.setSurfaceTextureListener(this);
//        if (textureView.isAvailable()) {
//            onSurfaceTextureAvailable(textureView.getSurfaceTexture(), textureView.getWidth(), textureView.getHeight());
//        }
		//holder.setFormat(ImageFormat.RG);
		WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		Display display = wm.getDefaultDisplay();
		size = new Point();
		display.getSize(size);

		Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
		for(int i =0; i<Camera.getNumberOfCameras(); i++) {
			Camera.getCameraInfo(i, cameraInfo);
			Log.d("CameraTest", ""+cameraInfo.orientation+" "+cameraInfo.facing);
		}

		size.set(1280,720);
		RGBData = new byte[size.x * size.y * 4];
		GrayScaleData = new int[size.x * size.y];
		holder.setFixedSize(size.x, size.y);
	}

	@Override
	public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
//		mCamera = Camera.open();
//		textureView.setAlpha(0);
//		Camera.Size previewSize = mCamera.getParameters().getPreviewSize();
//
//		textureView.setLayoutParams(new LinearLayout.LayoutParams(
//				previewSize.width, previewSize.height, Gravity.CENTER));
//
//		try {
//			mCamera.setPreviewTexture(surface);
//		} catch (IOException t) {
//		}
//		//mCamera.addCallbackBuffer(previewBuffer);
//		mCamera.setPreviewCallback(callback);
//
//		mCamera.startPreview();
		//textureView.setAlpha(1.0f);
		//textureView.setRotation(90.0f);
	}

	@Override
	public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

	}

	@Override
	public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
		return true;
	}

	@Override
	public void onSurfaceTextureUpdated(SurfaceTexture surface) {

	}

	public void setTextureView(TextureView viewById) {
		this.textureView = viewById;
		this.textureView.setSurfaceTextureListener(this);
	}

//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.main);
//        surfaceView = (SurfaceView) findViewById(R.id.surfaceView);
//        holder = surfaceView.getHolder();
//        holder.addCallback(this);
//        start();
//    }

	class MyPreviewCallback implements Camera.PreviewCallback{
		//private long timestamp=0;
		@Override
		public void onPreviewFrame(byte[] bytes, Camera camera) {
			if(QRmode){
				Camera.Parameters parameters = camera.getParameters();
				Camera.Size size = parameters.getPreviewSize();

				System.loadLibrary( "iconv" );
				Image barcode = new Image(size.width, size.height, "Y800");
				barcode.setData(bytes);

				ImageScanner scanner = new ImageScanner();
				int result = scanner.scanImage(barcode);

				if (result != 0) {
//            tvScanned.setText("YES");
					ToneGenerator toneG = new ToneGenerator(AudioManager.STREAM_ALARM, 100);
					toneG.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD, 200);
					camera.cancelAutoFocus();
					camera.setPreviewCallback(null);
					camera.stopPreview();
					//mPreviewing = false;
					SymbolSet syms = scanner.getResults();
					for (Symbol sym : syms) {
						String symData = sym.getData();
						if (!TextUtils.isEmpty(symData)) {
							Log.d("LOGS", "Result = "+symData);
							processBarcode(sym);
							Toast.makeText(context, symData, Toast.LENGTH_SHORT).show();
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

			} else {
				//Log.v("CameraTest","Time Gap = "+(System.currentTimeMillis()-timestamp));
				//timestamp=System.currentTimeMillis();
				//decodeYUV420SP(RGBData, bytes, size.x, size.y); - very slow!!!
				Log.d("CameraTest", "preview called");
				bitmap = Bitmap.createBitmap(size.x, size.y, Bitmap.Config.ARGB_8888);
				bmData = renderScriptNV21ToRGBA888(context,size.x,size.y,bytes);
	//			if(grayscale) {
					bmData.copyTo(RGBData);

	//				GrayScaleData = step0_Prepare(RGBData);
					//step1_GetColor(RGBData); - done in the prepare step
				if(!QRmode)  // search for markers mode
					step1_GetColor(RGBData);
	//				if (binarization)
	//					step2_Bin(GrayScaleData);
	//				if (sobel) {
	//					GrayScaleData = step3_Fields(GrayScaleData);
	//					GrayScaleData = step3_Normalize(GrayScaleData);
	//				}
	//				step4_Contures();
	//				step5_Corners();
	//				step6_Coordinates();
	//				RGBData = step0_PrepareOutput(GrayScaleData);

					//Camera.Size imageSize = camera.getParameters().getPictureSize();
					//bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length, options);
					bmData.copyFrom(RGBData);
	//			}
				bmData.copyTo(bitmap);
				bmData.destroy();
				drawPreview(bitmap);
				if(mCamera != null)
					mCamera.addCallbackBuffer(previewBuffer);
			}
		}
	}

	private void processBarcode(Symbol sym) {
		// TODO: 17.06.2017 Make this :)
	}

	public void start(){
		mCamera = Camera.open();
		final List<Camera.Size> sizes = mCamera.getParameters().getSupportedPreviewSizes();
		for(Camera.Size size : sizes){
			Log.v("CameraTest", size.width + " " + size.height);
		}
		//parameters.setPreviewFpsRange(5000,30000);
		Camera.Parameters parameters = mCamera.getParameters();
		parameters.setPreviewSize(size.x, size.y);
		parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
		parameters.setSceneMode(Camera.Parameters.SCENE_MODE_AUTO);
		parameters.setWhiteBalance(Camera.Parameters.WHITE_BALANCE_AUTO);

		previewBuffer = new byte[parameters.getPreviewSize().height*
				parameters.getPreviewSize().width*
				ImageFormat.getBitsPerPixel(ImageFormat.NV21)/8];
		mCamera.addCallbackBuffer(previewBuffer);
		mCamera.setPreviewCallbackWithBuffer(callback);
		try {
			mCamera.setPreviewDisplay(null);
//			mCamera.setPreviewTexture(textureView.getSurfaceTexture());
//			mCamera.setPreviewDisplay(this.getHolder());
		} catch (IOException e) {
			e.printStackTrace();
		}
		mCamera.startPreview();
		//parameters.setPreviewFormat(ImageFormat.RGB_565);
//        mCamera.setPreviewCallbackWithBuffer(new MyPreviewCallback());
	}

	public Allocation renderScriptNV21ToRGBA888(Context context, int width, int height, byte[] nv21) {
		RenderScript rs = RenderScript.create(context);
		ScriptIntrinsicYuvToRGB yuvToRgbIntrinsic = ScriptIntrinsicYuvToRGB.create(rs, Element.U8_4(rs));

		Type.Builder yuvType = new Type.Builder(rs, Element.U8(rs)).setX(nv21.length);
		Allocation in = Allocation.createTyped(rs, yuvType.create(), Allocation.USAGE_SCRIPT);

		Type.Builder rgbaType = new Type.Builder(rs, Element.RGBA_8888(rs)).setX(width).setY(height);
		Allocation out = Allocation.createTyped(rs, rgbaType.create(), Allocation.USAGE_SCRIPT);

		in.copyFrom(nv21);

		yuvToRgbIntrinsic.setInput(in);
		yuvToRgbIntrinsic.forEach(out);
		in.destroy();
		return out;
	}

	int frameNumber = 0;
	public byte[] step1_GetColor(byte[] input){
		frameNumber++;
		long r = 0, g = 0, b = 0, res;
		int index = 0;
		int pix;
		byte r_border = 32;
		byte g_border = 47;
		byte b_border = 62;
		byte a_border = 127;
		for(int i = 0; i < size.y*4; i+=1) {
			for (int j = 0; j < size.x; j+=4) {
				if(i % 4 == 0 && i >= size.y*2-360 && i <= size.y*2+360 && j < 180*4){
					r += input[i*size.x + j+0];
					g += input[i*size.x + j+1];
					b += input[i*size.x + j+2];
					index++;
				}

				if(j < 180*4 && i % 4 == 0 &&
						(
								(i >= size.y*2-360 && i <= size.y*2-350) ||
										(i >= size.y*2+350 && i <= size.y*2+360)
						)
					) {
					input[i * size.x + j + 0] = r_border;
					input[i * size.x + j + 1] = g_border;
					input[i * size.x + j + 2] = b_border;
					input[i * size.x + j + 3] = a_border;
				}

				if( i >= size.y*2-360 && i <= size.y*2+360 && i % 4 == 0 &&
						(
								(j >= 0 && j <= 10) ||
										(j >= 180*4-10 && j <= 180*4)
						)
						) {
					input[i * size.x + j + 0] = r_border;
					input[i * size.x + j + 1] = g_border;
					input[i * size.x + j + 2] = b_border;
					input[i * size.x + j + 3] = a_border;
				}

//                r *= 0.21;
//                g *= 0.72;
//                b *= 0.07;
			}
		}

		r /= index;
		g /= index;
		b /= index;

		if(frameNumber % 10 == 9)
			frameNumber = 0;
		else
			return input;
		if(toast != null)
			toast.cancel();

		int decision = scenario.compareColor((int)r, (int)g, (int)b);
		if(decision != -1) {
			toast = Toast.makeText(context, "Decision = "+scenario.getStep(decision), Toast.LENGTH_SHORT);
		} else {
			toast = Toast.makeText(context, "r = "+r+", g = "+g+", b = "+b, Toast.LENGTH_SHORT);
		}
		toast.show();

		return input;
	}

	public byte[] step1_GrayByte(byte[] input){
		long r, g, b, res;
		int pix;
		for(int i = 0; i < size.y*4; i+=1) {
			for (int j = 0; j < size.x; j+=4) {
				r = input[i*size.x + j+0];
				g = input[i*size.x + j+1];
				b = input[i*size.x + j+2];

				res = (r + g + b)/3;

//                r *= 0.21;
//                g *= 0.72;
//                b *= 0.07;

				input[i*size.x+j+0] = (byte)res;
				input[i*size.x+j+1] = (byte)res;
				input[i*size.x+j+2] = (byte)res;
			}
		}
		return input;
	}

	public static int[] step0_Prepare(byte[] input){
		int result[] = new int[input.length/4];
		for(int i = 0; i < input.length; i+=4)
			// result [i/4] = avg(r+g+b), ignoring alpha
			result[i/4] = (input[i] + input[i+1] + input[i+2])/3;
		return result;
	}

	public static byte[] step0_PrepareByte(byte[] input){
		int result[] = new int[input.length/4];
		long r = 0, g = 0, b = 0;
		for(int i = 0; i < input.length; i+=4)
			// result [i/4] = avg(r+g+b), ignoring alpha
			if(i == 0);
//			result[i/4] = (input[i] + input[i+1] + input[i+2])/3;
		return input;
	}

	public static byte[] step0_PrepareOutput(int[] input){
		byte result[] = new byte[input.length*4];
		for(int i = 0; i < input.length*4; i+=4){
			// setting r,g,b; alpha is always 100%
			result[i] = result[i + 1] = result[i + 2] = (byte)input[i/4];
			result[i+3] = (byte)255;
		}
		return result;
//		byte result[] = new byte[input.length*4];
//		for(int i = 0; i < input.length*4; i+=4){
//			// setting r,g,b; alpha is always 100%
//			result[i] = result[i + 1] = result[i + 2] = (byte)input[i/4];
//			result[i+3] = (byte)255;
//		}
//		return result;
	}

	public int[] step2_Bin(int[] input){
		int treshold = otsuThreshold(input);
		for(int i = 0; i < input.length; i++)
			input[i] = (input[i] > treshold)?0:255;
		return input;
	}

	public int[] step3_Fields(int[] input){
		int gx, gy, f;
		int result[] = new int[input.length];
		for(int i = 0; i < size.y; i++)
			for (int j = 0; j < size.x; j++) {
				if(i == 0 || i == size.y-1 || j == 0 || j == size.x-1) {
					result[i * size.x + j] = 0;
					continue;
				}

				// GX
				gx = (input[(i + 1) * size.x + j - 1] + 2 * input[(i + 1) * size.x + j] + input[(i + 1) * size.x + j + 1]) -
						(input[(i - 1) * size.x + j - 1] + 2 * input[(i - 1) * size.x + j] + input[(i - 1) * size.x + j + 1]);
				// GY
				gy = (input[(i - 1) * size.x + j + 1] + 2 * input[i * size.x + j + 1] + input[(i + 1) * size.x + j + 1]) -
						(input[(i - 1) * size.x + j - 1] + 2 * input[i * size.x + j - 1] + input[(i + 1) * size.x + j - 1]);
				f = (int) Math.sqrt(Math.pow(gx, 2) + Math.pow(gy, 2));
				if(f != 0 && f!= 255 && (i * size.x + j < 5000))
					Log.d("Hi", "value "+f+" at "+(i * size.x + j));
				result[i * size.x + j] = f;
			}
		return result;
	}

	public static int[] step3_Normalize(int[] input){
		int max = -256;
		for(int i = 0; i < input.length; i++)
			if(max < input[i]) max = input[i];
		for(int i = 0; i < input.length; i++)
			input[i] = (int)(((double)input[i])/max*255);
		return input;
	}

	public void step4_Contures(){};
	public void step5_Corners(){};
	public void step6_Coordinates(){};

	private void drawPreview(Bitmap bitmap){
		canvas = holder.lockCanvas();
		if (canvas != null){
			if(bitmap != null){
 				int x = bitmap.getWidth(), y = bitmap.getHeight();
				int x2 = 1, y2 = 1;
				if(y < x){
					y2 = size.y;
					x2 = (int) (((size.y * 1.0) / y) * x);
				}

				//Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, size.x, size.y, true);
				canvas.drawBitmap(bitmap, 0, 0, paint);
				bitmap.recycle();
				//scaledBitmap.recycle();
			}
			holder.unlockCanvasAndPost(canvas);
		}
	}

	static public void decodeYUV420SP(int[] rgb, byte[] yuv420sp, int width, int height) {
		final int frameSize = width * height;
		for (int j = 0, yp = 0; j <height; j++) {
			int uvp = frameSize + (j >> 1) * width, u = 0, v = 0;
			for (int i = 0; i <width; i++, yp++) {
				int y = (0xff & ((int) yuv420sp[yp])) - 16;
				if (y <0)y = 0;
				if ((i & 1) == 0) {
					v = (0xff & yuv420sp[uvp++]) - 128;
					u = (0xff & yuv420sp[uvp++]) - 128;
				}

				int y1192 = 1192 * y;
				int r = (y1192 + 1634 * v);
				int g = (y1192 - 833 * v - 400 * u);
				int b = (y1192 + 2066 * u);

				if (r <0)r = 0;
				else if (r > 262143)r = 262143;
				if (g <0)g = 0;
				else if (g > 262143)g = 262143;
				if (b <0)b = 0;
				else if (b > 262143)
					b = 262143;
				rgb[yp] = 0xff000000 | ((r <<6) & 0xff0000) | ((g >> 2) & 0xff00) | ((b >> 10) & 0xff);
			}
		}
	}

	@Override
	public void surfaceCreated(SurfaceHolder surfaceHolder) {
        try {
            start();
        } catch(Exception e){
            throw e;
            //if(checkPermission())
            //start();
        }
	}

	@Override
	public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
        try {
            mCamera.stopPreview();
            Camera.Parameters parameters = mCamera.getParameters();
            parameters.setPreviewSize(size.x, size.y);
            mCamera.setParameters(parameters);
            mCamera.addCallbackBuffer(previewBuffer);
            mCamera.setPreviewCallbackWithBuffer(callback);
            try {
                mCamera.setPreviewDisplay(null);
//                mCamera.setPreviewTexture(textureView.getSurfaceTexture());
//                mCamera.setPreviewDisplay(this.getHolder());
            } catch (IOException e) {
                e.printStackTrace();
            }
            mCamera.startPreview();
        } catch(Exception e) {
            throw e;
        }
//        if (surfaceHolder.getSurface() == null){
//            return;
//        }
//
//        try {
//            mCamera.stopPreview();
//        } catch (Exception e){
//            // ignore: tried to stop a non-existent preview
//        }
//
//        try {
//            start();
//            //mCamera.setPreviewDisplay(emulatedSurface.getHolder());
//        } catch (Exception e){
//            Log.d("Hi", "Error starting camera preview: " + e.getMessage());
//            throw e;
//        }
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
		if (mCamera != null) {
			mCamera.stopPreview();
			this.getHolder().removeCallback(this);
			mCamera.release();
			mCamera = null;
			this.destroyDrawingCache();
		}
	}

	/* Функция возвращает порог бинаризации для полутонового изображения image с общим числом пикселей size */
	int otsuThreshold(int[] image){
		int min=image[0], max=min;
		int i, temp, temp1;
		int hist[];
		int histSize;

		int alpha, beta, threshold=0;
		double sigma, maxSigma=-1;
		double w1,a;

		/**** Построение гистограммы ****/
        /* Узнаем наибольший и наименьший полутон */
		for(i=1;i<image.length;i+=1)
		{
			temp=image[i];
			if(temp<min)   min = temp;
			if(temp>max)   max = temp;
		}

		histSize=max-min+1;
		hist = new int[histSize];

		for(i=0;i<histSize;i++)
			hist[i]=0;

        /* Считаем сколько каких полутонов */
		for(i=0;i<image.length;i+=1)
			hist[ image[i] - min ]++;

		/**** Гистограмма построена ****/

		temp=temp1=0;
		alpha=beta=0;
        /* Для расчета математического ожидания первого класса */
		for(i=0;i<=(max-min);i++){
			temp += i*hist[i];
			temp1 += hist[i];
		}

        /* Основной цикл поиска порога
        Пробегаемся по всем полутонам для поиска такого, при котором внутриклассовая дисперсия минимальна */
		for(i=0;i<(max-min);i++)
		{
			alpha+= i*hist[i];
			beta+=hist[i];

			w1 = (double)beta / temp1;
			a = (double)alpha / beta - (double)(temp - alpha) / (temp1 - beta);
			sigma=w1*(1-w1)*a*a;

			if(sigma>maxSigma)
			{
				maxSigma=sigma;
				threshold=i;
			}
		}
		hist = null;
		return threshold + min;
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		switch(event.getAction()){
			case MotionEvent.ACTION_DOWN:
				if(event.getX() < size.x/3) // first 1/3
					grayscale = ! grayscale;
				else
				if(event.getX() > 2*size.x/3) // last 1/3
					sobel = ! sobel;
				else
					binarization = !binarization; // middle


		}
		return false;
	}
}

// Code to convert from the byte[] RGB_565 array to real R, G & B values
//pix = ((input[i*size.x+j] << 8) & 0x0000ff00) | (input[i*size.x+j+1] & 0x000000ff);
//                r = (pix & 0xf800) >> 11;// >> 8 & 0xff;
//                g = (pix & 0x07e0) >> 5;// >> 8 & 0xff;
//                b = (pix & 0x001f);// & 0xff;

// Code to convert from the int[] ARGB_8888 array to real R, G & B values
//r = (input[i*size.x + j] & 0x00ff_0000) >> 16;
//g = (input[i*size.x + j] & 0x0000_ff00) >> 8;
//b = input[i*size.x + j] & 0x0000_00ff;
