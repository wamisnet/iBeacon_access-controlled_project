package ibeacon.net.print;

/**
 * Created by wami on 2016/12/08.
 */

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.nifty.cloud.mb.core.FindCallback;
import com.nifty.cloud.mb.core.NCMBException;
import com.nifty.cloud.mb.core.NCMBObject;
import com.nifty.cloud.mb.core.NCMBQuery;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;


public class DialogManager {
    private Context context;
    private ArrayAdapter<String> _adapter;
    private ListView _listView = null;
    DialogManager(Context context, ArrayAdapter<String> _adapter,ListView _listView){this.context=context;this._adapter=_adapter;this._listView=_listView;}
    SubActivity subActivity = new SubActivity();

    private ProgressDialog progressDialog;
    private String pClassName;
    private String pClassID;
    private String pTearcherName;
    private String pTearcherID;
    private String pLessonName;
    private String pLessonID;
    private String pRoom;

    public void Attendance(){
        pushProgress("教室");
        ClassLoad();


    }

    public void pushProgress(String name){
        progressDialog = new ProgressDialog(context);
        progressDialog.setTitle(name+"を検索中");
        progressDialog.setMessage("インターネットに接続してデータベースを検索しています。");
        //progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.show();
    }
    private void ClassLoad(){
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
                    showClassDialog(name,id);
                }
            }
        });
    }
    private void TeacherLoad(){
        //TestClassを検索するためのNCMBQueryインスタンスを作成
        NCMBQuery<NCMBObject> query = new NCMBQuery<>("teacher");//AttendClass
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
                        id[i] = o.getString("Teacher_ID");
                        name[i] = o.getString("Teacher_name");

                    }
                    progressDialog.dismiss();
                    showTeacherDialog(name,id);
                }
            }
        });
    }
    private void showClassDialog(final String[] className, final String[] id){
        int defaultItem = 0; // デフォルトでチェックされているアイテム
        final List<Integer> checkedItems = new ArrayList<>();
        checkedItems.add(defaultItem);
        new AlertDialog.Builder(context)
                .setTitle("Teacher Selector")
                .setSingleChoiceItems(className, defaultItem, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        checkedItems.clear();
                        checkedItems.add(which);
                    }
                })
                .setPositiveButton("Next", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (!checkedItems.isEmpty()) {
                            Log.d("checkedItem:", "" + checkedItems.get(0));
                            Log.d("checkedItemSrect:", "" + id[checkedItems.get(0)]);
                            pClassName = className[checkedItems.get(0)];
                            pClassID = id[checkedItems.get(0)];
                            pushProgress("講師リスト");
                            TeacherLoad();
                        }
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
    private void showTeacherDialog(final String[] className, final String[] id){
        int defaultItem = 0; // デフォルトでチェックされているアイテム
        final List<Integer> checkedItems = new ArrayList<>();
        checkedItems.add(defaultItem);
        new AlertDialog.Builder(context)
                .setTitle("Class Selector")
                .setSingleChoiceItems(className, defaultItem, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        checkedItems.clear();
                        checkedItems.add(which);
                    }
                })
                .setPositiveButton("Next", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (!checkedItems.isEmpty()) {
                            Log.d("checkedItem:", "" + checkedItems.get(0));
                            Log.d("checkedItemSrect:", "" + id[checkedItems.get(0)]);
                            pTearcherName = className[checkedItems.get(0)];
                            pTearcherID = id[checkedItems.get(0)];
                            pushProgress("授業リスト");
                            LessonLoad();
                        }
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
    private void LessonLoad(){
        //TestClassを検索するためのNCMBQueryインスタンスを作成
        NCMBQuery<NCMBObject> query = new NCMBQuery<>("Jugyo");//AttendClass
        //データストアからデータを検索
        query.whereEqualTo("Teacher_ID", pTearcherID);
        Log.d("lesson",pTearcherID);
        query.findInBackground(new FindCallback<NCMBObject>() {
            @Override
            public void done(List<NCMBObject> objects, NCMBException e) {
                if (e != null) {
                    Log.d("NCMBQuery", "err:"+String.valueOf(e));
                    //検索失敗時の処理
                } else {
                    String[] name = new String[objects.size()];
                    String[] id = new String[objects.size()];
                    String[] room = new String[objects.size()];
                    for (int i = 0, n = objects.size(); i < n; i++) {
                        NCMBObject o = objects.get(i);
                        //Log.i("NCMB", o.getString("Gakkyu_name"));
                        id[i] = o.getString("Jugyo_ID");
                        name[i] = o.getString("Jugyo_name");
                        room[i] = o.getString("room_name");

                    }
                    progressDialog.dismiss();
                    showLessonDialog(name,id,room);
                }
            }
        });
    }
    private void showLessonDialog(final String[] className, final String[] id,final String[] room){
        int defaultItem = 0; // デフォルトでチェックされているアイテム
        final List<Integer> checkedItems = new ArrayList<>();
        checkedItems.add(defaultItem);
        new AlertDialog.Builder(context)
                .setTitle("Lesson Selector")
                .setSingleChoiceItems(className, defaultItem, new DialogInterface.OnClickListener() {
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
                            pLessonName = className[checkedItems.get(0)];
                            pLessonID = id[checkedItems.get(0)];
                            pRoom=room[checkedItems.get(0)];
                            pushProgress("生徒");
                            AttendLoad();
                        }
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
    private void AttendLoad() {
        final int[] major = new int[1];
        final int[] minor = new int[1];
        NCMBQuery<NCMBObject> query = new NCMBQuery<>("ClassRoom");
        //keyというフィールドがvalueとなっているデータを検索する条件を設定
        query.whereEqualTo("room_name", pRoom);
        //データストアからデータを検索
        query.findInBackground(new FindCallback<NCMBObject>() {
            @Override
            public void done(List<NCMBObject> objects, NCMBException e) {
                if (e != null) {
                    Log.d("query", "err");
                    //検索失敗時の処理
                } else {
                    NCMBObject o;
                    for (int i = 0, n = objects.size(); i < n; i++) {
                        o = objects.get(i);
                        Log.i("NCMB", o.getString("room_name"));
                        //TestClassを検索するためのNCMBQueryインスタンスを作成
                        NCMBQuery<NCMBObject> query1 = new NCMBQuery<>("AttendClass");//AttendClass
                        //データストアからデータを検索
                        query1.whereEqualTo("major",o.getInt("major"));
                        query1.whereEqualTo("minor", o.getInt("minor"));
                        query1.addOrderByDescending("attend");
                        query1.addOrderByDescending("createDate");
                        progressDialog.dismiss();
                        query1.findInBackground(new FindCallback<NCMBObject>() {
                            @Override
                            public void done(List<NCMBObject> objects, NCMBException e) {

                                if (e != null) {
                                    Log.d("NCMBQuery", "err:" + String.valueOf(e));
                                    //検索失敗時の処理
                                } else {
                                    for (int i = 0; i < objects.size(); i++) {
                                        NCMBObject o = objects.get(i);
                                        Log.d("Attend",o.getString("attend"));
                                        String returnText = subActivity.obj_print(1, o, pLessonName, pClassName+o.getString("attend"));
                                        if(!returnText.equals("")){_adapter.add(returnText);}

                                    }
                                    if(_adapter!=null)_listView.setAdapter(_adapter);//subActivity.setList(_adapter);
                                }
                            }
                        });
                    }
                    //Log.d("deta", String.valueOf(results.get(0)));
                    Log.d("deta", "end");
                    //検索成功時の処理
                }
            }
        });
    }
}