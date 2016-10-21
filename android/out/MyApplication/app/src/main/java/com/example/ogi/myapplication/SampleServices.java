package com.example.ogi.myapplication;

/**
 * Created by ogi on 2016/10/07.
 */
import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.util.Log;
import java.util.Calendar;

public class SampleServices extends IntentService {

    int StartHour[] = {9,10,13,14,0};
    int StartTime[] = {15,55,15,55,00};
    private static int dStartHour[] = {10,11,12,13,0};//デバッグ用
    private static int dStartTime[] = {3,4,5,8,00};

    private static final String TAG = "SampleService";

    public SampleServices() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d("時間テスト", "time:" + SystemClock.elapsedRealtime());
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

        // 10秒毎にサービスの処理を実行する
        AlarmManager am = (AlarmManager) context
                .getSystemService(Context.ALARM_SERVICE);
        /*am.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                SystemClock.elapsedRealtime(), 10 * 1000, pendingIntent);*/

        Calendar now = Calendar.getInstance(); //インスタンス化
        now.setTimeInMillis(System.currentTimeMillis());
        int h = now.get(now.HOUR_OF_DAY);//時を取得
        int m = now.get(now.MINUTE);     //分を取得
        int s = now.get(now.SECOND);      //秒を取得
      //  Context w = null;
                for(x=0 ; x<5 ; x++)//purpose
                {
                    if(h <= dStartHour[x]) {
                        adtimerhour = dStartHour[x];
                        adtimertime = dStartTime[x];
                        if(m > dStartTime[x]) {
                            x++;
                            adtimerhour = dStartHour[x];
                            adtimertime = dStartTime[x];
                        }
                        Log.d("ループ1-adtimerhour", String.valueOf(adtimerhour));
                        Log.d("ループ1-adtimertime", String.valueOf(adtimertime));
                        Log.d("ループ1-dstarthour", String.valueOf(dStartHour[x]));
                        Log.d("ループ1-dstarttime", String.valueOf(dStartTime[x]));


                        break;
                    }
                        if (x==4) {
                            adtimerhour = dStartHour[0];
                            adtimertime = dStartTime[0];
                            Log.d("ループ2-adtimerhour", String.valueOf(adtimerhour));
                            Log.d("ループ2-dstarthour", String.valueOf(dStartHour[x]));
                            Log.d("ループ2-adtimertime", String.valueOf(adtimertime));
                            Log.d("ループ2-dstarttime", String.valueOf(dStartTime[x]));
                            break;
                        }
                    Log.d("IF外", String.valueOf(adtimerhour));
                }

                //Debug
                if (m >= 59) {
                    adtimerhour = dStartHour[++x];
                    adtimertime = 1;
                }

    //Month指定は0から始まる　(ex:1月→0 2月→1
        now.set(2016,9,21,adtimerhour,adtimertime,s);
        Log.d("設定前-adtimerhour", String.valueOf(adtimerhour));
        Log.d("設定前-adtimertime", String.valueOf(adtimertime));
        am.set(AlarmManager.RTC_WAKEUP, now.getTimeInMillis(), pendingIntent);
        Log.d("タイマーテスト","time" +now.getTimeInMillis());
    }
  //  public void set
}