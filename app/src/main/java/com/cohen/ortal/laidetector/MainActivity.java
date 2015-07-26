package com.cohen.ortal.laidetector;

import android.app.Activity;

import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;


public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ImageButton fingerprint = (ImageButton) findViewById(R.id.fingerprint);

        fingerprint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }

    public void onPreviewFrame2(byte[] data, Camera arg1) {
        FileOutputStream outStream = null;
        try {
            YuvImage yuvimage = new YuvImage(data,ImageFormat.NV21,arg1.getParameters().getPreviewSize().width,arg1.getParameters().getPreviewSize().height,null);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            yuvimage.compressToJpeg(new Rect(0,0,arg1.getParameters().getPreviewSize().width,arg1.getParameters().getPreviewSize().height), 80, baos);

            outStream = new FileOutputStream(String.format("/sdcard/%d.jpg", System.currentTimeMillis()));
            outStream.write(baos.toByteArray());
            outStream.close();

            Log.d("", "onPreviewFrame - wrote bytes: " + data.length);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
        }
//        Preview.this.invalidate();
    }
    public void onPreviewFrame(byte[] data, Camera camera)
    {
        int PreviewSizeWidth = 100;
        int PreviewSizeHeight =100;
        String NowPictureFileName = "ddd";
        Camera.Parameters parameters = camera.getParameters();
        int imageFormat = parameters.getPreviewFormat();
        if (imageFormat == ImageFormat.NV21)
        {
            Rect rect = new Rect(0, 0, PreviewSizeWidth, PreviewSizeHeight);
            YuvImage img = new YuvImage(data, ImageFormat.NV21, PreviewSizeWidth, PreviewSizeHeight, null);
            OutputStream outStream = null;
            File file = new File(NowPictureFileName);
            try
            {
                outStream = new FileOutputStream(file);
                img.compressToJpeg(rect, 100, outStream);
                outStream.flush();
                outStream.close();
            }
            catch (FileNotFoundException e)
            {
                e.printStackTrace();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
