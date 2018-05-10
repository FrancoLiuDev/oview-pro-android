package com.leedian.oviewremote.presenter.presenterInterface;

import com.leedian.oviewremote.view.viewInterface.ViewCamera;
import com.leedian.oviewremote.view.viewInterface.ViewQRCodeScan;

/**
 * Created by franco liu on 2017/2/7.
 */

public interface QRCodeScanPresenter extends BasePresenter<ViewQRCodeScan> {

    void onScanQRCode(String code);

}
