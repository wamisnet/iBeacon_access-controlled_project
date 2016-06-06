/**
 * Created by ogi on 2016/06/06.
 */
package com.example.ogi.myapplication;


import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

public class MyService  extends Service {
    BluetoothAdapter mBluetoothAdapter;
    Handler mHandler = new Handler();
    int flag=0;
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("TAG", "onCreate");
    }




}
