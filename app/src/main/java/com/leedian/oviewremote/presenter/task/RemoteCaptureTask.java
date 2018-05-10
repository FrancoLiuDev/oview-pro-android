package com.leedian.oviewremote.presenter.task;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import com.leedian.oviewremote.OviewCameraApp;
import com.leedian.oviewremote.presenter.camera.CameraManager;
import com.leedian.oviewremote.presenter.device.OviewDeviceManager;
import com.leedian.oviewremote.presenter.wifi.RemoteWifiTask;
import com.leedian.oviewremote.utils.subscript.ObservableActionIdentifier;
import com.leedian.oviewremote.utils.subscript.ObservableCameraAction;

/**
 * Created by francoliu on 2017/3/28.
 */

public class RemoteCaptureTask {

      protected CameraManager mCameraManager = null;

      protected OviewDeviceManager mDeviceManager = null ;

      public RemoteCaptureTask(CameraManager camera,OviewDeviceManager device){

          mCameraManager = camera;
          mDeviceManager = device;
      }


}
