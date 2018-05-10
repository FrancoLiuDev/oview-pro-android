package com.leedian.oviewremote.presenter.wifi;

import java.util.List;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.SystemClock;
import android.util.Log;

import com.leedian.oviewremote.AppManager;
import static android.content.Context.WIFI_SERVICE;

/**
 * Created by francoliu on 2017/3/1.
 */

public class RemoteWifiManager {

    public static final String WIFI_AUTH_OPEN = "";
    public static final String WIFI_AUTH_ROAM = "[ESS]";
    private static final int WIFI_NO_PASSWORD = 1;
    boolean bWifiConnected = false;
    private Context context;


    public RemoteWifiManager(Context context) {
        this.context = context;
    }

    public void connectToCameraSSID(String networkSSID, String password) {

        android.net.wifi.WifiManager wifiManager = (android.net.wifi.WifiManager) this.context.getSystemService(WIFI_SERVICE);

        WifiConfiguration config = getWifiConfiguration(networkSSID);

        if (config == null) {

            WifiConfiguration conf = new WifiConfiguration();
            conf.SSID = "\"" + networkSSID + "\"";
            conf.preSharedKey = "\"" + password + "\"";

            wifiManager.addNetwork(conf);

            config = getWifiConfiguration(networkSSID);
        }

        bWifiConnected = false;


        wifiManager.disconnect();
        wifiManager.enableNetwork(config.networkId, true);
        wifiManager.reconnect();
    }

    public void disConnectWifi(String networkSSID) {

        android.net.wifi.WifiManager wifiManager = (android.net.wifi.WifiManager) this.context.getSystemService(WIFI_SERVICE);

        WifiInfo wifiInfo = wifiManager.getConnectionInfo();

        if (wifiInfo == null)
            return;

        if (wifiInfo.getSSID().contains(networkSSID)) {
            wifiManager.disconnect();
        }
    }

    private void startScanCamera() {

        final WifiManager mWifiManager = (WifiManager) context.getSystemService(WIFI_SERVICE);
        final String TAG = "LOG";

        if (mWifiManager.getWifiState() == WifiManager.WIFI_STATE_ENABLED) {

            // register WiFi scan results receiver
            IntentFilter filter = new IntentFilter();
            filter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);

            context.registerReceiver(new BroadcastReceiver() {

                @Override
                public void onReceive(Context context, Intent intent) {

                    List<ScanResult> results = mWifiManager.getScanResults();
                    final int N = results.size();

                    Log.v(TAG, "Wi-Fi Scan Results ... Count:" + N);
                    for (int i = 0; i < N; ++i) {

                        ScanResult result = results.get(i);

                        String ssid;

                        boolean isPasspointNetwork;

                        WifiInfo wifiInfo = mWifiManager.getConnectionInfo();

                        ssid = results.get(i).SSID;

                        Log.v(TAG, "  BSSID       =" + result.BSSID);
                        Log.v(TAG, "  SSID        =" + result.SSID);
                        Log.v(TAG, "  Capabilities=" + result.capabilities);
                        Log.v(TAG, "  Frequency   =" + result.frequency);
                        Log.v(TAG, "  Level       =" + result.level);

                        Log.v(TAG, "---------------");
                    }
                }
            }, filter);

            mWifiManager.startScan();
        }
    }

    public void startWatchConnectingEvent(WIfiListener listener) {

        registerWifiConnectionEvent(listener);

       /* new Thread() {

            @Override
            public void run() {

                long start = SystemClock.uptimeMillis();
                long time = SystemClock.uptimeMillis();

                while ((time - start) < (1000 * 8)) {

                    try {
                        Thread.sleep(1);
                        time = SystemClock.uptimeMillis();

                        if (bWifiConnected) {
                            break;
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                if (!bWifiConnected) {
                   // event.onConnectionErrorFinish();
                }
            }
        }.start();*/
    }

    public void stopWatchConnectingEvent(WIfiListener listener) {

        unRegisterWifiConnectionEvent(listener);
    }

    private void startWatchEvent(WIfiListener listener) {

        registerWifiConnectionEvent(listener);
    }

    private void registerWifiConnectionEvent(WIfiListener listener) {

        IntentFilter filter = new IntentFilter();
        filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);


        AppManager.registerWifiEvent(listener);

    }

    private void unRegisterWifiConnectionEvent(WIfiListener listener) {

        AppManager.unregisterWifiEvent(listener);
    }

    private WifiConfiguration getWifiConfiguration(String networkSSID) {

        WifiConfiguration config = null;

        android.net.wifi.WifiManager wifiManager = (android.net.wifi.WifiManager) this.context.getSystemService(WIFI_SERVICE);

        List<WifiConfiguration> list = wifiManager.getConfiguredNetworks();

        for (WifiConfiguration i : list) {
            if (i.SSID != null && i.SSID.equals("\"" + networkSSID + "\"")) {

                config = i;
                break;
            }
        }

        return config;
    }

    public boolean checkIsWifiConnected(String networkSSID) {

        android.net.wifi.WifiManager wifiManager = (android.net.wifi.WifiManager) this.context.getSystemService(WIFI_SERVICE);
        ConnectivityManager connectionManager = (ConnectivityManager) this.context.getSystemService(Context.CONNECTIVITY_SERVICE);


        WifiInfo connectionInfo = wifiManager.getConnectionInfo();

        NetworkInfo wifiCheck = connectionManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        if (!connectionInfo.getSSID().contains(networkSSID)){
            return  false;
        }

        return wifiCheck.isConnected();
    }

    public String getCurrentWifiSSID() {

        android.net.wifi.WifiManager wifiManager = (android.net.wifi.WifiManager) this.context.getSystemService(WIFI_SERVICE);
        ConnectivityManager connectionManager = (ConnectivityManager) this.context.getSystemService(Context.CONNECTIVITY_SERVICE);


        WifiInfo connectionInfo = wifiManager.getConnectionInfo();

        NetworkInfo wifiCheck = connectionManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);


        if  (wifiCheck.isConnected())
            return connectionInfo.getSSID();


        return "";
    }

    public interface WifiConnectionEvent {

        void onConnected();

        void onDisconnected();

        void onConnectionErrorFinish();
    }
}
