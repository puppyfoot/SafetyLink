package com.example.customermobile;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import android.os.Bundle;
import android.view.View;

import com.owl93.dpb.CircularProgressView;
import com.skydoves.progressview.OnProgressChangeListener;
import com.skydoves.progressview.ProgressView;

import java.util.Random;

import www.sanju.motiontoast.MotionToast;

public class MainActivity extends AppCompatActivity {

    CircularProgressView circularProgressView;
    ProgressView progressView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);

//        circularProgressView = findViewById(R.id.circularProgressView);
//        circularProgressView.setProgress(0);
//        circularProgressView.setMaxValue(100);
//
//        progressView = findViewById(R.id.progressView);
//        progressView.setOnProgressChangeListener(new OnProgressChangeListener() {
//            @Override
//            public void onChange(float v) {
//                progressView.setLabelText("Progress: " + v + "%");
//            }
//        });
    }

    public void clickbt(View v){
//        if(v.getId() == R.id.button1){
//            circularProgressView.setProgress(circularProgressView.getProgress() + 5);
//        }else if(v.getId() == R.id.button2){
//            MotionToast.Companion.createToast(this,
//                    "Hurray success 😍",
//                    "Upload Completed successfully!",
//                    MotionToast.TOAST_SUCCESS,
//                    MotionToast.GRAVITY_BOTTOM,
//                    MotionToast.LONG_DURATION,
//                    ResourcesCompat.getFont(this,R.font.helvetica_regular));
//        }else if(v.getId() == R.id.button3){
//            Random r = new Random();
//            progressView.setProgress(r.nextInt(100));
//        }
    }
}