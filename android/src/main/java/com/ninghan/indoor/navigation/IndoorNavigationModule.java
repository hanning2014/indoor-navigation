package com.ninghan.indoor.navigation;
import androidx.annotation.Nullable;
import android.util.Log;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableArray;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.modules.core.DeviceEventManagerModule;
import static com.facebook.react.bridge.UiThreadUtil.runOnUiThread;
import android.content.Context;
// 导入newbeacon功能
import com.minew.beacon.BluetoothState;
import com.minew.beacon.MinewBeacon;
import com.minew.beacon.MinewBeaconManager;
import com.minew.beacon.MinewBeaconManagerListener;
import com.ninghan.indoor.bean.Beacon;
import com.ninghan.indoor.bean.JsData;
//import com.google.gson.Gson;
import java.util.List;

public class IndoorNavigationModule extends ReactContextBaseJavaModule {
    private final String TAG ="logs";
    private ReactContext context;
    private MinewBeaconManager mMinewBeaconManager;
    //private Gson mGson = new Gson();
    private JsData mJsData = new JsData();
    @Override
    public String getName() {
        return "IndoorNavigationModule";
    }

    private void commonEvent(WritableMap map) {
        sendEvent(getReactApplicationContext(), "IndoorNavigatinEvent", map);
    }


    private void sendEvent(ReactContext reactContext,
                           String eventName,
                           @Nullable WritableMap params) {
        reactContext
                .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
                .emit(eventName, params);
    }



    public IndoorNavigationModule(ReactApplicationContext reactContext) {
        super(reactContext);
        context = reactContext;
    }

    // 初始化
    @ReactMethod
    private void initManager() {
        mMinewBeaconManager = MinewBeaconManager.getInstance(context);
    }

    // scan bluetooth state
    @ReactMethod
    private void checkBluetooth(final Callback  callback) {
        BluetoothState bluetoothState = mMinewBeaconManager.checkBluetoothState();
        switch (bluetoothState) {
            // 不支持蓝牙
            case BluetoothStateNotSupported:
                startBeaconComplete("system unsupported");
                callback.invoke("-1");
                break;
            // 蓝牙未开启
            case BluetoothStatePowerOff:
                startBeaconComplete("bluetooth power off");
                callback.invoke("0");
                break;
            // 蓝牙开启
            case BluetoothStatePowerOn:
                startBeaconComplete("ok");
                callback.invoke("1");
                if (mMinewBeaconManager != null) {
                    mMinewBeaconManager.startScan();
                }
                break;
            default:
        }
    }


     // start searchBeacon
     @ReactMethod
     private void onSearchBeacon() {
         if (mMinewBeaconManager != null) {
            mMinewBeaconManager.setDeviceManagerDelegateListener(mMinewBeaconManagerListener);
        }
     }


     // stop searchBeacon
    @ReactMethod
    public void onStopBeacon() {
        if (mMinewBeaconManager != null) {
            mMinewBeaconManager.setDeviceManagerDelegateListener(null);
            mMinewBeaconManager.stopScan();
            //stopBeaconComplete("ok");
        }
    }

    // stop ibeacon scan
    @ReactMethod
    private void stopScan() {
        mMinewBeaconManager.stopScan();
    }

    private void startBeaconComplete(String states) {

        // mWebView.loadUrl("javascript:backSDK.startBeacon.complete({errMsg:'startSearchBeacons:" + states + "'})");
    }


    private MinewBeaconManagerListener mMinewBeaconManagerListener =  new MinewBeaconManagerListener() {

        @Override
        public void onAppearBeacons(List<MinewBeacon> minewBeacons) {

        }

        @Override
        public void onDisappearBeacons(List<MinewBeacon> minewBeacons) {

        }

        @Override
        public void onRangeBeacons(final List<MinewBeacon> minewBeacons) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    updateBeacon(minewBeacons);
                }
            });
        }

        @Override
        public void onUpdateState(BluetoothState bluetoothState) {
        }
    };


    private void updateBeacon(List<MinewBeacon> minewBeacons) {
        if (minewBeacons == null || minewBeacons.size() == 0) {
            return;
        }
        mJsData.getBeacons().clear();
        for (MinewBeacon minewBeacon : minewBeacons) {
            Beacon beacon = Beacon.minewBeacon2Beacon(minewBeacon);
            if (beacon != null) {
                mJsData.getBeacons().add(beacon);
            }
        }
        if (mJsData.getBeacons().size() == 0) {
            return;
        }
        //String data = mGson.toJson(mJsData);
        // todo
        // searchBeaconComplete(data);
    }

}
