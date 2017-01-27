package ibeacon.net.print;

import android.util.Log;

import com.nifty.cloud.mb.core.NCMBObject;

import java.util.Calendar;

/**
 * Created by wami on 2016/12/09.
 */

public class TimerManager {
    int compH[] = {9, 11, 13, 15};
    int compST[] = {20, 00, 20, 00};

    int dcompH[] = {10, 12, 14, 16, 24}; //検証用
    int dcompST[] = {00, 19, 45, 02};

    int syu_ato1[] = {15, 00, 15, 00};
    int timerange = 10;
    int timerange2 = 20;
    public int getTime(int mode, String createTime) {
        int index, indexaf;

        if (mode == 1) {//year
            return Integer.parseInt(createTime.substring(0, 4));
        }
        index = createTime.indexOf("-");
        index = createTime.indexOf("-", index + 1);
        if (mode == 2) {//mon
            return Integer.parseInt(createTime.substring(5, index));
        }
        indexaf = createTime.indexOf("T", index + 1);
        if (mode == 3) {//day
            return Integer.parseInt(createTime.substring(index + 1, indexaf));
        }
        index = createTime.indexOf(":", indexaf + 1);
        if (mode == 4) {//h
            return Integer.parseInt(createTime.substring(indexaf + 1, index)) + 9;
        }
        indexaf = createTime.indexOf(":", index + 1);
        if (mode == 5) {//m
            return Integer.parseInt(createTime.substring(index + 1, indexaf));
        }
        index = createTime.indexOf(".", indexaf + 1);
        if (mode == 6) {//s
            return Integer.parseInt(createTime.substring(indexaf + 1, index));
        }
        return Integer.parseInt(createTime);
    }

    public String Attend(String TextTime, int timeConunt){
        if (getTime(5, TextTime) > dcompST[timeConunt-1]-timerange && (getTime(5,TextTime) < dcompST[timeConunt-1] + timerange)) {
            return ("○");
        } else if ((getTime(5, TextTime) >= (dcompST[timeConunt-1] + timerange )) && (getTime(5, TextTime) < (dcompST[timeConunt-1] + timerange2 ))){
            return ("△");
        } else {
            return ("×");
        }
    }

    public String hantei(NCMBObject o)//出席判定
    {
        int ii;
        Calendar now = Calendar.getInstance(); //インスタンス化
        int h = now.get(now.HOUR_OF_DAY);//時を取得

        for (ii = 0; ii < 4; ii++) {
            Log.d("ループ", String.valueOf(h));
            if (compH[ii] <= h && h < compH[ii + 1]) {
                Log.d("break前ii", String.valueOf(ii));
                break;
            }
        }
        //授業開始時間後からの出席判定
        if (getTime(4, o.getString("createDate")) == compH[ii]) {
            if (getTime(5, o.getString("createDate")) >= syu_ato1[ii] && (getTime(5, o.getString("createDate")) <= syu_ato1[ii] + timerange)) {
                return ("○");
            } else if (getTime(5, o.getString("createDate")) < (syu_ato1[ii] + timerange2))//授業開始時間から15分までの範囲
                return ("△");
            //授業出席時間前からの出席判定(00分が授業開始時刻の場合)
        } else {return  ("A");
        }

        Log.d("hantei", String.valueOf(ii));
        Log.d("hantei", "notfound");
        return ("×");
    }
}
