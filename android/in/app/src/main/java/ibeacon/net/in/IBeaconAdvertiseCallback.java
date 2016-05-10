package ibeacon.net.in;

import android.bluetooth.le.AdvertiseCallback;
import android.bluetooth.le.AdvertiseSettings;
import android.util.Log;

public class IBeaconAdvertiseCallback extends AdvertiseCallback {
    private final String TAG = "callback";
    @Override
    public void onStartSuccess(AdvertiseSettings settingsInEffect) {
        super.onStartSuccess(settingsInEffect);
        // Advertise成功時に呼び出されます
    }

    @Override
    public void onStartFailure(int errorCode) {
        super.onStartFailure(errorCode);
        // Advertise失敗時に呼び出されます
        String errorMessage = "";
        switch (errorCode) {
            case ADVERTISE_FAILED_ALREADY_STARTED:
                errorMessage = "既にAdvertiseを実行中です";
                break;
            case ADVERTISE_FAILED_DATA_TOO_LARGE:
                errorMessage = "Advertiseのメッセージが大きすぎます";
                break;
            case ADVERTISE_FAILED_FEATURE_UNSUPPORTED:
                errorMessage = "Advertiseをサポートしていません";
                break;
            case ADVERTISE_FAILED_INTERNAL_ERROR:
                errorMessage = "内部エラーが発生しました";
                break;
            case ADVERTISE_FAILED_TOO_MANY_ADVERTISERS:
                errorMessage = "利用可能なAdvertiseのインスタンスが余っていません";
                break;
        }
        Log.d(TAG, errorMessage);
    }
}