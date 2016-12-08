package ibeacon.net.print;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import com.nifty.cloud.mb.core.FindCallback;
import com.nifty.cloud.mb.core.NCMB;
import com.nifty.cloud.mb.core.NCMBException;
import com.nifty.cloud.mb.core.NCMBObject;
import com.nifty.cloud.mb.core.NCMBQuery;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by ogi on 2016/12/01.
 */

public class SubActivity extends AppCompatActivity {
    private ArrayAdapter<String> _adapter = null;
    private ListView _listView = null;
    private ProgressDialog progressDialog;

    int compH[] = {9, 11, 13, 15};
    int timerange = 5;
    int timerange2 = 15;
    int compST[] = {20, 00, 20, 00};

    int dcompH[] = {10, 12, 14, 16, 24}; //検証用
    int dcompST[] = {20, 19, 45, 02};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.subactivity);
        NCMB.initialize(this.getApplicationContext(), "fe8cc228956e2f26276c141ce824efb4810c9d711119dcd511e2cd8b39438913",
                "481f20a51e4ad7d6536280acb04fa83b05023e67105110b36040a221b16f1682");

        findViewById(R.id.reload2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pushProgress();

            }
        });

        findViewById(R.id.button2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {// インテントの生成
                Intent intent = new Intent(getApplication(), MainActivity.class);
                intent.setClassName("ibeacon.net.print", "ibeacon.net.print.MainActivity");

                // SubActivity の起動
                startActivity(intent);

            }

        });
        // LayoutファイルのListViewのリソースID
        _listView = (ListView) findViewById(R.id.list_item2);
        _adapter = new ArrayAdapter<String>(getApplicationContext(),
                R.layout.custom_listview
        );
    }

    private void pushProgress() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("教室を検索中");
        progressDialog.setMessage("インターネットに接続してデータベースを検索しています。");
        //progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.show();
        pushReload();
    }

    private void pushReload() {
        //TestClassを検索するためのNCMBQueryインスタンスを作成
        NCMBQuery<NCMBObject> query = new NCMBQuery<>("Gakkyu");//AttendClass
        //データストアからデータを検索
        query.findInBackground(new FindCallback<NCMBObject>() {
            @Override
            public void done(List<NCMBObject> objects, NCMBException e) {
                if (e != null) {
                    Log.d("NCMBQuery", "err:" + String.valueOf(e));
                    //検索失敗時の処理
                } else {
                    String[] name = new String[objects.size()];
                    String[] id = new String[objects.size()];
                    for (int i = 0, n = objects.size(); i < n; i++) {
                        NCMBObject o = objects.get(i);
                        //Log.i("NCMB", o.getString("Gakkyu_name"));
                        id[i] = o.getString("Gakkyu_ID");
                        name[i] = o.getString("Gakkyu_name");

                    }
                    progressDialog.dismiss();
                    showdDialog(name, id);
                }
            }
        });
    }

    private void showdDialog(final String[] items, final String[] id) {
        int defaultItem = 0; // デフォルトでチェックされているアイテム
        final List<Integer> checkedItems = new ArrayList<>();
        checkedItems.add(defaultItem);
        new AlertDialog.Builder(SubActivity.this)
                .setTitle("Class Selector")
                .setSingleChoiceItems(items, defaultItem, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        checkedItems.clear();
                        checkedItems.add(which);
                    }
                })
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (!checkedItems.isEmpty()) {
                            Log.d("checkedItem:", "" + checkedItems.get(0));
                            Log.d("checkedItemSrect:", "" + id[checkedItems.get(0)]);
                            setNowClass(id[checkedItems.get(0)], items[checkedItems.get(0)]);
                        }
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void setNowClass(final String id, final String name) {
        final Calendar now = Calendar.getInstance(); //インスタンス化
        now.setTimeInMillis(System.currentTimeMillis());
        //TestClassを検索するためのNCMBQueryインスタンスを作成
        NCMBQuery<NCMBObject> query = new NCMBQuery<>("AttendClass");//AttendClass
        //データストアからデータを検索
        query.whereEqualTo("Gakkyu_ID", id);
        query.addOrderByDescending("attend");
        query.addOrderByDescending("createDate");

        query.findInBackground(new FindCallback<NCMBObject>() {
            @Override
            public void done(List<NCMBObject> objects, NCMBException e) {
                final TextView settext3 = (TextView)findViewById(R.id.textView3);
                if (e != null) {
                    Log.d("NCMBQuery", "err:" + String.valueOf(e));
                    //検索失敗時の処理
                } else {
                    for (int i = 0, n = objects.size(); i < n; i++) {
                        NCMBObject o = objects.get(i);
                        obj_print(2, 1, o, "卒研", name);
                    }
                }
                _listView.setAdapter(_adapter);
                settext3.setText("クラス:"+id);
            }
        });
    }

    private int getTime(int mode, String createTime) {
        int index, indexaf;

        if (mode == 1) {//year
            return Integer.parseInt(createTime.substring(0, 4));
        }
        index = createTime.indexOf("-");
        index = createTime.indexOf("-", index + 1);
        if (mode == 2) {//mon
            return Integer.parseInt(createTime.substring(5, index));
        }
        indexaf = createTime.indexOf("T", index + 1);
        if (mode == 3) {//day
            return Integer.parseInt(createTime.substring(index + 1, indexaf));
        }
        index = createTime.indexOf(":", indexaf + 1);
        if (mode == 4) {//h
            return Integer.parseInt(createTime.substring(indexaf + 1, index)) + 9;
        }
        indexaf = createTime.indexOf(":", index + 1);
        if (mode == 5) {//m
            return Integer.parseInt(createTime.substring(index + 1, indexaf));
        }
        index = createTime.indexOf(".", indexaf + 1);
        if (mode == 6) {//s
            return Integer.parseInt(createTime.substring(indexaf + 1, index));
        }
        return Integer.parseInt(createTime);
    }

    public void obj_print(int youbi, int time, NCMBObject o, String kamoku, String name) {
        String user, userName;
        int tikoku_count = 0;
        int kesseki_count = 0;
        Log.i("NCMB", o.getString("attend") + ":" + o.getString("createDate"));
        user = o.getString("attend");
        for (int l = 1; l <= 6; l++)
            Log.i("create getTime", String.valueOf(getTime(l, o.getString("createDate"))));
        //    if (!userName.equals(user)) {
        userName = o.getString("attend");
        //出欠カウント
        if((hantei(o,time)) == "×" ){
            kesseki_count++;
        }else if ((hantei(o,time)) == "△" ) {
            tikoku_count++;
        }
        if(tikoku_count > 3 && tikoku_count < 6) {
                kesseki_count++;
                tikoku_count = tikoku_count % 3;
        }else if (tikoku_count >= 6){
            kesseki_count += 2;
            tikoku_count = tikoku_count % 3;
        }

        _adapter.add(name + o.getString("attend") +"               " + hantei(o,time));
    }

    public String hantei(NCMBObject o,int time)//出席判定
    {
        Calendar now = Calendar.getInstance(); //インスタンス化
        int h = now.get(now.HOUR_OF_DAY);//時を取得

            if (getTime(5, o.getString("createDate")) > dcompST[time-1] && (getTime(5, o.getString("createDate")) < dcompST[time-1] + timerange)) {
                return ("○");
            } else if ((getTime(5, o.getString("createDate")) >= (dcompST[time-1] + timerange )) && (getTime(5, o.getString("createDate")) < (dcompST[time-1] + timerange2 ))){
                return ("△");
            } else {
                return ("×");
            }
    }
}

