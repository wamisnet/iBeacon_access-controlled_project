package ibeacon.net.print;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import com.nifty.cloud.mb.core.FindCallback;
import com.nifty.cloud.mb.core.NCMB;
import com.nifty.cloud.mb.core.NCMBException;
import com.nifty.cloud.mb.core.NCMBObject;
import com.nifty.cloud.mb.core.NCMBQuery;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private ArrayAdapter<String> adapter = null;
    private ListView _listView = null;
    private ProgressDialog progressDialog;
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
        //TestClassを検索するためのNCMBQueryインスタンスを作成
        NCMBQuery<NCMBObject> query = new NCMBQuery<>("AttendClass");//AttendClass
        //データストアからデータを検索
        query.whereEqualTo("Gakkyu_ID", id);

        query.addOrderByDescending("attend");
        query.addOrderByDescending("createDate");
        query.findInBackground(new FindCallback<NCMBObject>() {
            @Override
            public void done(List<NCMBObject> objects, NCMBException e) {
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
                        Log.i("NCMB", o.getString("attend") + ":" + o.getString("createDate"));
                        user = o.getString("attend");
                        for(int l=1;l<=6;l++)
                        Log.i("create getTime", String.valueOf(getTime(l,o.getString("createDate"))));
                        if (!userName.equals(user)) {
                            userName = o.getString("attend");
                            adapter.add(name + o.getString("attend"));
                        }
                        //id[i] = o.getString("Gakkyu_ID");
                        //name[i] = o.getString("Gakkyu_name");
                    }
                    _listView.setAdapter(adapter);
                }
            }
        });
    }
    private int getTime(int mode, String createTime) {
        int index,indexaf;

        if(mode==1) {//year
            return Integer.parseInt(createTime.substring(0, 4));
        }
        index=createTime.indexOf("-");
        index=createTime.indexOf("-",index+1);
        if(mode==2) {//mon
            return Integer.parseInt(createTime.substring(5, index));
        }
        indexaf=createTime.indexOf("T",index+1);
        if (mode == 3) {//day
            return Integer.parseInt(createTime.substring(index+1, indexaf));
        }
        index=createTime.indexOf(":",indexaf+1);
        if(mode==4){//h
            return Integer.parseInt(createTime.substring(indexaf+1, index))+9;
        }
        indexaf=createTime.indexOf(":",index+1);
        if(mode==5){//m
            return Integer.parseInt(createTime.substring(index+1, indexaf));
        }
        index=createTime.indexOf(".",indexaf+1);
        if(mode==6){//s
            return Integer.parseInt(createTime.substring(indexaf+1, index));
        }
        return Integer.parseInt(createTime);
    }


}
