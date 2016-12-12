package ibeacon.net.print;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

import static java.util.Calendar.HOUR_OF_DAY;
import static java.util.Calendar.MINUTE;

public class MainActivity extends AppCompatActivity {
    private ArrayAdapter<String> adapter = null;
    private ListView _listView = null;
    private ProgressDialog progressDialog;

    int compH[] = {9, 11, 13, 15};
    int timerange = 5;//許容範囲
    int timelate = 15;//遅刻範囲
    int timeClass = 90;//授業時間

    int dcompH[] = {10, 12, 14, 16, 24}; //検証用
    int dcompST[] = {20, 19, 45, 02};

    int HS_timetable[] = {9, 11, 13, 15};
    int MS_timetable[] = {20, 00, 20, 00};

    TimerManager timerManager = new TimerManager();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        NCMB.initialize(this.getApplicationContext(),"fe8cc228956e2f26276c141ce824efb4810c9d711119dcd511e2cd8b39438913",
                "481f20a51e4ad7d6536280acb04fa83b05023e67105110b36040a221b16f1682");

        findViewById(R.id.reload).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pushProgress();

            }
        });

        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {// インテントの生成
                Intent intent = new Intent(getApplication(), SubActivity.class);
                intent.setClassName("ibeacon.net.print", "ibeacon.net.print.SubActivity");

                // SubActivity の起動
                startActivity(intent);

            }

        });
        // LayoutファイルのListViewのリソースID
        _listView = (ListView) findViewById(R.id.list_item);
        adapter = new ArrayAdapter<String>(getApplicationContext(),
                R.layout.custom_listview
        );
    }
    private void pushProgress(){

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("教室を検索中");
        progressDialog.setMessage("インターネットに接続してデータベースを検索しています。");
        //progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.show();
        pushReload();
    }
    private void pushReload(){
        //TestClassを検索するためのNCMBQueryインスタンスを作成
        NCMBQuery<NCMBObject> query = new NCMBQuery<>("Gakkyu");//AttendClass
        //データストアからデータを検索
        query.findInBackground(new FindCallback<NCMBObject>() {
            @Override
            public void done(List<NCMBObject> objects, NCMBException e) {
                if (e != null) {
                    Log.d("NCMBQuery", "err:"+String.valueOf(e));
                    //検索失敗時の処理
                } else {
                    String[] name=new String[objects.size()];
                    String[] id=new String[objects.size()];
                    for (int i = 0, n = objects.size(); i < n; i++) {
                        NCMBObject o = objects.get(i);
                        //Log.i("NCMB", o.getString("Gakkyu_name"));
                        id[i] = o.getString("Gakkyu_ID");
                        name[i] = o.getString("Gakkyu_name");

                    }
                    progressDialog.dismiss();
                   showdDialog(name,id);
                }
            }
        });
    }
    private void showdDialog(final String[] items, final String[] id){
        int defaultItem = 0; // デフォルトでチェックされているアイテム
        final List<Integer> checkedItems = new ArrayList<>();
        checkedItems.add(defaultItem);
        new AlertDialog.Builder(MainActivity.this)
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
                            setNowClass(id[checkedItems.get(0)],items[checkedItems.get(0)]);
                        }
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
    private void setNowClass(String id, final String name){
        final Calendar now = Calendar.getInstance(); //インスタンス化
        final TextView textView = (TextView) findViewById(R.id.textView);
        final SubActivity subA = new SubActivity();
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
                int ii;
                if (e != null) {
                    Log.d("NCMBQuery", "err:" + String.valueOf(e));
                    //検索失敗時の処理
                } else {
                    adapter = new ArrayAdapter<String>(getApplicationContext(),
                            R.layout.custom_listview
                    );
                    String userName = "", user;
                    for (int i = 0, n = objects.size(); i < n; i++) {
                        NCMBObject o = objects.get(i);
                        user = o.getString("attend");

                            if (!userName.equals(user)) {
                                Log.i("create getTime", String.valueOf(o.getString("attend"))+" : "+String.valueOf(o.getString("createDate")));
                                userName = o.getString("attend");
                            int h = now.get(HOUR_OF_DAY);//時を取得
                            for(ii = 0; ii < 4; ii++){
                                //Log.d("ループ",String.valueOf(h));
                                if(dcompH[ii] <= h && h < dcompH[ii+1])
                                {
                                    //Log.d("break前ii", String.valueOf(ii));
                                    break;
                                }
                            }
                            //Log.d("break後ii", String.valueOf(ii));
                            if(ii < 4){
                                textView.setText(String.valueOf(ii + 1) + "時限目の出席状況　○：出席　×：欠席　△：遅刻");
                            }
                            //出席判定
                            adapter.add(name + o.getString("attend")+"               "+hantei(o));
                        }
                    }
                    _listView.setAdapter(adapter);
                }
            }
        });
    }

    public String hantei(NCMBObject o)//出席判定
    {
        int ii;
        Calendar NowTime = Calendar.getInstance();

        Calendar CreateTime= Calendar.getInstance();
        CreateTime.set(timerManager.getTime(1,o.getString("createDate")),timerManager.getTime(2,o.getString("createDate"))-1,timerManager.getTime(3,o.getString("createDate"))+9,timerManager.getTime(4,o.getString("createDate")),timerManager.getTime(5,o.getString("createDate")),timerManager.getTime(6,o.getString("createDate")));
        for(int i=0;i<4;i++) {
            NowTime.set(HOUR_OF_DAY,HS_timetable[i]);
            NowTime.set(MINUTE,MS_timetable[i]);
            Log.d("TimeNow", String.valueOf(NowTime.getTimeInMillis()));
            Log.d("TimeCreate", String.valueOf(CreateTime.getTimeInMillis()));
            long CompTime=(CreateTime.getTimeInMillis()-NowTime.getTimeInMillis())/60/1000;
            Log.d("Time", String.valueOf(CompTime));
            if (CompTime==0){//||CompTime<timerange*60*1000||CompTime>timerange*60*1000){
                return "〇";
            }else if(CompTime<timelate*60*1000) {
                return "△";
            }else if(CompTime<timeClass*60*1000){
                return "×";
            }
        }
        return "";

    }
}
