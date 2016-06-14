package com.example.ogi.myapplication;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.nifty.cloud.mb.core.DoneCallback;
import com.nifty.cloud.mb.core.NCMB;
import com.nifty.cloud.mb.core.NCMBException;
import com.nifty.cloud.mb.core.NCMBObject;
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
    BluetoothAdapter mBluetoothAdapter;
    Handler mHandler = new Handler();
    int flag=0;
    ProgressBar progressBar;

    @Override
    //Create
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Mbaasを使用する為のAPIとキー↓
        NCMB.initialize(this.getApplicationContext(),"8eee2292f5c87bae5ec5bbb3bdb95ee997708bd1bc96aed8f8ed6f142ce71e61",
                "29aea4c9781e3664e4f9c959c2e04074ab3f765c74586ed447947699a5385970");
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        assert progressBar != null;
        progressBar.setVisibility(View.GONE);
        // ボタンのオブジェクトを取得
        Button save_btn = (Button) findViewById(R.id.savebutton);
        Button scan_btn = (Button) findViewById(R.id.scanbutton);
        //ブルートゥースサービスを開始させる
        final BluetoothManager bluetoothManager =
                (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();
        if(mBluetoothAdapter!= null) {
            Log.i("TAG", "BL1");
            if (!mBluetoothAdapter.isEnabled()) {
                Log.i("TAG", "BL2");
                Toast toast = Toast.makeText(this, "Bluetooth ON!", Toast.LENGTH_LONG);
                toast.show();
                mBluetoothAdapter.enable();
            }
        }
        EditText et = (EditText) findViewById(R.id.EditText);
        assert et != null;
        et.append(FileRead("user.txt","user"));

        assert scan_btn != null;
        scan_btn.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        // タイムアウト
                        Log.d("Oncreate", "タイムアウト");
                        progressBar.setVisibility(View.GONE);
                        mBluetoothAdapter.stopLeScan(mLeScanCallback);
                    }
                }, 10000);


                // スキャン開始
                mBluetoothAdapter.startLeScan(mLeScanCallback);
                progressBar.setVisibility(View.VISIBLE);
                flag=0;
            }
        });
        // クリックイベントを受け取れるようにする
        assert save_btn != null;
        save_btn.setOnClickListener(new OnClickListener() {
            // このメソッドはクリックされる毎に呼び出される
            public void onClick(View v) {
                //スタートサービス
                Context w = null;
                MyAlarmManager a = new MyAlarmManager(getApplicationContext());
                a.addAlarm(12,02);
                // ここにクリックされたときの処理を記述
                EditText edit = (EditText) findViewById(R.id.EditText);
                assert edit != null;
                FileWrite("user.txt","user" ,edit.getText().toString());
            }
        });
    }
    String TAG ="TAG";
    private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord) {
            Log.d("TAG", "receive!!!");
            getScanData(scanRecord);
            Log.d("TAG", "device address:" + device.getAddress());
        }
    };

    private void getScanData(byte[] scanRecord) {
        if (scanRecord.length > 30) {
            if ((scanRecord[5] == (byte) 0x4c) && (scanRecord[6] == (byte) 0x00) &&
                    (scanRecord[7] == (byte) 0x02) && (scanRecord[8] == (byte) 0x15)) {
                String uuid = Integer.toHexString(scanRecord[9] & 0xff)
                        + Integer.toHexString(scanRecord[10] & 0xff)
                        + Integer.toHexString(scanRecord[11] & 0xff)
                        + Integer.toHexString(scanRecord[12] & 0xff)
                        + "-"
                        + Integer.toHexString(scanRecord[13] & 0xff)
                        + Integer.toHexString(scanRecord[14] & 0xff)
                        + "-"
                        + Integer.toHexString(scanRecord[15] & 0xff)
                        + Integer.toHexString(scanRecord[16] & 0xff)
                        + "-"
                        + Integer.toHexString(scanRecord[17] & 0xff)
                        + Integer.toHexString(scanRecord[18] & 0xff)
                        + "-"
                        + Integer.toHexString(scanRecord[19] & 0xff)
                        + Integer.toHexString(scanRecord[20] & 0xff)
                        + Integer.toHexString(scanRecord[21] & 0xff)
                        + Integer.toHexString(scanRecord[22] & 0xff)
                        + Integer.toHexString(scanRecord[23] & 0xff)
                        + Integer.toHexString(scanRecord[24] & 0xff);
                //１0進数
                int major = (scanRecord[25] & 0xff)*256 + (scanRecord[26] & 0xff);
                int minor = (scanRecord[27] & 0xff)*256 + (scanRecord[28] & 0xff);

                Log.d(TAG, "UUID:" + uuid);
                if (uuid.equals("00ffe0-00-100-800-0805f9b34fb"))
                {
                    if(flag==0) {
                        Toast toast = Toast.makeText(this, "同一のUUIDを検知しました。", Toast.LENGTH_LONG);
                        toast.show();
                        //サーバへ送信
                        NCMBObject obj = new NCMBObject("TestClass");
                        obj.put("attend",FileRead("user.txt","user"));
                        obj.put("major", major);
                        obj.put("minor", minor);

                        obj.saveInBackground(new DoneCallback() {
                            @Override
                            public void done(NCMBException e) {
                                if (e != null) {
                                    //保存失敗
                                } else {
                                    //保存成功
                                }
                            }
                        });
                    flag=1;
                    }
                }
                Log.d(TAG, "major:" + major);
                Log.d(TAG, "minor:" + minor);
            }
        }
    }

    private void FileWrite(String filename,String id,String data){
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
    private String FileRead(String filename,String id){
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
//送信
//認証

