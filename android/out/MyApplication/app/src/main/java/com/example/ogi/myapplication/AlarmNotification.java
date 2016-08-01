package com.example.ogi.myapplication;

import android.app.Activity;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.wifi.WifiEnterpriseConfig;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.example.ogi.myapplication.obj_push;
import com.nifty.cloud.mb.core.DoneCallback;
import com.nifty.cloud.mb.core.NCMBException;
import com.nifty.cloud.mb.core.NCMBObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by ogi on 2016/06/14.
 */
public class AlarmNotification extends Service {
    private MediaPlayer mp;
    private TextView EditText1;
    private BluetoothManager mBluetoothManager;
    BluetoothAdapter mBluetoothAdapter;
    Handler mHandler = new Handler();
    Handler mHandler1 = new Handler();
    int flag=0;
    private Context context;
    MainActivity f =new MainActivity();
    obj_push  g = new obj_push();
    BluetoothManager bm;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId){
  //  public IBinder onBind(Intent intent) {
        Log.d("Oncreate", "おんばいんど");
        Toast.makeText(this, "MyService#onBind"+ ": " + intent, Toast.LENGTH_SHORT).show();
        Log.i(TAG, "onBind" + ": " + intent);
        Toast.makeText(getApplicationContext(), "アラームスタート！", Toast.LENGTH_LONG).show();
        // 音を鳴らす
        if (mp == null)
            // resのrawディレクトリにtest.mp3を置いてある
            mp = MediaPlayer.create(this, R.raw.test);


        mp.start();
        //  g.onCreate();
        //  g.ble();
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // タイムアウト
                Log.d("Oncreate", "タイムアウト");
                mBluetoothAdapter.stopLeScan(mLeScanCallback);

            }
        }, 10000);
        // スキャン開始
        mBluetoothAdapter.startLeScan(mLeScanCallback);
        flag=0;
        return 0;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("Oncreate", "おんくりえいと");
        final BluetoothManager bluetoothManager =
                (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();


        Toast.makeText(this, "アラーム！", Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopAndRelease();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void stopAndRelease() {
        if (mp != null) {
            mp.stop();
            mp.release();
        }
    }


    private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord) {
         //   Log.d("TAG", "receive!!!");
            getScanData(scanRecord);
         //   Log.d("TAG", "device address:" + device.getAddress());
        }
    };

    String TAG ="TAG";

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

             //   Log.d(TAG, "UUID:" + uuid);
                if (uuid.equals("00ffe0-00-100-800-0805f9b34fb"))
                {
                    if(flag==0) {
                        Log.d(TAG, "UUID一致したンゴｗｗｗ");
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
                                    Log.d(TAG, "失敗");
                                } else {
                                    //保存成功
                                    Log.d(TAG, "成功");
                                }
                            }
                        });
                        flag=1;
                    }
                }
              //  Log.d(TAG, "major:" + major);
              //  Log.d(TAG, "minor:" + minor);
            }
        }
    }

    public String FileRead(String filename,String id){
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

    public String removeString(String strSrc, String strRemove) {
        Pattern pattern = Pattern.compile(strRemove);
        Matcher matcher = pattern.matcher(strSrc);
        String strTmp = matcher.replaceAll("");

        return strTmp;
    }


}