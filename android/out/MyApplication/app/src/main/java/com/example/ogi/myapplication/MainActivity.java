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
    BLEManager ble;
    FileManager fileManager = new FileManager();
    private static final String TAG = "M Permission";
    private int REQUEST_CODE_LOCATE = 0x01;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // AlarmManager を開始する
        TimerServices.startAlarm(getApplicationContext());
        ble = new BLEManager(this);
        ble.init();
        //Mbaasを使用する為のAPIとキー↓
        NCMB.initialize(getApplication(), "fe8cc228956e2f26276c141ce824efb4810c9d711119dcd511e2cd8b39438913",
                "481f20a51e4ad7d6536280acb04fa83b05023e67105110b36040a221b16f1682");
        // ボタンのオブジェクトを取得
        Button save_btn = (Button) findViewById(R.id.savebutton);
        Button scan_btn = (Button) findViewById(R.id.scanbutton);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        assert progressBar != null;
        progressBar.setVisibility(View.GONE);
        // パーミッションを持っているか確認する
        if (PermissionChecker.checkSelfPermission(
                MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // パーミッションをリクエストする
            requestLocatePermission();
        }

        EditText et = (EditText) findViewById(R.id.EditText);
        assert et != null;
        et.append(fileManager.FileRead("user.txt", "user", getApplication()));
        ble.setUser(fileManager.FileRead("user.txt", "user", getApplication()));
        assert scan_btn != null;
        scan_btn.setOnTouchListener(new View.OnTouchListener() {
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

        scan_btn.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                setProgressTime(5000, progressBar);
                ble.search();
                //
            }
        });
        assert save_btn != null;
        save_btn.setOnTouchListener(new View.OnTouchListener() {
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

        // クリックイベントを受け取れるようにする
        save_btn.setOnClickListener(new OnClickListener() {
            // このメソッドはクリックされる毎に呼び出される
            public void onClick(View v) {

                // ここにクリックされたときの処理を記述
                EditText edit = (EditText) findViewById(R.id.EditText);
                assert edit != null;
                fileManager.FileWrite("user.txt", "user", edit.getText().toString(), getApplicationContext());
                ble.setUser(fileManager.FileRead("user.txt", "user", getApplicationContext()));
                Toast toast = Toast.makeText(getApplicationContext(), "ファイルに保存しました", Toast.LENGTH_SHORT);
                toast.show();
            }

        });
    }


    private void setProgressTime(int time, final ProgressBar mprogressBar) {
        Handler mHandler = new Handler();
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mprogressBar.setVisibility(View.GONE);
            }
        }, time);
        mprogressBar.setVisibility(View.VISIBLE);
    }

    private void requestLocatePermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.ACCESS_COARSE_LOCATION)) {

            Log.d(TAG, "shouldShowRequestPermissionRationale:追加説明");
            // 権限チェックした結果、持っていない場合はダイアログを出す
            new AlertDialog.Builder(this)
                    .setTitle("パーミッションの追加説明")
                    .setMessage("このアプリを使用するには位置情報が必要です")
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(MainActivity.this,
                                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                                    REQUEST_CODE_LOCATE);
                        }
                    })
                    .create()
                    .show();
            return;
        }

        // 権限を取得する
        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.ACCESS_COARSE_LOCATION
        }, REQUEST_CODE_LOCATE);
        return;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {

        if (requestCode == REQUEST_CODE_LOCATE) {
            if (grantResults.length != 1 ||
                    grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "onRequestPermissionsResult:DENYED");

                if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.ACCESS_COARSE_LOCATION)) {
                    Log.d(TAG, "[show error]");
                    new AlertDialog.Builder(this)
                            .setTitle("パーミッション取得エラー")
                            .setMessage("再試行する場合は、アプリを再起動してください")
                            .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    // サンプルのため、今回はもう一度操作をはさんでいますが
                                    // ここでrequestLocatePermissionメソッドの実行でもよい
                                }
                            })
                            .create()
                            .show();

                } else {
                    Log.d(TAG, "[show app settings guide]");
                    new AlertDialog.Builder(this)
                            .setTitle("パーミッション取得エラー")
                            .setMessage("今後は許可しないが選択されました。アプリ設定＞権限をチェックしてください（権限をON/OFFすることで状態はリセットされます）")
                            .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    //   openSettings();
                                }
                            })
                            .create()
                            .show();
                }
            } else {
                Log.d(TAG, "onRequestPermissionsResult:GRANTED");
                // 許可された
            }
        } else {
            // super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
}