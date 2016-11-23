package com.example.ogi.myapplication;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.PermissionChecker;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.nifty.cloud.mb.core.NCMB;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {
    ProgressBar progressBar;
    private int temporaryColorInt;
    BLEManager bleManager = new BLEManager(this);
    FileManager fileManager = new FileManager();
    PermissionManager permissionManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setProgressTime(0);
        // AlarmManager を開始する
        TimerServices.startAlarm(getApplicationContext());
        permissionManager = new PermissionManager(getApplicationContext(),this);
        bleManager.init();
        //Mbaasを使用する為のAPIとキー↓
        NCMB.initialize(getApplication(), "fe8cc228956e2f26276c141ce824efb4810c9d711119dcd511e2cd8b39438913",
                "481f20a51e4ad7d6536280acb04fa83b05023e67105110b36040a221b16f1682");
        // ボタンのオブジェクトを取得
        Button save_btn = (Button) findViewById(R.id.savebutton);
        Button scan_btn = (Button) findViewById(R.id.scanbutton);
        permissionManager.PermissionCheck();

        EditText et = (EditText) findViewById(R.id.EditText);
        assert et != null;
        et.append(fileManager.FileRead("user.txt", "user", getApplication()));
        bleManager.setUser(fileManager.FileRead("user.txt", "user", getApplication()));
        assert scan_btn != null;
        pushSw(scan_btn);

        scan_btn.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                setProgressTime(2000);
                bleManager.search();
            }
        });
        pushSw(save_btn);
        assert save_btn != null;


        // クリックイベントを受け取れるようにする
        save_btn.setOnClickListener(new OnClickListener() {
            // このメソッドはクリックされる毎に呼び出される
            public void onClick(View v) {

                // ここにクリックされたときの処理を記述
                EditText edit = (EditText) findViewById(R.id.EditText);
                assert edit != null;
                fileManager.FileWrite("user.txt", "user", edit.getText().toString(), getApplicationContext());
                bleManager.setUser(fileManager.FileRead("user.txt", "user", getApplicationContext()));
                Toast toast = Toast.makeText(getApplicationContext(), "ファイルに保存しました", Toast.LENGTH_SHORT);
                toast.show();
            }

        });
    }


    private void setProgressTime(int time) {
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        assert progressBar != null;
        progressBar.setVisibility(View.GONE);
        Handler mHandler = new Handler();
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                progressBar.setVisibility(View.GONE);
            }
        }, time);
        progressBar.setVisibility(View.VISIBLE);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        permissionManager.RequestPermissionsResult(requestCode,permissions,grantResults);
    }
    private void pushSw(Button button){
        button.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        ColorDrawable colorDrawable = (ColorDrawable) v.getBackground();
                        temporaryColorInt = colorDrawable.getColor();
                        float[] hsv = new float[3];
                        Color.colorToHSV(colorDrawable.getColor(), hsv);
                        hsv[2] -= 0.2f;
                        v.setBackgroundColor(Color.HSVToColor(hsv));
                        break;
                    case MotionEvent.ACTION_UP:
                        v.setBackgroundColor(temporaryColorInt);
                        break;
                }
                return false;
            }
        });
    }
}