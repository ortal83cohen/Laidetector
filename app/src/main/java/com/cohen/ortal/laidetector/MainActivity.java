package com.cohen.ortal.laidetector;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.hardware.Camera;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;


public class MainActivity extends Activity {

    private CameraView mCameraView;
    private Camera mCamera = null;
    private ImageButton fingerprint;
    private FrameLayout camera_view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        fingerprint = (ImageButton) findViewById(R.id.fingerprint);
        setupCamera();
        fingerprint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCamera.takePicture(null,
                        null, new Camera.PictureCallback() {

                            @Override
                            public void onPictureTaken(byte[] arg0, Camera arg1) {
                                Bitmap bitmapPicture
                                        = BitmapFactory.decodeByteArray(arg0, 0, arg0.length);
                                fingerprint.setImageDrawable(new BitmapDrawable(getResources(), bitmapPicture));
                                mCamera.startPreview();
                            }
                        });
            }
        });
    }

    private void setupCamera() {
        try {
            mCamera = Camera.open();//you can use open(int) to use different cameras
        } catch (Exception e) {
            Log.d("ERROR", "Failed to get camera: " + e.getMessage());
        }
        if (mCamera != null) {
            mCameraView = new CameraView(this, mCamera);//create a SurfaceView to show camera data
            camera_view = (FrameLayout) findViewById(R.id.camera_view);
            camera_view.addView(mCameraView);//add the SurfaceView to the layout
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
