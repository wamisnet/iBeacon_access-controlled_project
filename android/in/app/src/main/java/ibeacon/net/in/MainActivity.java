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
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.nifty.cloud.mb.core.FindCallback;
import com.nifty.cloud.mb.core.NCMB;
import com.nifty.cloud.mb.core.NCMBException;
import com.nifty.cloud.mb.core.NCMBObject;
import com.nifty.cloud.mb.core.NCMBQuery;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class MainActivity extends Activity {
    BluetoothLeAdvertiser mBluetoothLeAdvertiser;
    IBeaconAdvertiseCallback mAdvertiseCallback;
    private final String TAG = "mainActivity";
    private ArrayAdapter<String> adapter = null;
    private ListView _listView = null;
    private int[] major;
    private int[] minor;
    private int classPosition;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mainactivity);
        // Bluetoothの生成（ここは5.0以前と一緒）
        BluetoothManager manager=(BluetoothManager)getSystemService(Context.BLUETOOTH_SERVICE);
        final BluetoothAdapter bluetoothAdapter = manager.getAdapter();

        NCMB.initialize(getApplication(), "fe8cc228956e2f26276c141ce824efb4810c9d711119dcd511e2cd8b39438913",
                "481f20a51e4ad7d6536280acb04fa83b05023e67105110b36040a221b16f1682");

        // LayoutファイルのListViewのリソースID
        _listView = (ListView) findViewById(R.id.list_item);

        // Androidフレームワーク標準のレイアウト
        adapter = new ArrayAdapter<String>(getApplicationContext(),
                R.layout.custom_listview
        );

//TestClassを検索するためのNCMBQueryインスタンスを作成
        NCMBQuery<NCMBObject> query = new NCMBQuery<>("ClassRoom");

//keyというフィールドがvalueとなっているデータを検索する条件を設定
        //query.whereEqualTo("major", "200");

//データストアからデータを検索
        query.findInBackground(new FindCallback<NCMBObject>() {
            @Override
            public void done(List<NCMBObject> objects, NCMBException e) {
                if (e != null) {
                    Log.d("query", "err");
                    //検索失敗時の処理
                } else {
                    major=new int[objects.size()];
                    minor=new int[objects.size()];
                    for (int i = 0, n = objects.size(); i < n; i++) {
                        NCMBObject o = objects.get(i);
                        Log.i("NCMB", o.getString("room_name"));

                        String name = o.getString("room_name");
                        minor[i] = o.getInt("minor");
                        major[i] = o.getInt("major");
                        adapter.add(name + " 教室");
                    }
                    //Log.d("deta", String.valueOf(results.get(0)));
                    Log.d("deta", "end");
                    //検索成功時の処理
                }
            }
        });



        _listView.setAdapter(adapter);
        _listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //ここに処理を書く
                // 選択したListViewアイテムを表示する
                ListView list = (ListView) parent;
                String selectedItem = (String) list.getItemAtPosition(position);
                Toast.makeText(getApplicationContext(), selectedItem,
                        Toast.LENGTH_LONG).show();
                classPosition=position;
            }
        });

        //起動時ファイル読み込み
        EditText et = (EditText) findViewById(R.id.editText);
        assert et != null;
        et.append(FileRead("user.txt", "user"));

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


        findViewById(R.id.startble_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
                byteBuffer.putShort((short) major[classPosition]);
                // minor(28-29バイト目)
                byteBuffer.putShort((short) minor[classPosition]);
                // 電波強度を表す2の補数(30バイト目)
                byteBuffer.put((byte) 0x99);

                // 会社コード(6-7バイト目)
                final int appleManufactureId = 0x004C;
                // AdvertiseData作成
                final AdvertiseData.Builder dataBuilder = new AdvertiseData.Builder();
                dataBuilder.addManufacturerData(appleManufactureId, manufacturerData);
                mAdvertiseCallback = new IBeaconAdvertiseCallback();
                mBluetoothLeAdvertiser.startAdvertising(settingsBuilder.build(), dataBuilder.build(), mAdvertiseCallback);
                Toast toast = Toast.makeText(getApplicationContext(), "送信開始します", Toast.LENGTH_SHORT);
                toast.show();
            }
        });

        findViewById(R.id.stopble_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBluetoothLeAdvertiser.stopAdvertising(mAdvertiseCallback);
                Toast toast = Toast.makeText(getApplicationContext(), "送信終了しました", Toast.LENGTH_SHORT);
                toast.show();
            }
        });

        findViewById(R.id.savebutton).setOnClickListener(new View.OnClickListener() {
            // このメソッドはクリックされる毎に呼び出される
            public void onClick(View v) {

                // ここにクリックされたときの処理を記述
                EditText edit = (EditText) findViewById(R.id.editText);
                assert edit != null;
                FileWrite("user.txt", "user", edit.getText().toString());
                Toast toast = Toast.makeText(getApplicationContext(), "ファイルに保存しました", Toast.LENGTH_SHORT);
                toast.show();
            }

        });
    }
    public void onDestroy() {
        // Advertiseの停止
        if(mBluetoothLeAdvertiser != null){
            mBluetoothLeAdvertiser.stopAdvertising(mAdvertiseCallback);
            mBluetoothLeAdvertiser = null;
        }
        super.onDestroy();
    }

    public void FileWrite(String filename, String id, String data){
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
    }

    public String removeString(String strSrc, String strRemove) {
        Pattern pattern = Pattern.compile(strRemove);
        Matcher matcher = pattern.matcher(strSrc);
        String strTmp = matcher.replaceAll("");

        return strTmp;
    }

    public String FileRead(String filename, String id){
        try {Log.v("fileread","テスト1");
            InputStream in = openFileInput(filename);
            BufferedReader reader =
                    new BufferedReader(new InputStreamReader(in, "UTF-8"));
            String search;
            while ((search = reader.readLine()) != null) {
                if(search.startsWith(id)){
                    Log.v("fileread",search);
                    reader.close();
                    return removeString(search,id+":");
                }
                Log.v("fileread","テスト２");
            }
            reader.close();
            return "";
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }
}
