package com.leedian.oviewremote.presenter.presenterImp;

import com.leedian.oviewremote.base.basePresenter.CameraBasePresenter;
import com.leedian.oviewremote.presenter.presenterInterface.QRCodeScanPresenter;
import com.leedian.oviewremote.presenter.wifi.RemoteWifiTask;
import com.leedian.oviewremote.view.viewInterface.ViewQRCodeScan;

/**
 * Created by franco liu on 2017/2/7.
 */

public class QRCodeScanPresenterImp extends CameraBasePresenter<ViewQRCodeScan>
        implements QRCodeScanPresenter {

    @Override
    public void onScanQRCode(String code) {

        if (RemoteWifiTask.isCameraQRCodeAvalible(code)) {

            String[] ssid = RemoteWifiTask.gerCameraQRCodeSSIDPassword(code);

            setWifiSettingInfo(ssid[0], ssid[1]);
            
            getView().setCameraServerFound(ssid[0], ssid[1]);
        }
    }
}
