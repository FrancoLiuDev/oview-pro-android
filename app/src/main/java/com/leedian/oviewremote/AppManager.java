package com.leedian.oviewremote;
import java.util.ArrayList;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Environment;
import android.os.StrictMode;
import android.util.Log;

import com.leedian.oviewremote.model.dataout.UserInfoModel;
import com.leedian.oviewremote.model.restapi.UrlMessagesConstants;
import com.leedian.oviewremote.presenter.task.taskImp.UserManagerImp;
import com.leedian.oviewremote.presenter.task.taskInterface.UserManager;
import com.leedian.oviewremote.presenter.wifi.RemoteWifiManager;
import com.leedian.oviewremote.presenter.wifi.WIfiListener;
import com.leedian.oviewremote.presenter.wifi.WifiHelp;
import com.leedian.oviewremote.utils.Pref.UrlPreferencesManager;

/**
 * AppManager
 *
 * @author Franco
 */
public class AppManager {
    static Context AppContext;
    static UrlPreferencesManager ServerIpPref;

    static void initApp(Context context) {

        //refactor
        AppContext = context;

        initPreferences();
        initWifiDetection();
        UrlMessagesConstants.init();
        UrlMessagesConstants.setStrHttpServiceRoot(ServerIpPref.getServerUrl());

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
    }

    static public String getContentDownloadDir() {

        if (!isDevelopFileFolderDebugMode()) {
            return AppContext.getFilesDir().getAbsolutePath() + "/download";
        } else {
            return Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_DOWNLOADS).getPath() + "/Oview";
        }
    }

    static public String getOviewInfoFileDir() {

        if (!isDevelopFileFolderDebugMode()) {
            return AppContext.getFilesDir().getAbsolutePath() + "/Info";
        } else {
            return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                              .getPath() + "/OviewInfo";
        }
    }

    static public String getOviewContentDir() {

        if (!isDevelopFileFolderDebugMode()) {
            return AppContext.getFilesDir().getAbsolutePath() + "/content";
        } else {
            return Environment
                           .getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS) + "/content";
        }
    }

    static public boolean isUserLogin() {

        UserManager userManageTask = new UserManagerImp();
        return userManageTask.isUserLogin();
    }

    static public void setUserLogout() {

        UserManager userManageTask = new UserManagerImp();
          userManageTask.setUserLogout();
    }
    static public UserInfoModel getUserLoginInfoData() {

        UserManager userManageTask = new UserManagerImp();
        return userManageTask.getUserLoginInfoData();
    }


    static public String getPrefAddress() {

        return ServerIpPref.getServerUrl();
    }

    static public void setPrefAddress(String ip) {

        ServerIpPref.setServerUrl(ip);
    }

    private static boolean isDevelopFileFolderDebugMode() {

        return true;
    }

    public static boolean isDevelopCameraOnlyDebugMode() {

        return true;
    }

    public static boolean isDevelopCameraCaptureOnlyMode() {

        return false;
    }

    private static void initPreferences() {

        ServerIpPref = new UrlPreferencesManager(AppContext);
        ServerIpPref.setChangeEvent(new UrlPreferencesManager.Notifier() {
            @Override
            public void onUrlChange(String data) {

                UrlMessagesConstants.setStrHttpServiceRoot(ServerIpPref.getServerUrl());
            }
        });
    }

    private static void initWifiDetection() {

        AppManager.registerWifiConnectionEvent();
    }

    static BroadcastReceiver receiver = null;

    static  ArrayList<WIfiListener >listeners = new ArrayList<>();

    public static void registerWifiEvent(WIfiListener listener){

        synchronized (listeners){
            listener.setConnected(false);
            listeners.add(listener);
        }

    }
    public static void unregisterWifiEvent(WIfiListener listener){

        synchronized (listeners){

            if (listeners.contains(listener))
               listeners.remove(listener);
        }

    }

    public static void registerWifiConnectionEvent() {

        IntentFilter filter = new IntentFilter();
        filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);

        receiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {

                NetworkInfo networkInfo =
                        intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);

                NetworkInfo.State state = networkInfo.getState();

                if (intent.getAction().equals(WifiManager.NETWORK_STATE_CHANGED_ACTION)) {

                    networkInfo.getState();
                    if (networkInfo.isConnected()) {
                        // Wifi is connected

                        final WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
                        final WifiInfo connectionInfo = wifiManager.getConnectionInfo();

                        String ssid = connectionInfo.getSSID();

                        synchronized (listeners){

                            for (int i = 0 ; i< listeners.size();i++) {
                                WIfiListener  listener = listeners.get(i);

                                if (ssid.contains(listener.getWifiName())) {
                                    listener.getEvent().onConnected();
                                    listener.setConnected(true);
                                }

                            }

                        }


                        Log.d("Inetify", "Wifi is connected: " + ssid);
                    }
                } else if (intent.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION)) {

                    if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI &&
                            !networkInfo.isConnected()) {

                        final WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
                        final WifiInfo connectionInfo = wifiManager.getConnectionInfo();
                        String ssid = connectionInfo.getSSID();

                        synchronized (listeners){

                            String strSSID =  WifiHelp.getCurrentWifiSSID(AppContext);

                            for (int i = 0 ; i< listeners.size();i++) {
                                WIfiListener  listener = listeners.get(i);

                                boolean dDiconnect1 = listener.isConnected();
                                boolean dDiconnect2 = ssid.equals(listener.getWifiName());

                                if (dDiconnect1 || dDiconnect2){
                                    listener.getEvent().onDisconnected();
                                    listener.setConnected(false);
                                }



                            }

                        }

                        Log.d("Inetify", "Wifi is disconnected: " + String.valueOf(networkInfo) + ssid);
                    }
                }
            }
        };

        AppContext.registerReceiver(receiver, filter);
    }
}
