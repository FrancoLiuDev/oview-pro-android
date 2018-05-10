package com.leedian.oviewremote.presenter.presenterInterface;

import com.leedian.oviewremote.presenter.device.LightDirection;
import com.leedian.oviewremote.view.viewInterface.ViewCamera;

/**
 * Created by franco liu on 2017/2/7.
 */

public interface CameraPresenter extends BasePresenter<ViewCamera> {

    //void startSearchCamera();

    void onTackPicture();

    void onCameraActive();

    void onCameraDeActive();

    void onChangeCameraParamValue(String key, String value);

    void onAdjustDeviceColor(LightDirection direction, int R, int G , int B);

    void onChangeZoomPosition(String direction);

    void onPreviewTouchFocus(float xPosPercent, float yPosPercent);

    void onStopCapture();

    void onStopPreview();

    void onStartCapture(int cuts);

    void onStartOneCapture(int degree);


    void ConnectToPreviousWifiSSID();
}
