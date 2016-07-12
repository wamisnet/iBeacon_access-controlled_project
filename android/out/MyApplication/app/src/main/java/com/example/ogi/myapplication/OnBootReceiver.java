package com.example.ogi.myapplication;

/**
 * Created by ogi on 2016/07/12.
 */

import android.content.Context;

/**
 * 端末起動時の処理。
 * @author id:language_and_engineering
 *
 */
public class OnBootReceiver extends BaseOnBootReceiver
{
    @Override
    protected void onDeviceBoot(Context context)
    {
        // サンプルのサービス常駐を開始
        new SamplePeriodicService().startResident(context);
    }

}