package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.os.Bundle;
import android.widget.TextView;

import java.util.ArrayList;

public class DisplayActivity extends AppCompatActivity {
    private ImageView imageView;
    private TextView buttonText;
    private ArrayList<String> names;
    private ArrayList<Integer> images;
    private int currentIndex =0;
    private int displayTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display);

        imageView = (ImageView)findViewById(R.id.imageView);
        buttonText = (TextView)findViewById(R.id.buttonText);
        images = getIntent().getIntegerArrayListExtra("images");
        names = getIntent().getStringArrayListExtra("names");

        if (images == null || images.isEmpty()) {
            // Handle the case where images are not properly passed
            Log Log = null;
            Log.e("DisplayActivity", "Images array is null or empty");
            return;
        }
        
        boolean showImagesIf = getIntent().getBooleanExtra("showImagesIf",false);

        displayTime = getIntent().getIntExtra("DisplayTime", 1);
        closeDisplayActivity(displayTime, showImagesIf);
    }

    private void closeDisplayActivity(int displayTime, boolean showImagesIf) {
        if (images != null && currentIndex < names.size()) {
            buttonText.setText(names.get(currentIndex));

            //check if the state of the show keys
            Integer imageResource = images.get(currentIndex);
            if (imageResource != null && showImagesIf) {
                imageView.setImageResource(imageResource.intValue());
                imageView.setVisibility(View.VISIBLE);
            }else{
                imageView.setVisibility(View.GONE);
            }


            currentIndex++;

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    closeDisplayActivity(displayTime, showImagesIf);//calling the method itself again
                }
            }, displayTime * 1000);
        } else {
            finish();
        }
    }
}