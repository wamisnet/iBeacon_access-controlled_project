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

public class TimerServices extends IntentService {

    int StartHour[] = {9,11,13,15,0};
    int StartTime[] = {21,01,21,01,00};
    private static int dStartHour[] = {9,9,9,9,0};//デバッグ用
    private static int dStartTime[] = {8,10,13,15,00};

   // private static int dStartHour[] = {10,10,10,10,0};//デバッグ用
   // private static int dStartTime[] = {30,45,50,55,00};

    //サービスを処理する AlarmManager を開始する。
    public static void startAlarm(Context context) {
        int adtimerhour=0;
        int adtimertime=0;
        int StartHour;
        int StartTime;
        int x;
        // 実行するサービスを指定する
        PendingIntent pendingIntent = PendingIntent.getService(context, 0,
                new Intent(context, TimerServices.class),
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
                }
                //Debug
                if (m >= 59) {
                    adtimerhour = dStartHour[++x];
                    adtimertime = 1;
                }

        //Month指定は0から始まる　(ex:1月→0 2月→1
        now.set(y,M,d,adtimerhour,adtimertime,55);
        if(x == 4)
        {
            Log.d("カレンダー前", String.valueOf(d));
            now.add(now.DAY_OF_MONTH, 1);
            Log.d("カレンダー後", String.valueOf(now.get(now.DATE)));
            now.set(y,M,now.get(now.DATE),adtimerhour,adtimertime,55);
        }
        Log.d("設定前-adtimerhour", String.valueOf(adtimerhour));
        Log.d("設定前-adtimertime", String.valueOf(adtimertime));
        Log.d("端末時間", String.valueOf(m));
        am.set(AlarmManager.RTC_WAKEUP, now.getTimeInMillis(), pendingIntent);
        Log.d("タイマーテスト","time" +now.getTimeInMillis());
    }

    //時間になると呼び出される
    BLEManager bleManager;

    @Override
    protected void onHandleIntent(Intent intent) {

        Toast.makeText(getApplicationContext(), "検出開始", Toast.LENGTH_LONG).show();
        bleManager=new BLEManager(getApplicationContext());
        bleManager.init();
        bleManager.search();

        //スキャン後タイマー設定
        startAlarm(getApplicationContext());
    }

    public TimerServices() {
        super("TimerService");
    }

}