package com.example.ogi.myapplication;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

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

        MainActivity main = new MainActivity();
       // FileWrite("test.txt","ファイル作成" ,"テスト");
        Log.d("BaseOnBoot","IF外");

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
         //   FileWrite("test.txt","ファイル作成" ,"テスト");
            Log.d("BaseOnBoot","IF内");
        }

        // NOTE:
        // ・このメソッド終了時点でこのオブジェクトは消滅
        // ・このメソッドはメインスレッドで呼ばれ，10秒以上の長い処理は禁物
    }

   /* public void FileWrite(String filename, String id, String data){
        try {
            OutputStream out = openFileOutput(filename, MODE_PRIVATE);
            PrintWriter writer =
                    new PrintWriter(new OutputStreamWriter(out, "UTF-8"));
            writer.append(id+":"+data+"\n");
            writer.close();
            Log.d("FileWrite:","ID:"+id+"data:"+data);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }*/


    /**
     * 端末起動時に呼ばれるメソッド。
     * メインスレッド上ではないので，実行時間の心配はない。
     */
    protected abstract void onDeviceBoot(Context context);

}