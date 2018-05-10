package com.leedian.oviewremote.presenter.wifi;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * UrlPreferencesManager
 *
 * @author Franco
 */
public class SSIDPreferencesManager {

    private static final String PREFERENCES_NAME = "CAMERA_SERVER_SSID";
    private static final String KEY_ID = "SSID";
    private static final String KEY_PASSWORD = "PASSWORD";

    private Notifier ChangeEvent;
    private SharedPreferences sharedPreferences;

    public SSIDPreferencesManager(Context context) {

        sharedPreferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
    }

    public void setChangeEvent(Notifier changeEvent) {

        ChangeEvent = changeEvent;
    }

    public String getServerInfomationSSID() {

        String ssid ;

        ssid =  sharedPreferences.getString(KEY_ID, "");

        return ssid;
    }

    public String getServerInfomationPassword() {

        String pwd ;

        pwd =  sharedPreferences.getString(KEY_PASSWORD, "");

        return pwd;
    }

    public void setServerUrl(String ssid ,String pwd ) {

        sharedPreferences.edit().putString(KEY_ID, ssid).apply();
        sharedPreferences.edit().putString(KEY_PASSWORD, pwd).apply();

       // ChangeEvent.onUrlChange(ip);
    }

    public interface Notifier {
        void onUrlChange(String data);
    }
}
