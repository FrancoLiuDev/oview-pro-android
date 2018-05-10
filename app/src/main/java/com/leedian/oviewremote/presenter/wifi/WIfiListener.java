package com.leedian.oviewremote.presenter.wifi;

/**
 * Created by francoliu on 2017/4/18.
 */

public class WIfiListener {

    public String getWifiName() {
        return wifiName;
    }

    public void setWifiName(String wifiName) {
        this.wifiName = wifiName;
    }

    String wifiName = null;

    public RemoteWifiManager.WifiConnectionEvent getEvent() {
        return event;
    }

    public void setEvent(RemoteWifiManager.WifiConnectionEvent event) {
        this.event = event;
    }

    RemoteWifiManager.WifiConnectionEvent event =null;

    public boolean isConnected() {
        return connected;
    }

    public void setConnected(boolean connected) {
        this.connected = connected;
    }

    boolean connected = false;



}
