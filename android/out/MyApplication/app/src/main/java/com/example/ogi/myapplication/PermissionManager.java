package com.example.ogi.myapplication;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.PermissionChecker;
import android.support.v7.app.AlertDialog;
import android.util.Log;

/**
 * Created by wami on 2016/11/23.
 */

public class PermissionManager {
    Context context;
    Activity activity;
    String TAG="PermissonManager";
    private int REQUEST_CODE_LOCATE = 0x01;

    PermissionManager(Context context,Activity activity){
        this.context=context;
        this.activity= activity;
    }

    public void PermissionCheck(){
        // パーミッションを持っているか確認する
        if (PermissionChecker.checkSelfPermission(
                activity, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // パーミッションをリクエストする
            requestLocatePermission();
        }
    }

    private void requestLocatePermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(activity,
                Manifest.permission.ACCESS_COARSE_LOCATION)) {

            Log.d(TAG, "shouldShowRequestPermissionRationale:追加説明");
            // 権限チェックした結果、持っていない場合はダイアログを出す
            new AlertDialog.Builder(context)
                    .setTitle("パーミッションの追加説明")
                    .setMessage("このアプリを使用するには位置情報が必要です")
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(activity,
                                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                                    REQUEST_CODE_LOCATE);
                        }
                    })
                    .create()
                    .show();
            return;
        }

        // 権限を取得する
        ActivityCompat.requestPermissions(activity, new String[]{
                Manifest.permission.ACCESS_COARSE_LOCATION
        }, REQUEST_CODE_LOCATE);
        return;
    }

    public void RequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {

        if (requestCode == REQUEST_CODE_LOCATE) {
            if (grantResults.length != 1 ||
                    grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "onRequestPermissionsResult:DENYED");

                if (ActivityCompat.shouldShowRequestPermissionRationale(activity,
                        Manifest.permission.ACCESS_COARSE_LOCATION)) {
                    Log.d(TAG, "[show error]");
                    new AlertDialog.Builder(context)
                            .setTitle("パーミッション取得エラー")
                            .setMessage("再試行する場合は、アプリを再起動してください")
                            .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    // サンプルのため、今回はもう一度操作をはさんでいますが
                                    // ここでrequestLocatePermissionメソッドの実行でもよい
                                }
                            })
                            .create()
                            .show();

                } else {
                    Log.d(TAG, "[show app settings guide]");
                    new AlertDialog.Builder(context)
                            .setTitle("パーミッション取得エラー")
                            .setMessage("今後は許可しないが選択されました。アプリ設定＞権限をチェックしてください（権限をON/OFFすることで状態はリセットされます）")
                            .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    //   openSettings();
                                }
                            })
                            .create()
                            .show();
                }
            } else {
                Log.d(TAG, "onRequestPermissionsResult:GRANTED");
                // 許可された
            }
        } else {
            // super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
}
