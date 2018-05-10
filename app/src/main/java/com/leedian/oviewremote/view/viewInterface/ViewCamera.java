package com.leedian.oviewremote.view.viewInterface;

import android.graphics.Bitmap;

import com.leedian.oviewremote.base.baseView.BaseMvpView;
import com.leedian.oviewremote.model.CameraShootingParam;

import java.util.ArrayList;

/**
 * Created by franco liu on 2017/2/7.
 */

public interface ViewCamera extends BaseMvpView {

    public void updateLiveViewBitmap(Bitmap bitmap);

    public void updateCameraParam(CameraShootingParam param);

    void updateCaptureBitmap(Bitmap captureBitmap);

    void updateCameraZoom(int zoom);

    void updateDownloadPercent(int total, int complete);

    void dismissDownloadPercent();

    void navigateShowResultActivity();

    void updateFNumberList(ArrayList camAvailableFNumber, String fNumber);
}
