package com.cohen.ortal.laidetector;


import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.MotionEventCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Random;


public class MainActivity extends AppCompatActivity {

    final Handler handler = new Handler();
    private CameraView mCameraView;
    private Camera mCamera = null;
    private ImageButton fingerprint;
    private FrameLayout camera_view;
    private TextView textView;
    private TextView textViewTitle;
    private com.github.rahatarmanahmed.cpv.CircularProgressView circularProgressView;
    final Runnable runnableAction = new Runnable() {

        @Override
        public void run() {
            mCamera.takePicture(null,
                    null, new Camera.PictureCallback() {
                        @Override
                        public void onPictureTaken(byte[] arg0, Camera arg1) {
                            Bitmap bitmapPicture = BitmapFactory.decodeByteArray(arg0, 0, arg0.length);
                            textView.setText(String.valueOf(getNumberOfDifferentColors(bitmapPicture)));
                            textViewTitle.setVisibility(View.VISIBLE);
                            int numberOfDifferentColors = getNumberOfDifferentColors(bitmapPicture);
                            if (numberOfDifferentColors < 40) {
                                textViewTitle.setText("True");
                            } else if (numberOfDifferentColors < 60) {
                                textViewTitle.setText("Unclear");
                            } else {
                                textViewTitle.setText("False");
                            }
                            circularProgressView.setVisibility(View.GONE);
                            mCamera.startPreview();
                        }
                    });
        }
    };
    private AppBar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        fingerprint = (ImageButton) findViewById(R.id.fingerprint);
        textView = (TextView) findViewById(android.R.id.text1);
        textViewTitle = (TextView) findViewById(android.R.id.title);
        circularProgressView = (com.github.rahatarmanahmed.cpv.CircularProgressView) findViewById(R.id.progress_view);
        setupToolbar();
        if (checkFirstRun()) {
            startActivity(new Intent(this, PrivateInstructionsActivity.class));
        }

        fingerprint.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (MotionEventCompat.getActionMasked(event)) {
                    case MotionEvent.ACTION_UP:
                        circularProgressView.setVisibility(View.GONE);
                        handler.removeCallbacks(runnableAction);
                        break;
                    case MotionEvent.ACTION_DOWN:
                        textViewTitle.setVisibility(View.GONE);
                        circularProgressView.setVisibility(View.VISIBLE);
                        circularProgressView.startAnimation();
                        Random r = new Random();
                        handler.postDelayed(runnableAction, (r.nextInt(3000) + 300));
                        break;
                    default:
                        break;
                }
                return false;
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
            if (mCameraView == null) {
                mCameraView = new CameraView(this, mCamera);
                camera_view = (FrameLayout) findViewById(R.id.camera_view);
                camera_view.addView(mCameraView);
            } else {
                mCameraView.setCamera(mCamera);
            }
        }
    }

    protected void setupToolbar() {
        mToolbar = (AppBar) findViewById(R.id.app_bar);
        setSupportActionBar(mToolbar);
        final ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled(false);
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
            startActivity(new Intent(this, PublicInstructionsActivity.class));
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

    public boolean checkFirstRun() {
        boolean isFirstRun = getSharedPreferences("PREFERENCE", MODE_PRIVATE).getBoolean("isFirstRun", true);
        if (isFirstRun) {
            // Place your dialog code here to display the dialog

            getSharedPreferences("PREFERENCE", MODE_PRIVATE)
                    .edit()
                    .putBoolean("isFirstRun", false)
                    .apply();
        }
        return isFirstRun;
    }
}
