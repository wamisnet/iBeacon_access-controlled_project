package com.example.ogi.myapplication;

import android.os.Bundle;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.util.Log;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.nifty.cloud.mb.core.FindCallback;
import com.nifty.cloud.mb.core.NCMB;
import com.nifty.cloud.mb.core.NCMBException;
import com.nifty.cloud.mb.core.NCMBObject;
import com.nifty.cloud.mb.core.NCMBQuery;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    ProgressBar progressBar;
    private int temporaryColorInt;
    BLEManager bleManager = new BLEManager(this);
    FileManager fileManager = new FileManager();
    PermissionManager permissionManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //表示関連
            //ProgressBar
        setProgressTime(0);
            // ボタンのオブジェクトを取得
        Button save_btn = (Button) findViewById(R.id.savebutton);
        Button scan_btn = (Button) findViewById(R.id.scanbutton);
            //EditText
        final EditText et = (EditText) findViewById(R.id.EditText);
            //エラー検知
        assert et != null;
        assert save_btn != null;
        assert scan_btn != null;
            //SWを押したときに色が変わる
        pushSw(scan_btn);
        pushSw(save_btn);

            //権限チェック
        permissionManager = new PermissionManager(getApplicationContext(),this);
        permissionManager.PermissionCheck();
            // AlarmManager を開始する
        TimerServices.startAlarm(getApplicationContext());
            //BLEManagerの初期化
        bleManager.init();
        bleManager.setUser(fileManager.FileRead("user.txt", "user", getApplication()));
            //edittextに文字を挿入
        et.append(fileManager.FileRead("user.txt", "user", getApplication()));
        //Mbaasを使用する為のAPIキー
        NCMB.initialize(getApplication(), "fe8cc228956e2f26276c141ce824efb4810c9d711119dcd511e2cd8b39438913",
                "481f20a51e4ad7d6536280acb04fa83b05023e67105110b36040a221b16f1682");

        //ボタンを押したときの処理
        buttonListener(scan_btn,save_btn);
        //ネット検索してリスト表示をする
        searchClassList();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        permissionManager.RequestPermissionsResult(requestCode,permissions,grantResults);
    }
    private void pushSw(Button button){
        button.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        ColorDrawable colorDrawable = (ColorDrawable) v.getBackground();
                        temporaryColorInt = colorDrawable.getColor();
                        float[] hsv = new float[3];
                        Color.colorToHSV(colorDrawable.getColor(), hsv);
                        hsv[2] -= 0.2f;
                        v.setBackgroundColor(Color.HSVToColor(hsv));
                        break;
                    case MotionEvent.ACTION_UP:
                        v.setBackgroundColor(temporaryColorInt);
                        break;
                }
                return false;
            }
        });
    }
    private void buttonListener(Button scan_btn,Button save_btn){
        scan_btn.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                setProgressTime(2000);
                bleManager.search();
            }
        });

        save_btn.setOnClickListener(new OnClickListener() {
            // このメソッドはクリックされる毎に呼び出される
            public void onClick(View v) {
                final EditText et = (EditText) findViewById(R.id.EditText);
                fileManager.FileWrite("user.txt", "user", et.getText().toString(), getApplicationContext());
                bleManager.setUser(fileManager.FileRead("user.txt", "user", getApplicationContext()));
                Toast toast = Toast.makeText(getApplicationContext(), "ファイルに保存しました", Toast.LENGTH_SHORT);
                toast.show();
            }

        });
    }
    private void setProgressTime(int time) {
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        assert progressBar != null;
        progressBar.setVisibility(View.GONE);
        Handler mHandler = new Handler();
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                progressBar.setVisibility(View.GONE);
            }
        }, time);
        progressBar.setVisibility(View.VISIBLE);
    }
    private void searchClassList(){

        //Classを検索するためのNCMBQueryインスタンスを作成
        NCMBQuery<NCMBObject> query = new NCMBQuery<>("Gakkyu");

        //データストアからデータを検索
        query.findInBackground(new FindCallback<NCMBObject>() {
            @Override
            public void done(List<NCMBObject> objects, NCMBException e) {
                if (e != null) {
                    Log.d("query", "err");
                    //検索失敗時の処理
                } else {
                    // LayoutファイルのListViewのリソースID
                    ListView _listView = (ListView) findViewById(R.id.list_item);
                    // Androidフレームワーク標準のレイアウト
                    ArrayAdapter<String>adapter = new ArrayAdapter<String>(getApplicationContext(),
                            R.layout.custom_listview
                    );

                    String[] name=new String[objects.size()];
                    String[] id=new String[objects.size()];
                    for (int i = 0, n = objects.size(); i < n; i++) {
                        NCMBObject o = objects.get(i);
//                        Log.i("NCMB", o.getString("room_name"));

                        id[i] = o.getString("Gakkyu_ID");
                        name[i] = o.getString("Gakkyu_name");
                        adapter.add(name[i]);

                    }
                    _listView.setAdapter(adapter);

                    selectList(_listView,name,id);
                    Log.d("deta", "end");
                    //検索成功時の処理
                }
            }
        });

    }
    private void selectList(ListView _listView , final String  []name, final String []userid){
        _listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //ここに処理を書く
                // 選択したListViewアイテムを表示する
                ListView list = (ListView) parent;
                String selectedItem = (String) list.getItemAtPosition(position);
                Toast.makeText(getApplicationContext(), selectedItem,
                        Toast.LENGTH_LONG).show();
                fileManager.FileWrite("user.txt","user",name[position],getApplicationContext());
                fileManager.FileWrite("user.txt","userID",userid[position],getApplicationContext());
            }
        });
    }
}
