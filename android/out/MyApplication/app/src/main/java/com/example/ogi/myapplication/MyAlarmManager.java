package com.example.ogi.myapplication;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import java.util.Calendar;

/**
 * Created by ogi on 2016/06/14.
 */
public class MyAlarmManager {
    Context c;
    AlarmManager am;
    private PendingIntent mAlarmSender;
    private static final String TAG = MyAlarmManager.class.getSimpleName();

    public MyAlarmManager(Context c){
        // 初期化
        this.c = c;
        am = (AlarmManager)c.getSystemService(Context.ALARM_SERVICE);
        Log.v(TAG,"初期化完了");
    }



    public void addAlarm(int alarmHour, int alarmMinute){
        // アラームを設定する
        mAlarmSender = this.getPendingIntent();

        // アラーム時間設定
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(System.currentTimeMillis());
        // 設定した時刻をカレンダーに設定
        cal.set(Calendar.HOUR_OF_DAY, alarmHour);
        cal.set(Calendar.MINUTE, alarmMinute);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        // 過去だったら明日にする
        if(cal.getTimeInMillis() < System.currentTimeMillis()){
            cal.add(Calendar.DAY_OF_YEAR, 1);
        }
        Toast.makeText(c, String.format("%02d時%02d分にスキャンを開始します", alarmHour, alarmMinute), Toast.LENGTH_LONG).show();

        am.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), mAlarmSender);
        Log.v(TAG, cal.getTimeInMillis()+"ms");
        Log.v(TAG, "セット完了");
    }

    public void stopAlarm() {
        // アラームのキャンセル
        Log.d(TAG, "stopAlarm()");
        am.cancel(mAlarmSender);
        //spm.updateToRevival();
    }

    private PendingIntent getPendingIntent() {
        // アラーム時に起動するアプリケーションを登録
        Intent intent = new Intent(c, MyAlarmService.class);
        PendingIntent pendingIntent = PendingIntent.getService(c, PendingIntent.FLAG_ONE_SHOT, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        return pendingIntent;
    }
}
