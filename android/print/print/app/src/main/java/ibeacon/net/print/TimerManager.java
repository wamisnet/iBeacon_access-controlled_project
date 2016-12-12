package ibeacon.net.print;

import com.nifty.cloud.mb.core.NCMBObject;

import java.util.Calendar;

/**
 * Created by wami on 2016/12/09.
 */

public class TimerManager {
    int compH[] = {9, 11, 13, 15};
    int timerange = 5;
    int timerange2 = 15;
    int compST[] = {20, 00, 20, 00};

    int dcompH[] = {10, 12, 14, 16, 24}; //検証用
    int dcompST[] = {00, 19, 45, 02};
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
}
