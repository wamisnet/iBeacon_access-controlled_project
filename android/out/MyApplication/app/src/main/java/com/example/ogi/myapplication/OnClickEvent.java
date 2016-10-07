package com.example.ogi.myapplication;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.nifty.cloud.mb.core.DoneCallback;
import com.nifty.cloud.mb.core.FindCallback;
import com.nifty.cloud.mb.core.NCMBException;
import com.nifty.cloud.mb.core.NCMBObject;
import com.nifty.cloud.mb.core.NCMBQuery;

import java.util.List;

/**
 * Created by ogi on 2016/08/16.
 */
public class OnClickEvent implements View.OnClickListener {
    ProgressBar ProgressBar;
    MainActivity main =new MainActivity();
    BluetoothAdapter mBluetoothAdapter;
    Handler mHandler = new Handler();
    int flag=0;

    private static final OnClickEvent instance = new OnClickEvent();


    private OnClickEvent() {
    }

    public static OnClickEvent getInstance() {
        return instance;
    }

    @Override
    public void onClick(View v) {

        if (v != null) {
            switch (v.getId()) {
                case R.id.scanbutton:

                    // クリック処理
                     Log.v("OnClickEvent","分岐IN1");
                    final BluetoothManager bluetoothManager =
                            (BluetoothManager) main.getSystemService(Context.BLUETOOTH_SERVICE);
                    mBluetoothAdapter = bluetoothManager.getAdapter();
                    if (mBluetoothAdapter != null) {
                        if (!mBluetoothAdapter.isEnabled()) {
                            Toast toast = Toast.makeText(null, "Bluetooth ON!", Toast.LENGTH_LONG);
                            toast.show();
                            mBluetoothAdapter.enable();
                        }
                    }

                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            // タイムアウト
                            Log.d("Oncreate", "タイムアウト");
                            ProgressBar.setVisibility(View.GONE);
                            mBluetoothAdapter.stopLeScan(mLeScanCallback);
                        }
                    }, 10000);

                    // スキャン開始
                    mBluetoothAdapter.startLeScan(mLeScanCallback);
                    ProgressBar.setVisibility(View.VISIBLE);
                    flag = 0;
                    break;
                //    break;

/*                case R.id.piyopiyo:
                    // クリック処理
                    break;

                  case R.id.fugafuga:
                    // クリック処理
                    break;
*/
                default:
                    Log.v("OnClickEvent","分岐IN");

            }
        }
    }
    String TAG ="TAG";
    private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord) {
            //   Log.d("TAG", "receive!!!");
            getScanData(scanRecord);
            //   Log.d("TAG", "device address:" + device.getAddress());
        }
    };

    private void getScanData(byte[] scanRecord) {
        if (scanRecord.length > 30) {
            if ((scanRecord[5] == (byte) 0x4c) && (scanRecord[6] == (byte) 0x00) &&
                    (scanRecord[7] == (byte) 0x02) && (scanRecord[8] == (byte) 0x15)) {
                String uuid = Integer.toHexString(scanRecord[9] & 0xff)
                        + Integer.toHexString(scanRecord[10] & 0xff)
                        + Integer.toHexString(scanRecord[11] & 0xff)
                        + Integer.toHexString(scanRecord[12] & 0xff)
                        + "-"
                        + Integer.toHexString(scanRecord[13] & 0xff)
                        + Integer.toHexString(scanRecord[14] & 0xff)
                        + "-"
                        + Integer.toHexString(scanRecord[15] & 0xff)
                        + Integer.toHexString(scanRecord[16] & 0xff)
                        + "-"
                        + Integer.toHexString(scanRecord[17] & 0xff)
                        + Integer.toHexString(scanRecord[18] & 0xff)
                        + "-"
                        + Integer.toHexString(scanRecord[19] & 0xff)
                        + Integer.toHexString(scanRecord[20] & 0xff)
                        + Integer.toHexString(scanRecord[21] & 0xff)
                        + Integer.toHexString(scanRecord[22] & 0xff)
                        + Integer.toHexString(scanRecord[23] & 0xff)
                        + Integer.toHexString(scanRecord[24] & 0xff);
                //１0進数
                final int major = (scanRecord[25] & 0xff)*256 + (scanRecord[26] & 0xff);
                final int minor = (scanRecord[27] & 0xff)*256 + (scanRecord[28] & 0xff);

                //Log.d(TAG, "UUID:" + uuid);
                if (uuid.equals("00ffe0-00-100-800-0805f9b34fb"))
                {
                    if(flag==0) {
                        Toast toast = Toast.makeText(main.getApplicationContext(), "同一のUUIDを検知しました。", Toast.LENGTH_LONG);
                       // toast.show();
                        //サーバへ送信
                        final NCMBObject obj = new NCMBObject("TestClass");
                        obj.put("attend",main.FileRead("user.txt","user"));
                        try {
                            obj.increment("incrementKey",1);
                        } catch (NCMBException e) {
                            e.printStackTrace();
                        }

                        NCMBQuery<NCMBObject> query = new NCMBQuery<>("TestClass");
                        query.whereEqualTo("attend",main.FileRead("user.txt","user"));
                        query.findInBackground(new FindCallback<NCMBObject>() {
                            @Override
                            public void done(List<NCMBObject> objects, NCMBException e) {
                                if (e != null) {
                                    //エラー時の処理
                                    Log.e("NCMB", "検索に失敗しました。エラー:" + e.getMessage());
                                } else {
                                    //成功時の処理
                                    //  Log.d("TAG", String.valueOf(objects.size()));
                                    Log.i("NCMB", "検索に成功しました。");
                                    if(objects.size()==0) {
                                        final NCMBObject obj = new NCMBObject("TestClass");
                                        obj.put("attend",main.FileRead("user.txt","user"));
                                        obj.put("major", major);
                                        obj.put("minor", minor);
                                        obj.saveInBackground(new DoneCallback() {
                                            @Override
                                            public void done(NCMBException e) {
                                                if (e != null) {
                                                    //保存失敗
                                                } else {
                                                    //保存成功
                                                }
                                            }
                                        });
                                    }
                                }
                                // ループカウンタ
                                String oldname = null;
                                for (int i = 0, n = objects.size(); i < n; i++) {
                                    NCMBObject o = objects.get(i);

                                    Log.i("NCMB", o.getString("attend"));
                                    Log.i("NCMB", o.getString("major"));

                                    o.put("major",major);
                                    o.put("minor",minor);
                                    o.saveInBackground(new DoneCallback() {
                                        @Override
                                        public void done(NCMBException e) {
                                            if (e != null) {
                                                //保存失敗
                                            } else {
                                                //保存成功
                                            }
                                        }
                                    });
                                    // 処理
                                    String name = o.getString("attend");
                                    String timer = o.getString("createDate");
                                    Integer score = o.getInt("major");
                                    if(!name.equals(oldname)){
                                        oldname=name;
                                    }


                                }

                            }

                        });

                        obj.put("major", major);
                        obj.put("minor", minor);

                   /*     obj.saveInBackground(new DoneCallback() {
                            @Override
                            public void done(NCMBException e) {
                                if (e != null) {
                                    //保存失敗
                                } else {
                                    //保存成功
                                }
                            }
                        });*/
                        flag=1;
                    }
                }

                // Log.d(TAG, "major:" + major);
                // Log.d(TAG, "minor:" + minor);
            }
        }
    }

}
