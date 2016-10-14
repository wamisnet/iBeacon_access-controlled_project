package com.example.ogi.myapplication;

/**
 * Created by ogi on 2016/10/14.
 */
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class SampleBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // 他のアプリ更新時は対象外とする
        if (intent.getAction().equals(Intent.ACTION_PACKAGE_REPLACED)) {
            if (!intent.getDataString().equals(
                    "package:" + context.getPackageName())) {
                return;
            }
        }

        // AlarmManager を開始する
        SampleServices.startAlarm(context);
    }
}
