package com.cohen.ortal.laidetector;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.MotionEventCompat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.HashMap;


public class MainActivity extends Activity {

    private CameraView mCameraView;
    private Camera mCamera = null;
    private ImageButton fingerprint;
    private FrameLayout camera_view;
    private TextView textView;
    private TextView textViewTitle;
    private com.github.rahatarmanahmed.cpv.CircularProgressView circularProgressView;
    boolean buttonTuched = false;
    final Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        fingerprint = (ImageButton) findViewById(R.id.fingerprint);
        textView = (TextView) findViewById(android.R.id.text1);
        textViewTitle = (TextView) findViewById(android.R.id.title);
        circularProgressView = (com.github.rahatarmanahmed.cpv.CircularProgressView) findViewById(R.id.progress_view);

        fingerprint.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (MotionEventCompat.getActionMasked(event)) {
                    case MotionEvent.ACTION_UP:
                        buttonTuched = false;
                        circularProgressView.setVisibility(View.GONE);
                        handler.removeCallbacks(runnableAction);
                        break;
                    case MotionEvent.ACTION_DOWN:
                        buttonTuched = true;
                        textViewTitle.setVisibility(View.GONE);
                        circularProgressView.setVisibility(View.VISIBLE);
                        circularProgressView.startAnimation();

                        handler.postDelayed(runnableAction, 3000);

                        break;
                    default:
                        break;
                }
                return false;
            }
        });
    }

    final Runnable runnableAction = new Runnable() {

        @Override
        public void run() {

                mCamera.takePicture(null,
                        null, new Camera.PictureCallback() {

                            @Override
                            public void onPictureTaken(byte[] arg0, Camera arg1) {
                                Bitmap bitmapPicture
                                        = BitmapFactory.decodeByteArray(arg0, 0, arg0.length);

                                textView.setText(String.valueOf(getNumberOfDifferentColors(bitmapPicture)));
                                textViewTitle.setVisibility(View.VISIBLE);
                                textViewTitle.setText(getNumberOfDifferentColors(bitmapPicture) > 40 ? "False" : "True");
                                circularProgressView.setVisibility(View.GONE);
                                mCamera.startPreview();
                            }
                        });
            }
        

    };


    private void setupCamera() {
        try {
            mCamera = Camera.open();//you can use open(int) to use different cameras
        } catch (Exception e) {
            Log.d("ERROR", "Failed to get camera: " + e.getMessage());
        }
        if (mCamera != null) {
            if (mCameraView == null) {
                mCameraView = new CameraView(this, mCamera);
                camera_view = (FrameLayout) findViewById(R.id.camera_view);
                camera_view.addView(mCameraView);
            } else {
                mCameraView.setCamera(mCamera);
            }

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

    @Override
    protected void onResume() {
        super.onResume();
        setupCamera();
    }

    public int getNumberOfDifferentColors(Bitmap bitmap) {
        if (bitmap == null)
            throw new NullPointerException();

        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int size = width * height;
        int pixels[] = new int[size];

        Bitmap bitmap2 = bitmap.copy(Bitmap.Config.ARGB_4444, false);

        bitmap2.getPixels(pixels, 0, width, 0, 0, width, height);

        HashMap<Integer, Integer> colorMap = new HashMap<Integer, Integer>();
        int color = 0;
        for (int i = 0; i < pixels.length; i++) {
            color = pixels[i];
            if (!colorMap.containsKey(color)) {
                colorMap.put(color, 0);
            }
        }

        return colorMap.size();
    }
}
