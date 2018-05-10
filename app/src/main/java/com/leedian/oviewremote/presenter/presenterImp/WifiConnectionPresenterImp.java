package com.leedian.oviewremote.presenter.presenterImp;

import android.graphics.Bitmap;
import android.util.Log;

import com.leedian.oviewremote.base.basePresenter.CameraBasePresenter;
import com.leedian.oviewremote.base.baseView.ActivityUIUpdate;
import com.leedian.oviewremote.base.baseView.BaseMvpView;
import com.leedian.oviewremote.model.CameraShootingParam;
import com.leedian.oviewremote.presenter.camera.CameraEventObserver;
import com.leedian.oviewremote.presenter.camera.CameraManager;
import com.leedian.oviewremote.presenter.camera.CameraRemoteApi;
import com.leedian.oviewremote.presenter.camera.CameraSsdpClient;
import com.leedian.oviewremote.presenter.camera.ServerDevice;
import com.leedian.oviewremote.presenter.presenterInterface.CameraPresenter;
import com.leedian.oviewremote.presenter.presenterInterface.WifiConnectionPresenter;
import com.leedian.oviewremote.utils.subscript.ObservableActionIdentifier;
import com.leedian.oviewremote.utils.thread.JobExecutor;
import com.leedian.oviewremote.utils.thread.TaskExecutor;
import com.leedian.oviewremote.utils.thread.UIThread;
import com.leedian.oviewremote.view.viewInterface.ViewCamera;
import com.leedian.oviewremote.view.viewInterface.ViewifiConnection;

import java.util.List;

import rx.Subscriber;

/**
 * Created by franco liu on 2017/2/7.
 */

public class WifiConnectionPresenterImp extends CameraBasePresenter<ViewifiConnection>
        implements WifiConnectionPresenter {



}
