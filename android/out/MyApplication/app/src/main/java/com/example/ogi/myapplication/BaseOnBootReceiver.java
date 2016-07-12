package com.example.ogi.myapplication;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * 端末起動時にサービスを自動開始させるためのクラス。
 * @author id:language_and_engineering
 *
 */
public abstract class BaseOnBootReceiver extends BroadcastReceiver
{

    // ブロードキャストインテント検知時
    @Override
    public void onReceive(final Context context, Intent intent) {

        // 端末起動時？
        if( Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction()))
        {
            new Thread(new Runnable(){
                @Override
                public void run()
                {
                    onDeviceBoot(context);
                }
            }).start();

        }

        // NOTE:
        // ・このメソッド終了時点でこのオブジェクトは消滅
        // ・このメソッドはメインスレッドで呼ばれ，10秒以上の長い処理は禁物
    }


    /**
     * 端末起動時に呼ばれるメソッド。
     * メインスレッド上ではないので，実行時間の心配はない。
     */
    protected abstract void onDeviceBoot(Context context);

}