package com.example.ogi.myapplication;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by wami on 2016/11/17.
 */

public class FileManager {
    private Context context;
    FileManager(Context context){this.context=context;}

    public void FileWrite( String id, String data){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        sp.edit().putString(id, data).commit();
        Toast toast = Toast.makeText(context, "ファイルに保存しました", Toast.LENGTH_SHORT);
        toast.show();
    }

    public String FileRead(String id) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getString(id, null);
    }
}
