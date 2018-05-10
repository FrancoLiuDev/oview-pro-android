package com.leedian.oviewremote.presenter.camera;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import com.leedian.oviewremote.AppManager;
import com.leedian.oviewremote.OviewCameraApp;
import com.leedian.oviewremote.model.CameraShootingParam;
import com.leedian.oviewremote.presenter.device.OviewDeviceManager;
import com.leedian.oviewremote.utils.subscript.ObservableActionIdentifier;
import com.leedian.oviewremote.utils.subscript.ObservableCameraAction;
import static com.blankj.utilcode.utils.FileUtils.createOrExistsDir;

/**
 * Camera Manager class
 */
public class CameraManager {

    private final Set<String> mAvailableCameraApiSet = new HashSet<String>();
    private final Set<String> mSupportedApiSet = new HashSet<>();
    private final BlockingQueue<byte[]> mJpegQueue;
    private ArrayList availableISO = null;
    private ArrayList availableFnumber = null;
    private ArrayList availableShortcut = null;
    private ArrayList availableExposureCompensation = null;
    boolean shooting = true;
    boolean threadStop = false;
    private LiveViewListener liveviewListener = null;
    private ServerDevice mCameraDevice = null;
    private CameraRemoteApi mRemoteApi = null;
    private CameraEventObserver mEventObserver;
    private CameraEventObserver.ChangeListener mEventListener;
    private String mCameraStatus;
    private boolean bIsSupport;
    private boolean mWhileFetching;
    private Thread mDrawerThread;

    public CameraManager() {
        mJpegQueue = new ArrayBlockingQueue<byte[]>(2);
    }

    public void setbIsSupport(boolean bIsSupport) {
        this.bIsSupport = bIsSupport;
    }

    public void setmEventListener(CameraEventObserver.ChangeListener mEventListener) {
        this.mEventListener = mEventListener;
    }

    public void setLiveviewListener(LiveViewListener liveviewListener) {
        this.liveviewListener = liveviewListener;
    }

    public void setmCameraDevice(ServerDevice mCameraDevice) {
        this.mCameraDevice = mCameraDevice;
    }

    public boolean initCamera() {

        mRemoteApi = new CameraRemoteApi(mCameraDevice);
        mEventObserver = new CameraEventObserver(OviewCameraApp.getAppContext(), mRemoteApi);
        mEventObserver.setEventChangeListener(mEventListener);

        JSONObject replyJsonCamera = null;
        try {

            JSONObject replyJson = null;

            replyJsonCamera = mRemoteApi.getCameraMethodTypes();
            loadSupportedApiList(replyJsonCamera);

            if (!isApiSupported("getEvent")) {
                bIsSupport = false;
                return false;
            }

            if (isCameraApiAvailable("getApplicationInfo")) {

                replyJson = mRemoteApi.getApplicationInfo();
                if (!isSupportedServerVersion(replyJson)) {
                    setbIsSupport(false);
                    //   we don't support
                    return false;
                }
            }

            mCameraStatus = getCameraStatus();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        loadSupportedApiList(replyJsonCamera);

        return true;
    }

    public void startPreview() {
        openConnection();
    }

    public void stopPreview() {
        stopLiveview();
    }

    public Observable<ObservableActionIdentifier> executeGetCameraParm() {

        return Observable.create(
                new Observable.OnSubscribe<ObservableActionIdentifier>() {
                    @Override
                    public void call(Subscriber<? super ObservableActionIdentifier> subscriber) {
                        try {

                            String str;

                            CameraShootingParam parm = retrieveCameraShootingParam();

                            ObservableActionIdentifier identifier = new ObservableActionIdentifier(ObservableCameraAction.ACTION_GET_CAMERPARM, "");
                            identifier.setObject(parm);

                            subscriber.onNext(identifier);
                            subscriber.onCompleted();
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    private CameraShootingParam retrieveCameraShootingParam() throws IOException, JSONException {

        CameraRemoteApi.ApiResult result;
        CameraShootingParam parm = new CameraShootingParam();

        result = mRemoteApi.getExposureMode();
        if (result.isSuccess) {
            parm.setExpMode(result.retrieveExposure());
        }

        result = mRemoteApi.getFNumber();
        if (result.isSuccess) {
            parm.setFNumber(result.retrieveFNumber());
        }

        result = mRemoteApi.getIsoSpeedRate();
        if (result.isSuccess) {
            parm.setExpISO(result.retrieveISO());
        }

        if (this.availableISO != null) {
            parm.setCamAvailableIsoSpeedRate(this.availableISO);
        }

        if (this.availableFnumber != null) {
            parm.setCamAvailableFNumber(this.availableFnumber);
        }

        if (this.availableShortcut != null) {
            parm.setCamAvailableShortcut(this.availableShortcut);
        }

        if (this.availableExposureCompensation != null) {
            parm.setCamAvailableExposureCompensation(this.availableExposureCompensation);
        }

        result = mRemoteApi.getExposureCompensation();
        if (result.isSuccess) {
            parm.setExpExposureCompensation(result.retrieveResultValue());
        }

        result = mRemoteApi.getShutterSpeed();
        if (result.isSuccess) {
            parm.setShortcut(result.retrieveResultValue());
        }

        return parm;
    }

    public Observable<ObservableActionIdentifier> executeTackPicture() {

        return Observable.create(
                new Observable.OnSubscribe<ObservableActionIdentifier>() {
                    @Override
                    public void call(Subscriber<? super ObservableActionIdentifier> subscriber) {
                        try {

                            CameraRemoteApi.ApiResult result;

                            result = mRemoteApi.actTakePicture();

                            if (result.isSuccess) {

                                JSONArray array = new JSONArray(result.retrieveResultValue());

                                String url = array.getString(0);

                                Bitmap bmp = getCameraPhotoByUrl(url);

                                ObservableActionIdentifier identifier = new ObservableActionIdentifier(ObservableCameraAction.ACTION_TAKE_PICTURE, "");

                                identifier.setObject(bmp);

                                subscriber.onNext(identifier);
                            }

                            subscriber.onCompleted();
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    public Observable<ObservableActionIdentifier> requestCameraPicture(final String strUrl) {

        return Observable.create(
                new Observable.OnSubscribe<ObservableActionIdentifier>() {
                    @Override
                    public void call(Subscriber<? super ObservableActionIdentifier> subscriber) {
                        try {

                            URL url = new URL(strUrl);

                            InputStream istream = new BufferedInputStream(url.openStream());
                            BitmapFactory.Options options = new BitmapFactory.Options();
                            options.inSampleSize = 4; // irresponsible value

                            Bitmap image = BitmapFactory.decodeStream(istream, null, options);

                            istream.close();

                            ObservableActionIdentifier identifier = new ObservableActionIdentifier(ObservableCameraAction.ACTION_REQUEST_PICTURE_RETURN, "");
                            identifier.setObject(image);

                            subscriber.onNext(identifier);
                            subscriber.onCompleted();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    public Observable<ObservableActionIdentifier> requestRetrievePhotoArrary(final ArrayList<String> list) {

        final ArrayList<Bitmap> bmpList = new ArrayList<Bitmap>();

        return Observable.create(
                new Observable.OnSubscribe<ObservableActionIdentifier>() {
                    @Override
                    public void call(Subscriber<? super ObservableActionIdentifier> subscriber) {

                        for (int i = 0; i < list.size(); i++) {

                            Bitmap bmp = getCameraPhotoByUrl(list.get(i));
                            bmpList.add(bmp);
                        }

                        ObservableActionIdentifier identifier = new ObservableActionIdentifier(ObservableCameraAction.ACTION_REQUEST_PICTURE_RETURN, "");

                        identifier.setObject(bmpList);
                        subscriber.onNext(identifier);
                        subscriber.onCompleted();
                    }
                });
    }

    public Bitmap getCameraPhotoByUrl(final String strUrl) {

        Bitmap bmp = null;

        URL url = null;
        try {

            url = new URL(strUrl);
            InputStream istream = new BufferedInputStream(url.openStream());
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 2; // irresponsible value
            options.inDensity = 100;

            bmp = BitmapFactory.decodeStream(istream, null, options);

            istream.close();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return bmp;
    }

    public Observable<ObservableActionIdentifier> executeChangeCameraZooom(final String direction) {

        return Observable.create(
                new Observable.OnSubscribe<ObservableActionIdentifier>() {
                    @Override
                    public void call(Subscriber<? super ObservableActionIdentifier> subscriber) {
                        try {

                            CameraRemoteApi.ApiResult result = null;

                            result = mRemoteApi.actZoom(direction, "1shot");

                            ObservableActionIdentifier identifier = new ObservableActionIdentifier(ObservableCameraAction.ACTION_CHANGE_ZOOM, "");
                            identifier.setObject(null);

                            subscriber.onNext(identifier);
                            subscriber.onCompleted();

                            subscriber.onCompleted();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    public Observable<ObservableActionIdentifier> executeCameraSetFocus(final float xPosPercent, final float yPosPercent) {

        return Observable.create(
                new Observable.OnSubscribe<ObservableActionIdentifier>() {
                    @Override
                    public void call(Subscriber<? super ObservableActionIdentifier> subscriber) {
                        try {

                            CameraRemoteApi.ApiResult result = null;

                            result = mRemoteApi.cancelHalfPressShutter();

                            String s = String.valueOf(xPosPercent * 100);
                            String strX = s.substring(0, s.indexOf(".") + 3);

                            s = String.valueOf(yPosPercent * 100);
                            String strY = s.substring(0, s.indexOf(".") + 3);

                            result = mRemoteApi.setTouchAFPosition(Float.parseFloat(strX), Float.parseFloat(strY));

                            result = mRemoteApi.actHalfPressShutter();

                            //result = mRemoteApi.cancelHalfPressShutter();
                            ObservableActionIdentifier identifier = new ObservableActionIdentifier(ObservableCameraAction.ACTION_CHANGE_ZOOM, "");
                            identifier.setObject(null);

                            subscriber.onNext(identifier);
                            subscriber.onCompleted();

                            subscriber.onCompleted();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }
    /*public Observable<ObservableActionIdentifier> executeCameraCapture() {

        shooting = true;
        threadStop = false;

        int captureMode;
        final int captureframe  = 100;

        final Handler mUI_Handler = new Handler();

        return Observable.create(
                new Observable.OnSubscribe<ObservableActionIdentifier>() {
                    @Override
                    public void call(final Subscriber<? super ObservableActionIdentifier> subscriber) {

                        subscriber.add(new Subscription() {
                            @Override
                            public void unsubscribe() {
                                threadStop =true;
                            }

                            @Override
                            public boolean isUnsubscribed() {
                                return (!shooting);
                            }
                        });

                        //FileOutputStream out = null;

                        createOrExistsDir(AppManager.getOviewContentDir());
                        long start_time = SystemClock.uptimeMillis();
                        long end_time = SystemClock.uptimeMillis();

                        long total_time = 0;
                        int cnt= captureframe;

                        try {


                            cnt= captureframe;
                            while (!threadStop && cnt > 0){

                                CameraRemoteApi.ApiResult result = null;
                                result = mRemoteApi.actTakePicture();

                                if (result.isSuccess) {

                                    JSONArray array = new JSONArray(result.retrieveResultValue());

                                    final String url = array.getString(0);

                                    final int finalCnt = cnt;

                                    start_time = SystemClock.uptimeMillis();

                                    Bitmap bmp = getCameraPhotoByUrl(url);

                                    end_time = SystemClock.uptimeMillis();

                                    total_time = total_time + (end_time - start_time);
                                    FileOutputStream out = null;

                                    createOrExistsFile(AppManager.getOviewContentDir()+"/"+ String.valueOf(finalCnt)+".jpeg");

                                        out = new FileOutputStream(AppManager.getOviewContentDir()+"/"+ String.valueOf(finalCnt)+".jpeg");
                                        bmp.compress(Bitmap.CompressFormat.JPEG, 100, out); // bmp is your Bitmap instance
                                        bmp.recycle();
                                        out.close();

                                        ObservableActionIdentifier identifier = new ObservableActionIdentifier(ObservableCameraAction.ACTION_TAKE_PICTURE, "");

                                        identifier.setObject(bmp);

                                        subscriber.onNext(identifier);

                                    try {
                                        Thread.sleep(1500);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                }

                                cnt-=1;
                            }


                            shooting =false;


                            if  (!threadStop) {


                                String strLong = Long.toString(total_time);
                                String strLong2 = Long.toString(total_time/captureframe);
                                Log.d("END",strLong);
                                Log.d("AVG TIME",strLong2);
                                ObservableActionIdentifier identifier = new ObservableActionIdentifier(ObservableCameraAction.ACTION_TAKE_FINISH, "");
                                subscriber.onNext(identifier);
                                subscriber.onCompleted();

                            }
                        } catch (IOException e) {
                            end_time = SystemClock.uptimeMillis();
                            String strLong = Long.toString(end_time - start_time);
                            String strCount = Integer.toString(cnt);
                            Log.d("COUNT",strCount);
                            Log.d("END",strLong);
                            e.printStackTrace();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });

    }*/

    public Observable<ObservableActionIdentifier> executeCameraCapture(int takeNumber, final OviewDeviceManager device) {

        shooting = true;
        threadStop = false;

        int captureMode;
        final int captureframe = takeNumber;

        final Handler mUI_Handler = new Handler();

        return Observable.create(
                new Observable.OnSubscribe<ObservableActionIdentifier>() {
                    @Override
                    public void call(final Subscriber<? super ObservableActionIdentifier> subscriber) {

                        subscriber.add(new Subscription() {
                            @Override
                            public void unsubscribe() {
                                threadStop = true;
                            }

                            @Override
                            public boolean isUnsubscribed() {
                                return (!shooting);
                            }
                        });

                        createOrExistsDir(AppManager.getOviewContentDir());

                        try {

                            float degree;
                            int cnt = captureframe;

                            if (!AppManager.isDevelopCameraOnlyDebugMode()) {
                                device.initRotate();
                            }

                            while (!threadStop && cnt > 0) {

                                CameraRemoteApi.ApiResult result = null;

                                long tick_time = SystemClock.uptimeMillis();
                                String strLong = Long.toString(tick_time);

                                if (!AppManager.isDevelopCameraOnlyDebugMode()) {

                                    if (!device.checkRotateReady()) {
                                        subscriber.onError(new Exception());
                                        break;
                                    }
                                }

                                Log.d("actTakePicture", strLong);
                                result = mRemoteApi.actTakePicture();

                                degree = (float) 360f / captureframe;

                                if (!AppManager.isDevelopCameraOnlyDebugMode()) {
                                    device.startRotate(degree);
                                    Thread.sleep(200);
                                }

                                String strCount = Integer.toString(cnt);
                                Log.d("actTakePicture", strCount);
                                Log.d("url", strCount);

                                if (result.isSuccess) {

                                    JSONArray array = new JSONArray(result.retrieveResultValue());

                                    final String url = array.getString(0);

                                    final int finalCnt = cnt;
                                    Log.d("url", url);

                                    ObservableActionIdentifier identifier = new ObservableActionIdentifier(ObservableCameraAction.ACTION_TAKE_PICTURE, "");

                                    identifier.setContent(url);
                                    identifier.setObject(finalCnt);

                                    subscriber.onNext(identifier);

                                    try {
                                        Thread.sleep(1500);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                }

                                cnt -= 1;
                            }

                            shooting = false;

                            if (!threadStop) {
                                ObservableActionIdentifier identifier = new ObservableActionIdentifier(ObservableCameraAction.ACTION_TAKE_FINISH, "");
                                subscriber.onNext(identifier);
                                subscriber.onCompleted();
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    public Observable<ObservableActionIdentifier> executeCameraCaptureWithDegree(int degree, final OviewDeviceManager device) {

        shooting = true;
        threadStop = false;

        int captureMode;
        final int captureDegree = degree;

        final Handler mUI_Handler = new Handler();

        return Observable.create(
                new Observable.OnSubscribe<ObservableActionIdentifier>() {
                    @Override
                    public void call(final Subscriber<? super ObservableActionIdentifier> subscriber) {

                        subscriber.add(new Subscription() {
                            @Override
                            public void unsubscribe() {
                                threadStop = true;
                            }

                            @Override
                            public boolean isUnsubscribed() {
                                return (!shooting);
                            }
                        });

                        //FileOutputStream out = null;

                        createOrExistsDir(AppManager.getOviewContentDir());

                        try {

                            float degree;
                            CameraRemoteApi.ApiResult result = null;

                            degree = captureDegree;

                            if (!AppManager.isDevelopCameraOnlyDebugMode()) {
                                device.startRotate(degree);
                                device.checkRotateReady();
                            }

                            result = mRemoteApi.actTakePicture();

                            if (result.isSuccess) {

                                JSONArray array = new JSONArray(result.retrieveResultValue());

                                final String url = array.getString(0);

                                final int finalCnt = 1;
                                Log.d("url", url);

                                ObservableActionIdentifier identifier = new ObservableActionIdentifier(ObservableCameraAction.ACTION_TAKE_PICTURE, "");

                                identifier.setContent(url);
                                identifier.setObject(finalCnt);
                                subscriber.onNext(identifier);

                                try {
                                    Thread.sleep(1500);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }

                            shooting = false;

                            if (!threadStop) {
                                ObservableActionIdentifier identifier = new ObservableActionIdentifier(ObservableCameraAction.ACTION_TAKE_FINISH, "");
                                subscriber.onNext(identifier);
                                subscriber.onCompleted();
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    public Observable executeReleaseTouchFocus() {

        return Observable.create(
                new Observable.OnSubscribe<ObservableActionIdentifier>() {
                    @Override
                    public void call(Subscriber<? super ObservableActionIdentifier> subscriber) {
                        try {

                            CameraRemoteApi.ApiResult result = null;
                            result = mRemoteApi.cancelHalfPressShutter();

                            //result = mRemoteApi.cancelHalfPressShutter();
                            ObservableActionIdentifier identifier = new ObservableActionIdentifier(ObservableCameraAction.ACTION_CHANGE_ZOOM, "");
                            identifier.setObject(null);

                            subscriber.onNext(identifier);
                            subscriber.onCompleted();

                            subscriber.onCompleted();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    public Observable<ObservableActionIdentifier> executeChangeCameraValue(final String valueKey, final String value) {

        return Observable.create(
                new Observable.OnSubscribe<ObservableActionIdentifier>() {
                    @Override
                    public void call(Subscriber<? super ObservableActionIdentifier> subscriber) {
                        try {

                            boolean isSuccess;
                            CameraRemoteApi.ApiResult result = null;

                            if (valueKey.equals("iso")) {
                                result = mRemoteApi.setIsoSpeedRate(value);
                            }

                            if (valueKey.equals("fnumber")) {
                                result = mRemoteApi.setFNumber(value);
                            }

                            if (valueKey.equals("shortcut")) {
                                result = mRemoteApi.setShutterSpeed(value);
                            }

                            ObservableActionIdentifier identifier = new ObservableActionIdentifier(ObservableCameraAction.ACTION_GET_CAMERPARM, "");
                            identifier.setObject(null);

                            subscriber.onNext(identifier);
                            subscriber.onCompleted();

                            subscriber.onCompleted();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    /**
     * Open connection to the camera device to start monitoring Camera events
     * and showing liveview.
     */
    private void openConnection() {

        mEventObserver.setEventChangeListener(mEventListener);
        mEventObserver.activate();
        new Thread() {

            @Override
            public void run() {

                try {
                    JSONObject replyJson = null;

                    replyJson = mRemoteApi.getAvailableApiList();
                    loadAvailableCameraApiList();

                    if (isCameraApiAvailable("startRecMode")) {

                        replyJson = mRemoteApi.startRecMode();
                    }

                    if (isCameraApiAvailable("getEvent")) {

                        mEventObserver.start();
                    }

                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    replyJson = mRemoteApi.getAvailableApiList();
                    loadAvailableCameraApiList();

                    if (isCameraApiAvailable("startLiveview")) {

                        startLiveview();
                    }

                    replyJson = mRemoteApi.getSupportedExposureMode();

                    /*if (isCameraApiAvailable("setExposureMode")) {

                        replyJson = mRemoteApi.setExposureMode(CameraRemoteApi.CameraParameter.EXPOSURE_APERTURE_PRIORITY);
                        replyJson = null;
                    }*/

                    CameraRemoteApi.ApiResult result;

                    result = mRemoteApi.setPostviewImageSize("Original");
                    result = mRemoteApi.getPostviewImageSize();
                    result = mRemoteApi.getSupportedPostviewImageSize();

                    result = mRemoteApi.getAvailableIsoSpeedRate();
                    if (result.isSuccess) {
                        availableISO = result.retrieveAvailableResultValue();
                    }

                    //getFocusMode
                    result = mRemoteApi.getFocusMode();
                    if (result.isSuccess) {

                    }

                    result = mRemoteApi.getAvailableFNumber();
                    if (result.isSuccess) {
                        availableFnumber = result.retrieveAvailableResultValue();
                    }

                    result = mRemoteApi.getAvailableShutterSpeed();
                    if (result.isSuccess) {
                        availableShortcut = result.retrieveAvailableResultValue();
                    }

                    result = mRemoteApi.getAvailableExposureCompensation();
                    if (result.isSuccess) {
                        availableExposureCompensation = result.retrieveAvailableResultValue2();
                    }

                    liveviewListener.onConnectSuccess();
                    replyJson = null;
                } catch (IOException e) {

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    private void stopLiveview() {
        new Thread() {
            @Override
            public void run() {
                try {

                    setmWhileFetching(false);

                    if (mEventObserver != null)
                        mEventObserver.stop();

                    if (mRemoteApi != null)
                        mRemoteApi.stopLiveview();
                } catch (IOException e) {

                }
            }
        }.start();
    }

    public void setmWhileFetching(boolean mWhileFetching) {
        this.mWhileFetching = mWhileFetching;
    }

    public boolean startFetch(final String streamUrl) {
        //mErrorListener = listener;

        if (streamUrl == null) {

            setmWhileFetching(false);
            liveviewListener.onError(LiveViewListener.StreamErrorReason.OPEN_ERROR);
            return false;
        }

        if (mWhileFetching) {

            return false;
        }

        setmWhileFetching(true);

        // A thread for retrieving liveview data from server.
        new Thread() {
            @Override
            public void run() {

                CameraLiveViewSlicer slicer = null;

                try {

                    // Create Slicer to open the stream and parse it.
                    slicer = new CameraLiveViewSlicer();
                    slicer.open(streamUrl);

                    while (mWhileFetching) {
                        final CameraLiveViewSlicer.Payload payload = slicer.nextPayload();
                        if (payload == null) { // never occurs
                            continue;
                        }

                        if (mJpegQueue.size() == 2) {
                            mJpegQueue.remove();
                        }
                        mJpegQueue.add(payload.jpegData);
                    }
                } catch (IOException e) {

                    setmWhileFetching(false);

                    if (mDrawerThread != null) {
                        mDrawerThread.interrupt();
                    }

                    fireOnLiveError(LiveViewListener.StreamErrorReason.IO_EXCEPTION);
                } finally {
                    if (slicer != null) {
                        slicer.close();
                    }

                    if (mDrawerThread != null) {
                        mDrawerThread.interrupt();
                    }

                    mJpegQueue.clear();
                    mWhileFetching = false;
                }
            }
        }.start();

        mDrawerThread = new Thread() {
            @Override
            public void run() {

                Bitmap frameBitmap = null;

                BitmapFactory.Options factoryOptions = new BitmapFactory.Options();
                factoryOptions.inSampleSize = 1;
                factoryOptions.inBitmap = null;
                factoryOptions.inMutable = true;

                while (mWhileFetching) {
                    try {
                        byte[] jpegData = mJpegQueue.take();
                        frameBitmap = BitmapFactory.decodeByteArray(//
                                jpegData, 0, jpegData.length, factoryOptions);
                    } catch (IllegalArgumentException e) {
                        if (factoryOptions.inBitmap != null) {
                            factoryOptions.inBitmap.recycle();
                            factoryOptions.inBitmap = null;
                        }
                        continue;
                    } catch (InterruptedException e) {
                        mWhileFetching = false;
                        break;
                    }
                    factoryOptions.inBitmap = frameBitmap;

                    if (frameBitmap != null)
                        fireOnLiveRanderring(frameBitmap);
                    // drawFrame(frameBitmap);
                }

                if (frameBitmap != null) {
                    frameBitmap.recycle();
                }
                mWhileFetching = false;
            }
        };
        mDrawerThread.start();
        return true;
    }

    private void startLiveview() {

        new Thread() {
            @Override
            public void run() {

                try {
                    JSONObject replyJson = null;
                    replyJson = mRemoteApi.startLiveview();

                    if (!CameraRemoteApi.isErrorReply(replyJson)) {
                        JSONArray resultsObj = replyJson.getJSONArray("result");
                        if (1 <= resultsObj.length()) {
                            // Obtain liveview URL from the result.
                            final String liveviewUrl = resultsObj.getString(0);
                            startFetch(liveviewUrl);
                        }
                    }
                } catch (IOException e) {

                } catch (JSONException e) {

                }
            }
        }.start();
    }

    /**
     * Check if the version of the server is supported in this application.
     *
     * @param replyJson
     * @return
     */
    private boolean isSupportedServerVersion(JSONObject replyJson) {

        try {
            JSONArray resultArrayJson = replyJson.getJSONArray("result");
            String version = resultArrayJson.getString(1);
            String[] separated = version.split("\\.");
            int major = Integer.valueOf(separated[0]);
            if (2 <= major) {
                return true;
            }
        } catch (JSONException e) {

        } catch (NumberFormatException e) {

        }
        return false;
    }

    private boolean loadApplicationInfo() throws IOException, JSONException {

        JSONObject replyJson = null;
        replyJson = mRemoteApi.getApplicationInfo();

        JSONArray resultArrayJson = replyJson.getJSONArray("result");
        String version = resultArrayJson.getString(1);
        String[] separated = version.split("\\.");
        int major = Integer.valueOf(separated[0]);
        if (2 <= major) {
            return true;
        }

        return true;
    }

    /**
     * Check if the specified API is available at present. This works correctly
     * only for Camera API.
     *
     * @param apiName
     * @return
     */
    private boolean isCameraApiAvailable(String apiName) {
        boolean isAvailable = false;
        synchronized (mAvailableCameraApiSet) {
            isAvailable = mAvailableCameraApiSet.contains(apiName);
        }
        return isAvailable;
    }

    /**
     * Retrieve a list of APIs that are available at present.
     */
    private void loadAvailableCameraApiList() throws IOException {

        JSONObject replyJson = null;

        // getAvailableApiList
        replyJson = mRemoteApi.getAvailableApiList();

        synchronized (mAvailableCameraApiSet) {
            mAvailableCameraApiSet.clear();
            try {
                JSONArray resultArrayJson = replyJson.getJSONArray("result");
                JSONArray apiListJson = resultArrayJson.getJSONArray(0);
                for (int i = 0; i < apiListJson.length(); i++) {
                    mAvailableCameraApiSet.add(apiListJson.getString(i));
                }
            } catch (JSONException e) {

            }
        }
    }

    private String getCameraStatus() throws IOException, JSONException {

        String cameraStatus = null;
        JSONObject replyJson = mRemoteApi.getEvent(false);
        JSONArray resultsObj = replyJson.getJSONArray("result");
        JSONObject cameraStatusObj = resultsObj.getJSONObject(1);
        String type = cameraStatusObj.getString("type");

        if ("cameraStatus".equals(type)) {
            cameraStatus = cameraStatusObj.getString("cameraStatus");
        } else {
            throw new IOException();
        }

        return cameraStatus;
    }

    /**
     * Check if the specified API is supported. This is for camera and avContent
     * service API. The result of this method does not change dynamically.
     *
     * @param apiName
     * @return
     */
    private boolean isApiSupported(String apiName) {
        boolean isAvailable = false;
        synchronized (mSupportedApiSet) {
            isAvailable = mSupportedApiSet.contains(apiName);
        }
        return isAvailable;
    }

    /**
     * Retrieve a list of APIs that are supported by the target device.
     *
     * @param replyJson
     */
    private void loadSupportedApiList(JSONObject replyJson) {
        synchronized (mSupportedApiSet) {
            try {
                JSONArray resultArrayJson = replyJson.getJSONArray("results");
                for (int i = 0; i < resultArrayJson.length(); i++) {
                    mSupportedApiSet.add(resultArrayJson.getJSONArray(i).getString(0));
                }
            } catch (JSONException e) {
                //Log.w(TAG, "loadSupportedApiList: JSON format error.");
            }
        }
    }

    private void fireOnLiveError(LiveViewListener.StreamErrorReason reason) {

        if (liveviewListener == null) return;

        liveviewListener.onError(reason);
    }

    private void fireOnLiveRanderring(Bitmap data) {

        if (liveviewListener == null) return;

        liveviewListener.onLiveImageRendering(data);
    }

    public interface LiveViewListener {

        void onLiveImageRendering(Bitmap data);

        void onConnectSuccess();

        void onError(StreamErrorReason reason);

        enum StreamErrorReason {
            IO_EXCEPTION,
            OPEN_ERROR,
        }
    }
}
