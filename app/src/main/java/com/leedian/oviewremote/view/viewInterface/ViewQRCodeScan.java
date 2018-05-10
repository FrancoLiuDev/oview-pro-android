package com.leedian.oviewremote.view.viewInterface;

import android.os.Bundle;
import android.support.annotation.NonNull;

import com.hannesdorfmann.mosby.mvp.viewstate.RestorableViewState;
import com.leedian.oviewremote.base.baseView.BaseMvpView;
import com.leedian.oviewremote.view.viewInterface.ViewCamera;

/**
 * Created by francoliu on 2017/2/7.
 */

public interface ViewQRCodeScan extends BaseMvpView {


    public void setCameraServerFound(String ssid ,String password);
}
