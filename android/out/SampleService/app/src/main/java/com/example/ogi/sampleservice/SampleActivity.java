package com.example.ogi.sampleservice;

import android.os.Bundle;


import android.app.Activity;
import android.os.Bundle;

public class SampleActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sample);

        // AlarmManager を開始する
        SampleService.startAlarm(getApplicationContext());
        }
}




