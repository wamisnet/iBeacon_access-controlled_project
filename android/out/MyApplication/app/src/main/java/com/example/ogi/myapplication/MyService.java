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

import com.nifty.cloud.mb.core.NCMB;

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
        NCMB.initialize(this.getApplicationContext(),"8eee2292f5c87bae5ec5bbb3bdb95ee997708bd1bc96aed8f8ed6f142ce71e61",
                "29aea4c9781e3664e4f9c959c2e04074ab3f765c74586ed447947699a5385970");
    }




}
