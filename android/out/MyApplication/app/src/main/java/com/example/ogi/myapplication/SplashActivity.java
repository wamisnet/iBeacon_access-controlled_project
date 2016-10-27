package com.example.ogi.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

/**
 * Created by ogi on 2016/07/05.
 */
 public class SplashActivity extends Activity {

        Handler mHandler = new Handler();

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            // スプラッシュ用のビューを取得する
            setContentView(R.layout.splash);

            // 2秒したらMainActivityを呼び出してSplashActivityを終了する
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    // MainActivityを呼び出す
                    Intent intent = new Intent(getApplicationContext(),
                            MainActivity.class);
                    startActivity(intent);
                    // SplashActivityを終了する
                    SplashActivity.this.finish();
                }
            }, 2 * 1000); // 2000ミリ秒後（2秒後）に実行
        }
    }

