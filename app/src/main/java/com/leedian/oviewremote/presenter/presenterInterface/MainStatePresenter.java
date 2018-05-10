package com.leedian.oviewremote.presenter.presenterInterface;

import com.leedian.oviewremote.view.viewInterface.ViewCamera;
import com.leedian.oviewremote.view.viewInterface.ViewMainState;

/**
 * Created by franco liu on 2017/2/7.
 */

public interface MainStatePresenter extends BasePresenter<ViewMainState> {


    void connecWifiSSID(String SSID,String password);

    void startWatchWifiConnection();

    void ConnectToPrivusWifiSSID();

    void ConnectToOviewDevice();

    void onUserLogout();
}
