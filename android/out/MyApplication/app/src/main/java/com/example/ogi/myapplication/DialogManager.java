package com.example.ogi.myapplication;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.nifty.cloud.mb.core.FindCallback;
import com.nifty.cloud.mb.core.NCMBException;
import com.nifty.cloud.mb.core.NCMBObject;
import com.nifty.cloud.mb.core.NCMBQuery;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by wami on 2016/11/28.
 */

public class DialogManager {
    private Context context;
    DialogManager(Context context){this.context=context;}

    private ProgressDialog progressDialog;

    public void pushProgress(){
        progressDialog = new ProgressDialog(context);
        progressDialog.setTitle("教室を検索中");
        progressDialog.setMessage("インターネットに接続してデータベースを検索しています。");
        //progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.show();
        ServerLoad();
    }
    private void ServerLoad(){
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
    private void showClassDialog(final String[] items, final String[] id){
        int defaultItem = 0; // デフォルトでチェックされているアイテム
        final List<Integer> checkedItems = new ArrayList<>();
        checkedItems.add(defaultItem);
        new AlertDialog.Builder(context)
                .setTitle("Class Selector")
                .setSingleChoiceItems(items, defaultItem, new DialogInterface.OnClickListener() {
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
                            showUserDialog(items[checkedItems.get(0)],id[checkedItems.get(0)]);
                        }
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
    private void showUserDialog(final String name, final String id){
        LayoutInflater factory = LayoutInflater.from(context);
        final View inputView = factory.inflate(R.layout.input_dialog, null);
        TextView textView = (TextView) inputView.findViewById(R.id.dialog_textview);
        textView.setText(name);
        final EditText edit= (EditText) inputView.findViewById(R.id.dialog_edittext);
        new AlertDialog.Builder(context)
                .setTitle("UserName Editer")
                .setView(inputView)

                .setPositiveButton("Next", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        showCheckDialog(name,id,edit.getText().toString());
                    }
                })
                .setNeutralButton("再入力", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        pushProgress();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
    private void showCheckDialog(final String name, final String id, final String number){
        final LayoutInflater factory = LayoutInflater.from(context);
        final View inputView = factory.inflate(R.layout.check_dialog, null);
        TextView textView = (TextView) inputView.findViewById(R.id.dialog_textview);
        textView.setText(name+number+" で保存されます" );

        new AlertDialog.Builder(context)
                .setTitle("Check Dialog")
                .setView(inputView)

                .setPositiveButton("保存", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        FileManager fileManager = new FileManager(context);
                        fileManager.FileWrite("classID",id);
                        fileManager.FileWrite("number",number);
                        fileManager.FileWrite("name",name);

                    }
                })
                .setNeutralButton("再入力", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        pushProgress();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}
