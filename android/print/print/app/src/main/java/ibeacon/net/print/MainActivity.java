package ibeacon.net.print;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.nifty.cloud.mb.core.FindCallback;
import com.nifty.cloud.mb.core.NCMB;
import com.nifty.cloud.mb.core.NCMBException;
import com.nifty.cloud.mb.core.NCMBObject;
import com.nifty.cloud.mb.core.NCMBQuery;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        NCMB.initialize(this.getApplicationContext(),"8eee2292f5c87bae5ec5bbb3bdb95ee997708bd1bc96aed8f8ed6f142ce71e61",
                "29aea4c9781e3664e4f9c959c2e04074ab3f765c74586ed447947699a5385970");

        //TestClassを検索するためのNCMBQueryインスタンスを作成
        NCMBQuery<NCMBObject> query = new NCMBQuery<>("TestClass");

//keyというフィールドがvalueとなっているデータを検索する条件を設定
        query.whereEqualTo("major", "65000");

//データストアからデータを検索
        query.findInBackground(new FindCallback<NCMBObject>() {
            @Override
            public void done(List<NCMBObject> results, NCMBException e) {
                if (e != null) {
                    Log.d("deta", "err end");
                    //検索失敗時の処理
                } else {
                  Log.d("deta", String.valueOf(results.get(0)));
                    Log.d("deta", "end");
                    //検索成功時の処理
                }
            }
        });


    }
}
