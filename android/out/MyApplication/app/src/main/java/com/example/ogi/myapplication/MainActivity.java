package com.example.ogi.myapplication;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.os.Bundle;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
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
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // AlarmManager を開始する
        TimerServices.startAlarm(getApplicationContext());
        ble=new BLEManager(this);
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


        EditText et = (EditText) findViewById(R.id.EditText);
        assert et != null;
        et.append(FileRead("user.txt", "user"));
        ble.setUser(FileRead("user.txt", "user"));
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

    //    assert scan_btn != null;
        scan_btn.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                ble.search();
                progressBar.setVisibility(View.GONE);
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
                FileWrite("user.txt", "user", edit.getText().toString());
                ble.setUser(FileRead("user.txt", "user"));
                Toast toast = Toast.makeText(getApplicationContext(), "ファイルに保存しました", Toast.LENGTH_SHORT);
                toast.show();
            }

        });

    }



   public void FileWrite(String filename, String id, String data){
        try {
            OutputStream out = openFileOutput(filename, MODE_PRIVATE);
            PrintWriter writer =
                    new PrintWriter(new OutputStreamWriter(out, "UTF-8"));
            writer.append(id+":"+data+"\n");
            writer.close();
            Log.d("FileWrite:","ID:"+id+"data:"+data);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String removeString(String strSrc, String strRemove) {
        Pattern pattern = Pattern.compile(strRemove);
        Matcher matcher = pattern.matcher(strSrc);
        String strTmp = matcher.replaceAll("");

        return strTmp;
    }

    public String FileRead(String filename, String id){
        try {Log.v("fileread","テスト1");
            InputStream in = openFileInput(filename);
            BufferedReader reader =
                    new BufferedReader(new InputStreamReader(in, "UTF-8"));
            String search;
            while ((search = reader.readLine()) != null) {
                if(search.startsWith(id)){
                    Log.v("fileread",search);
                    reader.close();
                    return removeString(search,id+":");
                }
                Log.v("fileread","テスト２");
            }
            reader.close();
            return "";
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }
}

