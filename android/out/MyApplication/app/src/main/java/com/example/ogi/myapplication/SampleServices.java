package com.example.ogi.myapplication;

/**
 * Created by ogi on 2016/10/07.
 */
import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;
import com.nifty.cloud.mb.core.DoneCallback;
import com.nifty.cloud.mb.core.NCMBException;
import com.nifty.cloud.mb.core.NCMBObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SampleServices extends IntentService {

    int StartHour[] = {9,10,13,14,0};
    int StartTime[] = {15,55,15,55,00};
    private static int dStartHour[] = {10,10,11,11,0};//デバッグ用
    private static int dStartTime[] = {12,30,20,25,00};
    BluetoothAdapter mBluetoothAdapter;
    Handler mHandler = new Handler();
    int flag = 0;
    private static final String TAG = "SampleService";

    public SampleServices() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        final BluetoothManager bluetoothManager =
                (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();
        Toast.makeText(getApplicationContext(), "検出開始", Toast.LENGTH_LONG).show();
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // タイムアウト
                Log.d("onHandleIntent", "タイムアウト");
                mBluetoothAdapter.stopLeScan(mLeScanCallback);
            }
        }, 5000);
        // スキャン開始
        mBluetoothAdapter.startLeScan(mLeScanCallback);
        flag=0;
        //スキャン後タイマー設定
        startAlarm(getApplicationContext());
    }

    /**
     * サービスを処理する AlarmManager を開始する。
     *
     * @param context
     */
    public static void startAlarm(Context context) {
        int adtimerhour=0;
        int adtimertime=0;
        int StartHour;
        int StartTime;
        int x;
        // 実行するサービスを指定する
        PendingIntent pendingIntent = PendingIntent.getService(context, 0,
                new Intent(context, SampleServices.class),
                PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager am = (AlarmManager) context
                .getSystemService(Context.ALARM_SERVICE);

        Calendar now = Calendar.getInstance(); //インスタンス化
        now.setTimeInMillis(System.currentTimeMillis());
        int y = now.get(now.YEAR);       //年を取得
        int M = now.get(now.MONTH);      //月を取得
        int d = now.get(now.DATE);       //日を取得
        int h = now.get(now.HOUR_OF_DAY);//時を取得
        int m = now.get(now.MINUTE);     //分を取得
        int s = now.get(now.SECOND);      //秒を取得
                for(x=0 ; x<5 ; x++)//現在時刻と比較し時刻を決める
                {
                    if(h <= dStartHour[x]) {
                        adtimerhour = dStartHour[x];
                        adtimertime = dStartTime[x];
                        if(m > dStartTime[x] & h == dStartHour[x] ) {
                            continue;}
                            break;}
                        if (x==4) {
                            adtimerhour = dStartHour[0];
                            adtimertime = dStartTime[0];
                            break;}
                    Log.d("IF外", String.valueOf(adtimerhour));}
                //Debug
                if (m >= 59) {
                    adtimerhour = dStartHour[++x];
                    adtimertime = 1;
                }

    //Month指定は0から始まる　(ex:1月→0 2月→1
        now.set(y,M,d,adtimerhour,adtimertime,s);
        Log.d("設定前-adtimerhour", String.valueOf(adtimerhour));
        Log.d("設定前-adtimertime", String.valueOf(adtimertime));
        Log.d("端末時間", String.valueOf(m));
        am.set(AlarmManager.RTC_WAKEUP, now.getTimeInMillis(), pendingIntent);
        Log.d("タイマーテスト","time" +now.getTimeInMillis());
    }

    private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord) {
            getScanData(scanRecord);
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
                final int major = (scanRecord[25] & 0xff)*256 + (scanRecord[26] & 0xff);
                final int minor = (scanRecord[27] & 0xff)*256 + (scanRecord[28] & 0xff);

                if (uuid.equals("00ffe0-00-100-800-0805f9b34fb"))
                {
                    if(flag==0) {
                        Toast toast = Toast.makeText(this, "同一のUUIDを検知しました。", Toast.LENGTH_LONG);
                        toast.show();
                        //サーバへ送信
                        final NCMBObject obj = new NCMBObject("TestClass");
                        obj.put("attend",FileRead("user.txt","user"));
                        try {
                            obj.increment("incrementKey",1);
                        } catch (NCMBException e) {
                            e.printStackTrace();
                        }
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