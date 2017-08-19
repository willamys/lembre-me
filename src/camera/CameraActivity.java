package camera;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.src.android.R;
import com.src.android.R.id;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;

 /**@author Willamys Araujo
 **Generate for Jacroid**/

public class CameraActivity extends Activity {

	private Camera mCamera;
	private CameraPreview mPreview;
	private String tag = "CameraActivity";
	protected static final int MEDIA_TYPE_IMAGE = 1;
	protected static String APP_NAME;
	protected File pictureFile;
	public static Camera getCameraInstance(){
		Camera c = null;
		try {
			c = Camera.open(); // attempt to get a Camera instance
		}
		catch (Exception e){
			// Camera is not available (in use or does not exist)
		}
		return c; // returns null if camera is unavailable
	}
	/** Check if this device has a camera */
	private static boolean checkCameraHardware(Context context) {
		if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)){
			// this device has a camera
			return true;
		} else {
			// no camera on this device
			return false;
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.camera);

		PackageInfo pinfo =  null;

		try {
			pinfo = getPackageManager().getPackageInfo(getPackageName(), 0);
			setAppName(pinfo.applicationInfo.loadLabel(getPackageManager()).toString());
		} catch (NameNotFoundException e) {
			Log.d(tag, "Error: "+ e.getMessage());
		}
		
		// Create an instance of Camera
		if(checkCameraHardware(this)){
			mCamera = getCameraInstance();
			
			// Create our Preview view and set it as the content of our activity.
			mPreview = new CameraPreview(this, mCamera);
			FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);
			
			FrameLayout.LayoutParams previewLayoutParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
			
			preview.addView(mPreview,0,previewLayoutParams);

			Button captureButton = (Button) findViewById(id.button_capture);
			captureButton.setOnClickListener(
					new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							// get an image from the camera
							mCamera.takePicture(null, null, mPicture);
						}
					}
					);
		}else{
			Log.d(tag, "Error: Device does not have a camera.");
			finish();
		}
	}

	private PictureCallback mPicture = new PictureCallback() {

		@Override
		public void onPictureTaken(byte[] data, Camera camera) {

			pictureFile = getOutputMediaFile(MEDIA_TYPE_IMAGE);

			if (pictureFile == null){
				Log.d(tag, "Error creating media file, check storage permissions: ");
				return;
			}

			try {
				FileOutputStream fos = new FileOutputStream(pictureFile);
				fos.write(data);
				fos.close();
				Toast.makeText(CameraActivity.this,"Picture Save at "+ pictureFile.getAbsolutePath(), Toast.LENGTH_LONG).show();
				onBackPressed();
				//upload(pictureFile);
			} catch (FileNotFoundException e) {
				Log.d(tag, "File not found: " + e.getMessage());
			} catch (IOException e) {
				Log.d(tag, "Error accessing file: " + e.getMessage());
			}
		}
	};
	/** Create a File for saving an image or video */
	private static File getOutputMediaFile(int type){
		// To be safe, you should check that the SDCard is mounted
		// using Environment.getExternalStorageState() before doing this.

		File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
				Environment.DIRECTORY_PICTURES), getAppName());
		// The picture will be save in the folder Pictures/APP_NAME
		// This location works best if you want the created images to be shared
		// between applications and persist after your app has been uninstalled.

		// Create the storage directory if it does not exist
		if (! mediaStorageDir.exists()){
			if (! mediaStorageDir.mkdirs()){
				Log.d(getAppName(), "failed to create directory");
				return null;
			}
		}

		// Create a media file name
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
		File mediaFile;
		if (type == MEDIA_TYPE_IMAGE){
			mediaFile = new File(mediaStorageDir.getPath() + File.separator +
					"IMG_"+ timeStamp + ".jpg");
		} else {
			return null;
		}

		return mediaFile;
	}

	private void releaseCamera(){
		if (mCamera != null){
			mCamera.release();        // release the camera for other applications
			mCamera = null;
		}
	}

	@Override
	public void onBackPressed() {
		if(pictureFile != null){
			Intent data = new Intent();
			data.putExtra("camera", pictureFile.getAbsolutePath());
			setResult(2,data);
			finish();
			releaseCamera();
		}else{
			Intent data = new Intent();
			data.putExtra("camera", "");
			setResult(2,data);
			finish();
			releaseCamera();
		}
	}
	 protected static String getAppName() {
		 return APP_NAME;
	 }

	 protected static void setAppName(String appName) {
		 APP_NAME = appName;
	 }
}