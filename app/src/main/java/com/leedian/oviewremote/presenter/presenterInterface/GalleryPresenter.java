package com.leedian.oviewremote.presenter.presenterInterface;

import com.leedian.oviewremote.model.dataIn.CameraMetaModel;
import com.leedian.oviewremote.presenter.device.LightDirection;
import com.leedian.oviewremote.view.viewInterface.ViewCamera;
import com.leedian.oviewremote.view.viewInterface.ViewGallery;

/**
 * Created by franco liu on 2017/2/7.
 */

public interface GalleryPresenter extends BasePresenter<ViewGallery> {

    void startUploadFiles(String[] files, CameraMetaModel model);
}
