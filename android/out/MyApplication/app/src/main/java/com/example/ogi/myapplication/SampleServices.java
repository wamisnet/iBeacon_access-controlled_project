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

public class SampleServices extends IntentService {

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
        // 実行するサービスを指定する
        PendingIntent pendingIntent = PendingIntent.getService(context, 0,
                new Intent(context, SampleServices.class),
                PendingIntent.FLAG_UPDATE_CURRENT);

        // 10秒毎にサービスの処理を実行する
        AlarmManager am = (AlarmManager) context
                .getSystemService(Context.ALARM_SERVICE);
        am.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                SystemClock.elapsedRealtime(), 10 * 1000, pendingIntent);
    }
}