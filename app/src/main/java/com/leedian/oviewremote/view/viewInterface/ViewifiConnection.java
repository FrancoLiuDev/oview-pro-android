package com.leedian.oviewremote.view.viewInterface;

import android.graphics.Bitmap;

import com.leedian.oviewremote.base.baseView.BaseMvpView;
import com.leedian.oviewremote.model.CameraShootingParam;

/**
 * Created by franco liu on 2017/2/7.
 */

public interface ViewifiConnection extends BaseMvpView {

    public void updateLiveViewBitmap(Bitmap bitmap);

    public void updateCameraParam(CameraShootingParam param);

}
