package com.example.ogi.myapplication;

/**
 * Created by ogi on 2016/07/12.
 */
import android.app.AlarmManager;
import android.content.Context;
import android.util.Log;
import android.widget.EditText;

import java.util.Calendar;

/**
 * 常駐型サービスのサンプル。定期的にログ出力する。
 * @author id:language_and_engineering
 *
 */
public class SamplePeriodicService extends BasePeriodicService
{
    // 画面から常駐を解除したい場合のために，常駐インスタンスを保持
    public static BasePeriodicService activeService;


    @Override
    protected long getIntervalMS() {
        return 1000 * 10;
    }


    @Override
    protected void execTask() {
        activeService = this;

        // ※もし毎回の処理が重い場合は，メインスレッドを妨害しないために
        // ここから下を別スレッドで実行する。



        // ログ出力（ここに定期実行したい処理を書く）
        Log.d("hoge", "fuga");


        // 次回の実行について計画を立てる
        makeNextPlan();
    }


    @Override
    public void makeNextPlan()
    {
        this.scheduleNextTime();
    }


    /**
     * もし起動していたら，常駐を解除する
     */
    public static void stopResidentIfActive(Context context) {
        if( activeService != null )
        {
            activeService.stopResident(context);
        }
    }

}