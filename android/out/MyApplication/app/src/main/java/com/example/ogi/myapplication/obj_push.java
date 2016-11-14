package com.example.ogi.myapplication;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.os.Handler;
import android.util.Log;

import com.nifty.cloud.mb.core.DoneCallback;
import com.nifty.cloud.mb.core.NCMBException;
import com.nifty.cloud.mb.core.NCMBObject;

/**
 * Created by ogi on 2016/06/21.
 */
public class obj_push {
    BluetoothAdapter mBluetoothAdapter;
    Handler mHandler = new Handler();
    int flag=0;
    private Context context;
    MainActivity f =new MainActivity();
    /*public obj_push() {
        this.context = context;
    }*/
    public void onCreate(){
        Log.i(TAG, "onCreate");
    };

    public void ble(){
        BluetoothManager bluetoothManager =
                (BluetoothManager) f.getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // タイムアウト
                Log.d("Oncreate", "タイムアウト");
                mBluetoothAdapter.stopLeScan(mLeScanCallback);
            }
        }, 10000);
        // スキャン開始
        mBluetoothAdapter.startLeScan(mLeScanCallback);
    }

    private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord) {
            Log.d("TAG", "receive!!!");
            getScanData(scanRecord);
            Log.d("TAG", "device address:" + device.getAddress());
        }
    };

    String TAG ="TAG";

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
                int major = (scanRecord[25] & 0xff)*256 + (scanRecord[26] & 0xff);
                int minor = (scanRecord[27] & 0xff)*256 + (scanRecord[28] & 0xff);

                Log.d(TAG, "UUID:" + uuid);
                if (uuid.equals("00ffe0-00-100-800-0805f9b34fb"))
                {
                    if(flag==0) {
                        Log.d(TAG, "UUID一致したンゴｗｗｗ");
                        //サーバへ送信
                        NCMBObject obj = new NCMBObject("TestClass");
                        obj.put("attend",f.FileRead("user.txt","user"));
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
                        flag=1;
                    }
                }
                Log.d(TAG, "major:" + major);
                Log.d(TAG, "minor:" + minor);
            }
        }
    }

}