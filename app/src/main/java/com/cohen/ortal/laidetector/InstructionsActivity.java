package com.cohen.ortal.laidetector;

import android.app.Activity;
import android.os.Bundle;

import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

/**
 * @author user
 * @date 2015-07-27
 */
public class InstructionsActivity extends Activity {

    private ImageButton continueButton;

    private TextView textView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instructions);

        continueButton = (ImageButton) findViewById(R.id.continue_button);
        continueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

}
