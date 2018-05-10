package com.leedian.oviewremote;
import android.app.Application;
import android.content.Context;

/**
 * OviewApp
 *
 * @author Franco
 */
public class OviewCameraApp
        extends Application
{
    private static Context context;

    public static Context getAppContext() {

        return OviewCameraApp.context;
    }

    @Override
    public void onCreate() {

        super.onCreate();
        OviewCameraApp.context = getApplicationContext();
        AppManager.initApp(getApplicationContext());
    }
}

