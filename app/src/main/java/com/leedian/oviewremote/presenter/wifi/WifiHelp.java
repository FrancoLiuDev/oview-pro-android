package com.leedian.oviewremote.presenter.wifi;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;

import static android.content.Context.WIFI_SERVICE;

/**
 * Created by francoliu on 2017/4/18.
 */

public class WifiHelp {

    static public String getCurrentWifiSSID(Context context) {

        android.net.wifi.WifiManager wifiManager = (android.net.wifi.WifiManager) context.getSystemService(WIFI_SERVICE);
        ConnectivityManager connectionManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);


        WifiInfo connectionInfo = wifiManager.getConnectionInfo();

        NetworkInfo wifiCheck = connectionManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);


        if  (wifiCheck.isConnected())
            return connectionInfo.getSSID();


        return "";
    }

}
