package ibeacon.net.in;

import android.app.FragmentTransaction;
import android.os.Bundle;


/**
 * Created by wami on 2016/05/10.
 */
public class Activity extends android.app.Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // フラグメントトランザクションを開始する
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        // ルートビューをフラグメントに差し替える
        transaction.replace(android.R.id.content, new MainActivity());
        // 設定をコミットする
        transaction.commit();
    }
}
