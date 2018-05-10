package com.leedian.oviewremote.presenter.wifi;

import java.io.IOException;
import java.util.List;

import android.content.Context;
import android.net.wifi.WifiConfiguration;

import org.json.JSONException;

import rx.Observable;
import rx.Subscriber;
import com.leedian.oviewremote.model.CameraShootingParam;
import com.leedian.oviewremote.utils.subscript.ObservableActionIdentifier;
import com.leedian.oviewremote.utils.subscript.ObservableCameraAction;

/**
 * Created by franco on 28/02/2017.
 */

public class RemoteWifiTask {

    Context context;
    RemoteWifiManager wifiManager;

    public RemoteWifiTask(Context context) {
        this.context = context ;

        wifiManager =  new RemoteWifiManager(this.context);

    }

    public static boolean isCameraQRCodeAvalible(String code){


        if (code.indexOf("8P") == 0 ){
            return true;
        }

        if (code.contains("ILCE-7") ){
            return true;
        }

        if (code.contains("ILCE-5100") ){
            return true;
        }

        return false;
    }

    public static String[] gerCameraQRCodeSSIDPassword(String code){


        String[] ssid = new String[2];


        String id="";
        String password="";

        if (code.indexOf("8P") == 0 )
        {
            id = "DIRECT-8PQ0:DSC-QX100";
            password = code.substring(2);
        }

         //W01:S:QAE0;P:2ySqdkYQ;C:ILCE-7;M:7A4B87AC4A46;
        if (code.contains("ILCE-7"))
        {
            //id = "DIRECT-QAE0:ILCE-7";
            id = "DIRECT-" + findHeaderStringAndData(code,"S:")+ ":"
                  +"ILCE-7";
            password = findHeaderStringAndData(code,"P:");
        }

        if (code.contains("ILCE-5100"))
        {
            //id = "DIRECT-QAE0:ILCE-7";
            id = "DIRECT-" + findHeaderStringAndData(code,"S:")+ ":"
                    +"ILCE-5100";
            password = findHeaderStringAndData(code,"P:");
        }

        ssid[0] = id;
        ssid[1] = password;
        return ssid;

    }

    public String checkIsAvalibleCameraConnected(){

        String str = wifiManager.getCurrentWifiSSID();

        if (str.contains("DSC-QX100") ){
            return str;
        }

        if (str.contains("ILCE-7") ){
            return str;
        }

        if (str.contains("ILCE-5100") ){
            return str;
        }


        return "";

    }


    public static String findHeaderStringAndData(String str ,String head){

        String temp;

        String data="";

        int headIndex = str.indexOf(head);

        temp  = str.substring(headIndex+2);

        data = findStringStopAt(temp,";");


        return data;
    }

    public static String findStringStopAt(String str ,String tag ){

        String temp;

        int endIndex = str.indexOf(tag);

        temp = str.substring(0,endIndex);

        return temp;
    }

    public boolean checkIfCameraConnect(final String networkSSID) {

        return  wifiManager.checkIsWifiConnected(networkSSID);

    }

    public  void connectToCameraSSID(final String networkSSID , final String password ) {

        wifiManager.connectToCameraSSID(networkSSID, password);

    }

    public  void startWatcWifiSates(WIfiListener listener) {

        wifiManager.startWatchConnectingEvent(listener);

    }

    public  void stopWatcWifiSates(WIfiListener listener) {

        wifiManager.stopWatchConnectingEvent(listener);

    }

    public  void disConnectCameraServer(String ssid) {

        wifiManager.disConnectWifi(ssid);

    }
}
