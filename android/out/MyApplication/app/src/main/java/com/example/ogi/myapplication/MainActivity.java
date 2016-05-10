package com.example.ogi.myapplication;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.app.Activity;
import android.os.Bundle;
import android.widget.EditText;
import android.text.SpannableStringBuilder;
import android.app.Activity;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

public class MainActivity extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // ボタンのオブジェクトを取得
        Button btn = (Button) findViewById(R.id.button);

        try{
            InputStream in = openFileInput("a.txt");
            BufferedReader reader =
            new BufferedReader(new InputStreamReader(in,"UTF-8"));
            String gakuseki;
            EditText et = (EditText)findViewById(R.id.EditText);
            while((gakuseki = reader.readLine())!= null){
                et.append(gakuseki);
                }
            reader.close();
        }catch(IOException e){
            e.printStackTrace();
        }

        // クリックイベントを受け取れるようにする
        btn.setOnClickListener(new OnClickListener() {
            // このメソッドがクリック毎に呼び出される
            public void onClick(View v) {
                // ここにクリックされたときの処理を記述
                EditText edit = (EditText) findViewById(R.id.EditText);
                String gakuseki = edit.getText().toString();
                Log.v("onCreate", gakuseki);

                try{
                    OutputStream out = openFileOutput("a.txt",MODE_PRIVATE);
                    PrintWriter writer =
                    new PrintWriter(new OutputStreamWriter(out,"UTF-8"));
                    writer.append(gakuseki);
                    writer.close();
                }catch(IOException e){
                    e.printStackTrace();
                }
            }
        });
    }
}
//文字を送る
//ビーコンの判別
  /*  protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
}*/

