package com.leedian.oviewremote.presenter.task.taskInterface;

import rx.Observable;
import com.leedian.oviewremote.model.dataIn.CameraMetaModel;

import com.leedian.oviewremote.utils.UploadListener;
import com.leedian.oviewremote.utils.subscript.ObservableActionIdentifier;

/**
 * Created by francoliu on 2017/4/12.
 */

public interface ROviewManager {

    Observable<ObservableActionIdentifier> executeUploadROviewInstance(String cover,String[] files, CameraMetaModel model);


}
