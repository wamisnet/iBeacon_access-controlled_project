package ibeacon.net.in;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.AdvertiseData;
import android.bluetooth.le.AdvertiseSettings;
import android.bluetooth.le.BluetoothLeAdvertiser;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.UUID;


public class MainActivity extends Activity {
    BluetoothLeAdvertiser mBluetoothLeAdvertiser;
    IBeaconAdvertiseCallback mAdvertiseCallback;
    private final String TAG = "mainActivity";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment1);
        // Bluetoothの生成（ここは5.0以前と一緒）
        BluetoothManager manager=(BluetoothManager)getSystemService(Context.BLUETOOTH_SERVICE);
        final BluetoothAdapter bluetoothAdapter = manager.getAdapter();

        // Bluetoothサポートチェック
        final boolean isBluetoothSupported = bluetoothAdapter != null;
        if (!isBluetoothSupported) {
            Log.d(TAG," errorMessage 非サポート時の処理をしてください");
            return;
        }

        // Bluetoothオンかチェック
        if (!bluetoothAdapter.isEnabled()) {
            Log.d(TAG, "errorMessage Bluetoothオフ時の処理をしてください");
            return;
        }

         mBluetoothLeAdvertiser = bluetoothAdapter.getBluetoothLeAdvertiser();
        final boolean isAdvertiseSupported = mBluetoothLeAdvertiser != null;
        // Advertiseサポートチェック
        if(!isAdvertiseSupported){
            Log.d(TAG, "errorMessage Bluetooth Low Enerey Advertise非サポート時の処理をしてください");
            return;
        }
        // 以下はAdvertiseSettings
        // 何も指定しないと、
        // モード（ADVERTISE_MODE_LOW_POWER）、TxPowerLevel（ADVERTISE_TX_POWER_MEDIUM）、タイムアウト(0)、Connectable(true)
        // が設定されます。
        final AdvertiseSettings.Builder settingsBuilder = new AdvertiseSettings.Builder();
        settingsBuilder.setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_LOW_POWER);
        settingsBuilder.setTxPowerLevel(AdvertiseSettings.ADVERTISE_TX_POWER_MEDIUM);
        settingsBuilder.setTimeout(0);
        settingsBuilder.setConnectable(true);

        // 以下はAdvertiseData
        // 1-5バイト目のフラグはAdvertiseSettingsのsetConnectable(true)で行われています
        // 6-7バイト目の会社コードはaddManufactureDataで後ほど構築します
        // なので残りの23バイトをByteBufferを用いて構築します
        final byte[] manufacturerData = new byte[23];
        ByteBuffer byteBuffer = ByteBuffer.wrap(manufacturerData);
        byteBuffer.order(ByteOrder.BIG_ENDIAN);
        // iBeacon固定値(8バイト目)
        byteBuffer.put((byte) 0x02);
        // iBeaconのデータバイト数(9バイト目)
        byteBuffer.put((byte) 0x15);

        // UUID（10―25バイト目）
        final UUID uuid = UUID.fromString("0000ffe0-0000-1000-8000-00805f9b34fb");
        // 上位64ビットを追加
        byteBuffer.putLong(uuid.getMostSignificantBits());
        // 下位64ビットを追加
        byteBuffer.putLong(uuid.getLeastSignificantBits());

        // major(26-27バイト目)
        byteBuffer.putShort((short) 0x0A);
        // minor(28-29バイト目)
        byteBuffer.putShort((short) 0x1F);
        // 電波強度を表す2の補数(30バイト目)
        byteBuffer.put((byte) 0x99);

        // 会社コード(6-7バイト目)
        final int appleManufactureId = 0x004C;
        // AdvertiseData作成
        final AdvertiseData.Builder dataBuilder = new AdvertiseData.Builder();
        dataBuilder.addManufacturerData(appleManufactureId, manufacturerData);

        mAdvertiseCallback = new IBeaconAdvertiseCallback();
        mBluetoothLeAdvertiser.startAdvertising(settingsBuilder.build(), dataBuilder.build(), mAdvertiseCallback);

    }
    public void onDestroy() {
        // Advertiseの停止
        if(mBluetoothLeAdvertiser != null){
            mBluetoothLeAdvertiser.stopAdvertising(mAdvertiseCallback);
            mBluetoothLeAdvertiser = null;
        }
        super.onDestroy();
    }
}
