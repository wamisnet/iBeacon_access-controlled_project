package com.example.ogi.myapplication;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.nifty.cloud.mb.core.DoneCallback;
import com.nifty.cloud.mb.core.NCMBException;
import com.nifty.cloud.mb.core.NCMBObject;

/**
 * Created by wami on 2016/10/27.
 */

public class BLEManager {
    private Context context;
    public BLEManager(Context context){
        this.context = context;
    }

    private BluetoothAdapter mBluetoothAdapter;
    private boolean bleflg;//bleが起動していたかどうかFlgで管理
    private Handler mHandler = new Handler();
    private int flag=0;
    public void search(){
        bleEnable();
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // タイムアウト
                Log.d("BLEManager Search()", "タイムアウト");
                mBluetoothAdapter.stopLeScan(mLeScanCallback);
                bleDisable();
            }
        }, 10000);

        // スキャン開始
        mBluetoothAdapter.startLeScan(mLeScanCallback);
        flag = 0;
    }

    public void init(){
        BluetoothManager bluetoothManager=(BluetoothManager)context.getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();
        if (mBluetoothAdapter != null) {
            bleflg=false;
            if (!(bleflg=mBluetoothAdapter.isEnabled())) {
                mBluetoothAdapter.enable();
            }
        }
    }

    private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord) {
            getScanData(scanRecord);
        }
    };

    public void getScanData(byte[] scanRecord) {
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
                final int major = (scanRecord[25] & 0xff) * 256 + (scanRecord[26] & 0xff);
                final int minor = (scanRecord[27] & 0xff) * 256 + (scanRecord[28] & 0xff);

                if (uuid.equals("00ffe0-00-100-800-0805f9b34fb")) {
                    if (flag == 0) {
                        Log.d("BLEManagerget.ScanData()", "同一のUUIDを検知しました");
                        Toast toast = Toast.makeText(context, "信号を受信しました。" , Toast.LENGTH_SHORT);
                        toast.show();
                        //サーバへ送信
                        FileManager fileManager = new FileManager(context);
                        final NCMBObject obj = new NCMBObject("AttendClass");
                        obj.put("attend",fileManager.FileRead("number"));
                        obj.put("Gakkyu_ID",fileManager.FileRead("classID"));
                        try {
                            obj.increment("incrementKey", 1);
                        } catch (NCMBException e) {
                            e.printStackTrace();
                        }
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
                        flag = 1;
                    }
                }
            }
        }
    }

    //BLEを有効にする
    private void bleEnable(){
        if (!mBluetoothAdapter.isEnabled()) {
            Log.d("BLEManager bleEnable()", "enable");
            mBluetoothAdapter.enable();
        }
    }

    //BLEを無効にする。初回にBLE起動していればOFFにしない。
    private void bleDisable(){
        if (bleflg!=mBluetoothAdapter.isEnabled()) {
            Log.d("BLEManager bleDisable()", "Disable");
            mBluetoothAdapter.disable();
        }
    }
}
