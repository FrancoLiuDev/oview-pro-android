package com.leedian.oviewremote.presenter.presenterImp;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.SystemClock;
import android.util.Log;

import rx.Subscriber;
import com.leedian.oviewremote.AppManager;
import com.leedian.oviewremote.AppResource;
import com.leedian.oviewremote.R;
import com.leedian.oviewremote.base.basePresenter.CameraBasePresenter;
import com.leedian.oviewremote.base.baseView.ActivityUIUpdate;
import com.leedian.oviewremote.base.baseView.BaseMvpView;
import com.leedian.oviewremote.model.CameraShootingParam;
import com.leedian.oviewremote.presenter.camera.CameraEventObserver;
import com.leedian.oviewremote.presenter.camera.CameraManager;
import com.leedian.oviewremote.presenter.camera.CameraSsdpClient;
import com.leedian.oviewremote.presenter.camera.ServerDevice;
import com.leedian.oviewremote.presenter.device.LightDirection;
import com.leedian.oviewremote.presenter.device.OviewDeviceManager;
import com.leedian.oviewremote.presenter.presenterInterface.CameraPresenter;
import com.leedian.oviewremote.presenter.wifi.RemoteWifiManager;
import com.leedian.oviewremote.presenter.wifi.WIfiListener;
import com.leedian.oviewremote.utils.subscript.ObservableActionIdentifier;
import com.leedian.oviewremote.utils.subscript.ObservableCameraAction;
import com.leedian.oviewremote.utils.thread.JobExecutor;
import com.leedian.oviewremote.utils.thread.TaskExecutor;
import com.leedian.oviewremote.utils.thread.UIThread;
import com.leedian.oviewremote.view.viewInterface.ViewCamera;
import static com.blankj.utilcode.utils.FileUtils.createOrExistsFile;
import static com.blankj.utilcode.utils.FileUtils.deleteDir;

/**
 * Camera Presenter
 */

public class CameraPresenterImp extends CameraBasePresenter<ViewCamera>
        implements CameraPresenter, CameraManager.LiveViewListener {

    private boolean connected = false;
    private Subscriber<ObservableActionIdentifier> captureSubscriber = null;
    private int corePoolSize = 1;
    private int maxPoolSize = 3;
    private long keepAliveTime = 60L;
    private ThreadPoolExecutor executor = new ThreadPoolExecutor(corePoolSize,
            maxPoolSize,
            keepAliveTime,
            TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>()
    );
    private int totalImg = 0;
    private int receiveImp = 0;
    private WIfiListener listenerSearchCamera = new WIfiListener();
    private WIfiListener listenerConnect = new WIfiListener();
    private OviewDeviceManager.DeviceServiceEvent event = null;
    private boolean isShooting = false;
    private Thread captureThread;
    private CameraSsdpClient mSsdpClient = new CameraSsdpClient();
    private ServerDevice mCameraDevice = null;
    private CameraShootingParam camCurrentParam;
    private CameraEventObserver.ChangeListener mEventListener = new CameraEventObserver.ChangeListenerImp() {

        @Override
        public void onShootModeChanged(String shootMode) {

            Log.d("onShootModeChanged", shootMode);
        }

        @Override
        public void onCameraStatusChanged(String status) {
            Log.d("onCameraStatusChanged", status);
        }

        @Override
        public void onApiListModified(List<String> apis) {
            Log.d("onApiListModified", "onApiListModified");
        }

        @Override
        public void onZoomPositionChanged(int zoomPosition) {

            getUIUpdateInstance(getView()).updateCameraZoomValue(zoomPosition);
            Log.d("onZoomPositionChanged", "onZoomPositionChanged");
        }

        @Override
        public void onLiveViewStatusChanged(boolean status) {
            Log.d("onLiveViewStatusChanged", "onLiveViewStatusChanged");
        }

        @Override
        public void onStorageIdChanged(String storageId) {
            Log.d("onStorageIdChanged", "onStorageIdChanged");
        }

        @Override
        public void onCameraFocused() {
            onReleaseTouchFocus();
        }

        @Override
        public void onfNumberListChanged(ArrayList fNumberList, String fNumber) {
            getUIUpdateInstance(getView()).updateViewFnumberList(fNumberList, fNumber);
        }
    };

    public CameraPresenterImp() {
        mCameraManager.setLiveviewListener(this);
        mCameraManager.setmEventListener(mEventListener);
    }

    private static LiveViewUIUpdate getUIUpdateInstance(ViewCamera view) {
        return new LiveViewUIUpdate(view);
    }

    private void startSearchCamera() {

        if (!AppManager.isDevelopCameraOnlyDebugMode()) {

            mDeviceManager.registerEventListner(createDeviceEvent());

            if (!mDeviceManager.isConnected()) {
                mDeviceManager.connectToOviewDevice();
            }
        }

        if (connected) {

            getUIUpdateInstance(getView()).showLoadingDialogMsg(AppResource.getString(R.string.camera_loading));
            mCameraManager.startPreview();
            getUIUpdateInstance(getView()).hideLoadingDialog();
            return;
        }

        listenerSearchCamera.setWifiName(wifiSSID);
        listenerSearchCamera.setEvent(new RemoteWifiManager.WifiConnectionEvent() {

            @Override
            public void onConnected() {
                connected = true;
            }

            @Override
            public void onDisconnected() {
                connected = false;
                getUIUpdateInstance(getView()).setViewAsInvalid();
            }

            @Override
            public void onConnectionErrorFinish() {
                connected = false;
            }
        });

        mRemoteWifiTask.startWatcWifiSates(listenerSearchCamera);

        getUIUpdateInstance(getView()).showLoadingDialogMsg(AppResource.getString(R.string.camera_loading));

        mSsdpClient.search(new CameraSsdpClient.SearchResultHandler() {

            @Override
            public void onDeviceFound(final ServerDevice device) {
                // Called by non-UI thread.
                mCameraDevice = device;
            }

            @Override
            public void onFinished() {

                mCameraManager.setmCameraDevice(mCameraDevice);

                if (mCameraManager.initCamera()) {
                    mCameraManager.startPreview();
                }

                connected = true;
                getUIUpdateInstance(getView()).hideLoadingDialog();
            }

            @Override
            public void onErrorFinished() {
                // Called by non-UI thread.
                getUIUpdateInstance(getView()).showErrorMessage(AppResource.getString(R.string.camera_loading＿error));

                connected = false;
            }
        });
    }

    private void connectWifiSSID(String SSID, String password) {

        wifiSSID = SSID;

        getView().showLoadingDialog();

        mRemoteWifiTask.stopWatcWifiSates(listenerConnect);

        listenerConnect.setWifiName(wifiSSID);
        listenerConnect.setEvent(new RemoteWifiManager.WifiConnectionEvent() {

            @Override
            public void onConnected() {
                connected = true;
                getUIUpdateInstance(getView()).hideLoadingDialog();

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                startSearchCamera();
            }

            @Override
            public void onDisconnected() {
                connected = false;
                getUIUpdateInstance(getView()).hideLoadingDialog();
                getUIUpdateInstance(getView()).setViewAsInvalid();
            }

            @Override
            public void onConnectionErrorFinish() {
                getUIUpdateInstance(getView()).hideLoadingDialog();
            }
        });

        mRemoteWifiTask.startWatcWifiSates(listenerConnect);

        if (mRemoteWifiTask.checkIfCameraConnect(SSID)) {

            listenerConnect.setConnected(true);
            getUIUpdateInstance(getView()).hideLoadingDialog();
            startSearchCamera();
        } else {
            mRemoteWifiTask.connectToCameraSSID(SSID, password);
        }
    }

    private OviewDeviceManager.DeviceServiceEvent createDeviceEvent() {

        releaseDeviceEvent();

        event = new OviewDeviceManager.DeviceServiceEvent() {

            @Override
            public void disconnect() {

                mDeviceManager.disconnectToOviewDevice();
                detachRelease();
                getUIUpdateInstance(getView()).setViewAsInvalid();
            }

            @Override
            public void connect() {

            }
        };

        return event;
    }

    private void releaseDeviceEvent() {

        if (event != null) {
            mDeviceManager.unregisterEventListner(event);
            event = null;
        }
    }

    @Override
    public void ConnectToPreviousWifiSSID() {

        if (!AppManager.isDevelopCameraOnlyDebugMode()) {

            mDeviceManager.registerEventListner(createDeviceEvent());

            if (!mDeviceManager.isConnected()) {
                mDeviceManager.connectToOviewDevice();
            }
        }

        String ssid = mRemoteWifiTask.checkIsAvalibleCameraConnected();
        String pwd;

        if (ssid.length() != 0) {

            connectWifiSSID(ssid, "");
            return;
        }

        ssid = getWifiSettingInfoSSID();
        pwd = getWifiSettingInfoPassword();

        if (ssid.length() < 1) {
            return;
        }

        connectWifiSSID(ssid, pwd);
    }

    @Override
    public void detachView(boolean retainInstance) {
        super.detachView(retainInstance);
        detachRelease();
    }

    private void detachRelease() {

        Log.d("detachRelease", "detachRelease");
        isShooting = false;

        watchTreadStop();

        if (captureSubscriber != null) {
            captureSubscriber.unsubscribe();
        }

        mCameraManager.stopPreview();
        mRemoteWifiTask.stopWatcWifiSates(listenerSearchCamera);
        mRemoteWifiTask.stopWatcWifiSates(listenerConnect);
    }

    private void fetchCameraPram() {

        Subscriber<ObservableActionIdentifier> subscriber = new Subscriber<ObservableActionIdentifier>() {
            @Override
            public void onStart() {
                getUIUpdateInstance(getView()).showLoadingDialog();
                request(1);
            }

            @Override
            public void onCompleted() {
                getUIUpdateInstance(getView()).hideLoadingDialog();
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(ObservableActionIdentifier observableActionIdentifier) {

                camCurrentParam = (CameraShootingParam) observableActionIdentifier.getObject();

                getUIUpdateInstance(getView()).updateViewCameraParam(camCurrentParam);
                //observableActionIdentifier.getAction() == ObservableCameraAction.ACTION_GET_CAMERPARM
            }
        };

        TaskExecutor taskCase = new TaskExecutor(new JobExecutor(), new UIThread(), mCameraManager
                .executeGetCameraParm());
        taskCase.execute(subscriber);
    }

    @Override
    public void onTackPicture() {

    }

    @Override
    public void onChangeCameraParamValue(String key, String value) {

        Subscriber<ObservableActionIdentifier> subscriber = new Subscriber<ObservableActionIdentifier>() {
            @Override
            public void onStart() {
                getUIUpdateInstance(getView()).showLoadingDialog();
                request(1);
            }

            @Override
            public void onCompleted() {
                getUIUpdateInstance(getView()).hideLoadingDialog();
                fetchCameraPram();
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(ObservableActionIdentifier observableActionIdentifier) {

            }
        };

        TaskExecutor taskCase = new TaskExecutor(new JobExecutor(), new UIThread(), mCameraManager
                .executeChangeCameraValue(key, value));
        taskCase.execute(subscriber);
    }

    @Override
    public void onStartCapture(final int cuts) {

        final ArrayList<String> images = new ArrayList();

        captureSubscriber = new Subscriber<ObservableActionIdentifier>() {
            @Override
            public void onStart() {
                deleteDir(AppManager.getOviewContentDir());

                isShooting = true;
                totalImg = cuts;
                receiveImp = 0;
                watchTreadStart();
            }

            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(ObservableActionIdentifier observableActionIdentifier) {

                if (!isShooting)
                    return;

                if (observableActionIdentifier.getAction().equals(ObservableCameraAction.ACTION_TAKE_PICTURE) ) {
                    int cnt = (int) observableActionIdentifier.getObject();
                    String url = observableActionIdentifier.getContent();
                    images.add(url);
                    saveUrlToBitmap(cnt, url);
                }
            }
        };

        TaskExecutor taskCase = new TaskExecutor(new JobExecutor(), new UIThread(), mCameraManager
                .executeCameraCapture(cuts, mDeviceManager));

        taskCase.execute(captureSubscriber);
    }

    @Override
    public void onStartOneCapture(int degree) {

        final ArrayList<String> stringBitmapList = new ArrayList();

        captureSubscriber = new Subscriber<ObservableActionIdentifier>() {
            @Override
            public void onStart() {
                deleteDir(AppManager.getOviewContentDir());
                isShooting = true;
                totalImg = 1;
                receiveImp = 0;
                watchTreadStart();
            }

            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                watchTreadStop();
            }

            @Override
            public void onNext(ObservableActionIdentifier observableActionIdentifier) {

                if (!isShooting)
                    return;

                if (observableActionIdentifier.getAction().equals(ObservableCameraAction.ACTION_TAKE_PICTURE)  ) {
                    int cnt = (int) observableActionIdentifier.getObject();
                    String url = observableActionIdentifier.getContent();
                    stringBitmapList.add(url);
                    saveUrlToBitmap(cnt, url);
                }
            }
        };

        TaskExecutor taskCase = new TaskExecutor(new JobExecutor(), new UIThread(), mCameraManager
                .executeCameraCaptureWithDegree(degree, mDeviceManager));

        taskCase.execute(captureSubscriber);
    }

    private void saveUrlToBitmap(final int cnt, final String url) {

        executor.allowCoreThreadTimeOut(false);
        executor.execute(new Runnable() {

                             @Override
                             public void run() {
                                 //executor.getActiveCount()
                                 long submitted = executor.getTaskCount();
                                 long completed = executor.getCompletedTaskCount();
                                 String strSubmitted = Long.toString(submitted);
                                 String strCompleted = Long.toString(completed);

                                 Log.d("new thread :", strSubmitted + ":" + strCompleted);
                                 runSaveBitmap(cnt, url);
                             }
                         }
        );
    }

    private void watchTreadStop() {

        if (captureThread != null) {
            captureThread.interrupt();
            captureThread = null;
        }
    }

    private void watchTreadStart() {

        getUIUpdateInstance(getView()).showProgressDialog(totalImg, receiveImp);

        captureThread = new Thread(new Runnable() {

            @Override
            public void run() {

                while (true) {

                    try {
                        Thread.sleep(5000);

                        Log.d("isShooting", String.valueOf(isShooting));
                        if (!isShooting) {
                            return;
                        }

                        getUIUpdateInstance(getView()).showProgressDialog(totalImg, receiveImp);

                        if (totalImg == receiveImp) {
                            getUIUpdateInstance(getView()).dismissProgressDialog();
                            getUIUpdateInstance(getView()).navigateShowCaptureResultActivity();

                            watchTreadStop();
                            break;
                        }
                    } catch (InterruptedException e) {
                        getUIUpdateInstance(getView()).dismissProgressDialog();
                        e.printStackTrace();
                        return;
                    }
                }
            }
        });

        captureThread.start();
    }

    private void runSaveBitmap(final int cnt, final String url) {

        long tick_time = SystemClock.uptimeMillis();
        String strLong = Long.toString(tick_time);

        Log.d("getCameraPhotoByUrl", strLong);

        Bitmap bmp;

        if (!isShooting) {
            return;
        }

        if (AppManager.isDevelopCameraCaptureOnlyMode()) {
            int w = 1024, h = 960;

            Bitmap.Config conf = Bitmap.Config.ARGB_8888;
            bmp = Bitmap.createBitmap(w, h, conf);

        } else {

            try {
                bmp = getCameraPhotoByUrl(url);
            } catch (IOException e) {

                saveUrlToBitmap(cnt, url);
                return;
            }

            if (bmp == null) {
                saveUrlToBitmap(cnt, url);
                return;
            }
        }

        FileOutputStream out;

        String filename = String.format("%03d", cnt);

        createOrExistsFile(AppManager.getOviewContentDir() + "/" + filename + ".jpg");

        try {

            out = new FileOutputStream(AppManager.getOviewContentDir() + "/" + filename + ".jpg");

            if (out == null) {
                getUIUpdateInstance(getView()).showErrorMessage("out");
                getUIUpdateInstance(getView()).hideLoadingDialog();
                return;
            }

            String strCount = Integer.toString(cnt);
            Log.d("FileOutputStream", strCount);

            bmp.compress(Bitmap.CompressFormat.JPEG, 65, out); // bmp is your Bitmap instance
            bmp.recycle();
            out.close();
            receiveImp++;

            if (receiveImp == totalImg)
                getUIUpdateInstance(getView()).hideLoadingDialog();
        } catch (IOException e) {
            getUIUpdateInstance(getView()).hideLoadingDialog();
            e.printStackTrace();
        }
    }

    public Bitmap getCameraPhotoByUrl(final String strUrl) throws IOException {

        Bitmap bmp = null;

        URL url = null;
        try {

            url = new URL(strUrl);
            URLConnection conn = url.openConnection();

            conn.setConnectTimeout(20000);
            conn.setReadTimeout(20000);

            InputStream istream = new BufferedInputStream(conn.getInputStream());
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 1; // irresponsible value
            options.inDensity = 100;

            bmp = BitmapFactory.decodeStream(istream, null, options);

            istream.close();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return bmp;
    }

    @Override
    public void onStopCapture() {

        captureSubscriber.unsubscribe();

        while (!captureSubscriber.isUnsubscribed()) {

        }

        watchTreadStop();
        getUIUpdateInstance(getView()).dismissProgressDialog();
    }

    @Override
    public void onStopPreview() {
        mCameraManager.stopPreview();
        mRemoteWifiTask.stopWatcWifiSates(listenerSearchCamera);
        mRemoteWifiTask.stopWatcWifiSates(listenerConnect);
    }

    @Override
    public void onAdjustDeviceColor(LightDirection direction, int R, int G, int B) {

        mDeviceManager.setColorRGB(direction, R, G, B);
    }

    @Override
    public void onChangeZoomPosition(String direction) {

        Subscriber<ObservableActionIdentifier> subscriber = new Subscriber<ObservableActionIdentifier>() {
            @Override
            public void onStart() {

                getUIUpdateInstance(getView()).showLoadingDialog();
                request(1);
            }

            @Override
            public void onCompleted() {
                getUIUpdateInstance(getView()).hideLoadingDialog();
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(ObservableActionIdentifier observableActionIdentifier) {

            }
        };

        TaskExecutor taskCase = new TaskExecutor(new JobExecutor(), new UIThread(), mCameraManager
                .executeChangeCameraZooom(direction));
        taskCase.execute(subscriber);
    }

    @Override
    public void onPreviewTouchFocus(float xPosPercent, float yPosPercent) {

        Subscriber<ObservableActionIdentifier> subscriber = new Subscriber<ObservableActionIdentifier>() {
            @Override
            public void onStart() {

                getUIUpdateInstance(getView()).showLoadingDialog();
                request(1);
            }

            @Override
            public void onCompleted() {

                getUIUpdateInstance(getView()).hideLoadingDialog();
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(ObservableActionIdentifier observableActionIdentifier) {

            }
        };

        TaskExecutor taskCase = new TaskExecutor(new JobExecutor(), new UIThread(), mCameraManager
                .executeCameraSetFocus(xPosPercent, yPosPercent));
        taskCase.execute(subscriber);
    }

    private void onReleaseTouchFocus() {

        Subscriber<ObservableActionIdentifier> subscriber = new Subscriber<ObservableActionIdentifier>() {
            @Override
            public void onStart() {

                request(1);
            }

            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(ObservableActionIdentifier observableActionIdentifier) {

            }
        };

        TaskExecutor taskCase = new TaskExecutor(new JobExecutor(), new UIThread(), mCameraManager
                .executeReleaseTouchFocus());
        taskCase.execute(subscriber);
    }

    @Override
    public void onLiveImageRendering(Bitmap data) {

        getUIUpdateInstance(getView()).renderGalleryPresenterLiveViewBitmap(data);
    }

    @Override
    public void onConnectSuccess() {
        fetchCameraPram();
    }

    @Override
    public void onError(StreamErrorReason reason) {

        mCameraManager.stopPreview();
    }

    @Override
    public void onCameraActive() {

        if (mCameraManager != null)
            mCameraManager.startPreview();
    }

    @Override
    public void onCameraDeActive() {
        if (mCameraManager != null) {

            mCameraManager.stopPreview();
        }
    }

    private static class LiveViewUIUpdate
            extends ActivityUIUpdate<ViewCamera> {
        final static public int UI_UPDATE_LIVE_BITMAP = 1;
        final static public int UI_UPDATE_CAM_PARAM = 2;
        final static public int UI_UPDATE_CAPTURE_PICTURE = 3;
        final static public int UI_UPDATE_CAMERA_ZOOM = 4;
        final static public int UI_UPDATE_PROGRESS_PERCENT = 5;
        final static public int UI_UPDATE_PROGRESS_DISMISS = 6;
        final static public int UI_NAVIGATE_SHOW_RESULT = 7;
        final static public int UI_UPDATE_FNUMBER_LIST = 8;
        Bitmap liveViewBitmap;
        Bitmap captureBitmap;
        int zoom;
        int total;
        int receive;
        CameraShootingParam camParam;
        String fNumber;
        ArrayList camAvailableFNumber;

        public LiveViewUIUpdate(ViewCamera view) {
            super(view);
        }

        public void renderGalleryPresenterLiveViewBitmap(Bitmap bitmap) {

            ViewCamera view = getView();

            if (view == null) {
                return;
            }

            setUpdateId(UI_UPDATE_LIVE_BITMAP);
            liveViewBitmap = bitmap;

            view.updateViewInMainThread(this);
        }

        public void randerCaptureBitmap(Bitmap bitmap) {

            setUpdateId(UI_UPDATE_CAPTURE_PICTURE);
            captureBitmap = bitmap;

            getView().updateViewInMainThread(this);
        }

        public void updateCameraZoomValue(int zoom) {

            setUpdateId(UI_UPDATE_CAMERA_ZOOM);
            this.zoom = zoom;

            getView().updateViewInMainThread(this);
        }

        public void updateViewCameraParam(CameraShootingParam param) {

            setUpdateId(UI_UPDATE_CAM_PARAM);
            camParam = param;

            getView().updateViewInMainThread(this);
        }

        public void updateViewFnumberList(ArrayList camAvailableFNumber, String fNumber) {

            setUpdateId(UI_UPDATE_FNUMBER_LIST);
            this.camAvailableFNumber = camAvailableFNumber;
            this.fNumber = fNumber;
            getView().updateViewInMainThread(this);
        }

        @Override
        protected void onUpdateEventChild(BaseMvpView view, int event) {

            ViewCamera liveView = (ViewCamera) view;

            if (liveView == null)
                return;

            switch (event) {
                case UI_UPDATE_LIVE_BITMAP:
                    liveView.updateLiveViewBitmap(liveViewBitmap);
                    break;
                case UI_UPDATE_CAPTURE_PICTURE:
                    liveView.updateCaptureBitmap(captureBitmap);
                    break;
                case UI_UPDATE_CAM_PARAM:
                    liveView.updateCameraParam(camParam);
                    break;
                case UI_UPDATE_CAMERA_ZOOM:
                    liveView.updateCameraZoom(zoom);
                    break;
                case UI_UPDATE_PROGRESS_PERCENT:
                    liveView.updateDownloadPercent(total, receive);
                    break;
                case UI_UPDATE_PROGRESS_DISMISS:
                    liveView.dismissDownloadPercent();
                    break;
                case UI_NAVIGATE_SHOW_RESULT:
                    liveView.navigateShowResultActivity();
                    break;
                case UI_UPDATE_FNUMBER_LIST:
                    liveView.updateFNumberList(camAvailableFNumber, fNumber);
                    break;
            }
        }

        public void showProgressDialog(int totalImg, int recieveImg) {

            ViewCamera view = getView();

            if (view == null)
                return;

            total = totalImg;
            receive = recieveImg;

            setUpdateId(UI_UPDATE_PROGRESS_PERCENT);

            view.updateViewInMainThread(this);
        }

        public void dismissProgressDialog() {

            setUpdateId(UI_UPDATE_PROGRESS_DISMISS);

            getView().updateViewInMainThread(this);
        }

        public void navigateShowCaptureResultActivity() {
            setUpdateId(UI_NAVIGATE_SHOW_RESULT);

            getView().updateViewInMainThread(this);
        }
    }
}
