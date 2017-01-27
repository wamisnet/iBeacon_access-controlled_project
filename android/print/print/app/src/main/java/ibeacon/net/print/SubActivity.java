package ibeacon.net.print;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Adapter;
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
    TimerManager timerManager = new TimerManager();
    int tikoku_count = 0;
    int kesseki_count = 0;
    String userName = "";
    String reach;
    String out;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.subactivity);
        NCMB.initialize(this.getApplicationContext(), "fe8cc228956e2f26276c141ce824efb4810c9d711119dcd511e2cd8b39438913",
                "481f20a51e4ad7d6536280acb04fa83b05023e67105110b36040a221b16f1682");
        _listView = (ListView) findViewById(R.id.list_item2);
        findViewById(R.id.reload2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _adapter = new ArrayAdapter<String>(getApplicationContext(),R.layout.custom_listview);
                DialogManager dialogManager = new DialogManager(SubActivity.this,_adapter,_listView);
                dialogManager.Attendance();
            }
        });

        findViewById(R.id.button2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplication(), MainActivity.class);
                intent.setClassName("ibeacon.net.print", "ibeacon.net.print.MainActivity");
                startActivity(intent);
            }

        });
    }


    public  boolean week_judge(NCMBObject o){
     Calendar calendar = Calendar.getInstance();
     calendar.set(timerManager.getTime(1,o.getString("createDate")),timerManager.getTime(2,o.getString("createDate"))-1,timerManager.getTime(3,o.getString("createDate")));
     Log.d("曜日", String.valueOf(calendar.get(Calendar.DAY_OF_WEEK)));
     Log.d("日付", String.valueOf(calendar.get(Calendar.DATE)));

     long millis1 = calendar.getTimeInMillis();
     Log.d("みり", String.valueOf(millis1));
        Log.d("week", o.getString("week"));
     if(Integer.parseInt(o.getString("week"))==(int)calendar.get(Calendar.DAY_OF_WEEK)){
        return  true;
     }

     return false;
 }


    public String obj_print(int time, NCMBObject o, String kamoku, String name) {
        String user;

        Log.i("NCMB", o.getString("attend") + ":" + o.getString("createDate"));
        String warn_flag = "";
        int flag = 0;

        user = o.getString("attend");
        if(userName.equals(user)) {
            userName = user;

            if(timerManager.hantei(o).equals("△")){
                tikoku_count++;
            }else if(timerManager.hantei(o).equals("×")){
                kesseki_count++;
            }
            //欠席３回で１５％
            //欠席５回で２５％
            //遅刻３回で欠席１回

            if(tikoku_count == 3){
                kesseki_count++;
                tikoku_count = 0;
            }


        }else{
            if(kesseki_count >= 3 && kesseki_count < 5){

                return userName+"15%";
            }else{
                return userName;
            }
        }
        return "";
    }


}

