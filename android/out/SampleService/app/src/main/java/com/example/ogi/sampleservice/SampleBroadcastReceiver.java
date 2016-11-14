package com.example.ogi.sampleservice;

/**
 * Created by ogi on 2016/10/07.
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
        SampleService.startAlarm(context);
    }
}
