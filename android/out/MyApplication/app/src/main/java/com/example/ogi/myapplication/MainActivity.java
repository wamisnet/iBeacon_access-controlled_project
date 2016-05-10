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

public class MainActivity extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        double[] string;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // ボタンのオブジェクトを取得
        Button btn = (Button) findViewById(R.id.button);

        // クリックイベントを受け取れるようにする
        btn.setOnClickListener(new OnClickListener() {
            // このメソッドがクリック毎に呼び出される
            public void onClick(View v) {
                // ここにクリックされたときの処理を記述
                EditText edit = (EditText) findViewById(R.id.EditText);
                SpannableStringBuilder sp = (SpannableStringBuilder) edit.getText();
                Log.v("onCreate", sp.toString());
                String[] string = sp.toString().substring ;
                }
            }
        });
    }
}

  /*  protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
}*/

