package com.leedian.oviewremote.presenter.presenterImp;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.os.Handler;

import rx.Subscriber;
import com.leedian.oviewremote.AppManager;
import com.leedian.oviewremote.OviewCameraApp;
import com.leedian.oviewremote.base.basePresenter.CameraBasePresenter;
import com.leedian.oviewremote.base.baseView.ActivityUIUpdate;
import com.leedian.oviewremote.base.baseView.BaseMvpView;
import com.leedian.oviewremote.presenter.device.OviewDeviceManager;
import com.leedian.oviewremote.presenter.device.OviewDeviceManager.DeviceConnectionLister;
import com.leedian.oviewremote.presenter.presenterInterface.MainStatePresenter;
import com.leedian.oviewremote.presenter.wifi.RemoteWifiManager;
import com.leedian.oviewremote.presenter.wifi.WIfiListener;
import com.leedian.oviewremote.utils.BlueToothUtils;
import com.leedian.oviewremote.utils.subscript.ObservableActionIdentifier;
import com.leedian.oviewremote.utils.thread.JobExecutor;
import com.leedian.oviewremote.utils.thread.TaskExecutor;
import com.leedian.oviewremote.utils.thread.UIThread;
import com.leedian.oviewremote.view.viewInterface.ViewCamera;
import com.leedian.oviewremote.view.viewInterface.ViewMainState;

/**
 * Created by franco liu on 2017/2/7.
 */

public class MainStatePresenterImp extends CameraBasePresenter<ViewMainState>
        implements MainStatePresenter {

    private Context context;



    public MainStatePresenterImp(Context context){
      this.context = context;

    }


    static public MainStateUIUpdate getUIUpdateInstance(ViewMainState view){
        return new MainStateUIUpdate(view);
    }


    @Override
    public void ConnectToOviewDevice() {

        if (mDeviceManager.isConnected()){
            getUIUpdateInstance(getView()).updateDeviceConnectStates(true);
            return;
        }
        //scanLeDevice(true);
        mDeviceManager.startSearchDevice(new DeviceConnectionLister(){

            @Override
            public void onDeviceConnected(String name, String addr) {
                mDeviceManager.stopSearchDevice();

                connectDevice();
            }

            @Override
            public void onSearchConnectedTimeout() {
                mDeviceManager.stopSearchDevice();
                getUIUpdateInstance(getView()).updateDeviceConnectStates(false);
            }
        });



    }

    private void connectDevice(){

        if (!AppManager.isDevelopCameraOnlyDebugMode()) {


            mDeviceManager.registerEventListner(createDeviceEvent());
            mDeviceManager.connectToOviewDevice();
        }

    }

    OviewDeviceManager.DeviceServiceEvent event = null;

    private OviewDeviceManager.DeviceServiceEvent createDeviceEvent(){

        releaseDeviceEvent();


        event =   new OviewDeviceManager.DeviceServiceEvent() {

            @Override
            public void disconnect() {
                mDeviceManager.disconnectToOviewDevice();
                getUIUpdateInstance(getView()).updateDeviceConnectStates(false);
            }

            @Override
            public void connect() {
                getUIUpdateInstance(getView()).updateDeviceConnectStates(true);
            }
        };

        return event;
    }

    private void releaseDeviceEvent(){

        if (event!=null){
            mDeviceManager.unregisterEventListner(event);
            event = null;
        }
    }


    @Override
    public void onUserLogout() {

        Subscriber<ObservableActionIdentifier> subscriber = new Subscriber<ObservableActionIdentifier>() {
            @Override
            public void onStart() {

                showLoadingDialog();
                request(1);
            }

            @Override
            public void onCompleted() {

                getUIUpdateInstance(getView()).navigateToHomePage( );
                hideLoadingDialog();
            }

            @Override
            public void onError(Throwable e) {

                hideLoadingDialog();
                HandleOnException(e);
            }

            @Override
            public void onNext(ObservableActionIdentifier action) {

                request(1);
            }
        };
        TaskExecutor taskCase = new TaskExecutor(new JobExecutor(), new UIThread(), userManageTask
                .executeRequestUserLogout());
        taskCase.execute(subscriber);

    }

    @Override
    public void ConnectToPrivusWifiSSID() {

        String ssid = mRemoteWifiTask.checkIsAvalibleCameraConnected();
        String pwd ;

        if (ssid.length()!= 0)
        {

            getUIUpdateInstance(getView()).updateCameraConnectStates(true);
            connecWifiSSID(ssid,"");
            return;
        }


        ssid = getWifiSettingInfoSSID();
        pwd = getWifiSettingInfoPassword();


        if (ssid.length() < 1) {
            return;
        }

        connecWifiSSID(ssid,pwd);

    }

    WIfiListener listenerConnect = new WIfiListener();

    @Override
    public void connecWifiSSID(String SSID, String password) {

        wifiSSID = SSID;




        mRemoteWifiTask.stopWatcWifiSates(listenerConnect);

        listenerConnect.setWifiName(wifiSSID);
        listenerConnect.setEvent(new RemoteWifiManager.WifiConnectionEvent() {

            @Override
            public void onConnected() {
                getUIUpdateInstance(getView()).updateCameraConnectStates(true);
                //getUIUpdateInstance(getView()).hideLoadingDialog();
            }

            @Override
            public void onDisconnected() {
                getUIUpdateInstance(getView()).updateCameraConnectStates(false);
            }

            @Override
            public void onConnectionErrorFinish() {
                getUIUpdateInstance(getView()).updateCameraConnectStates(false);

            }
        });

        mRemoteWifiTask.startWatcWifiSates(listenerConnect);



        if (mRemoteWifiTask.checkIfCameraConnect(SSID)){

            listenerConnect.setConnected(true);
            getUIUpdateInstance(getView()).updateCameraConnectStates(true);
            getUIUpdateInstance(getView()).hideLoadingDialog();
        }
        else {
            mRemoteWifiTask.connectToCameraSSID(SSID, password);
        }


    }


    WIfiListener listenerObserve = new WIfiListener();

    @Override
    public void startWatchWifiConnection() {


        listenerObserve.setWifiName(wifiSSID);
        listenerObserve.setEvent(new RemoteWifiManager.WifiConnectionEvent() {

            @Override
            public void onConnected() {
                getUIUpdateInstance(getView()).updateCameraConnectStates(true);
                getUIUpdateInstance(getView()).hideLoadingDialog();
            }

            @Override
            public void onDisconnected() {
                getUIUpdateInstance(getView()).updateCameraConnectStates(false);
            }

            @Override
            public void onConnectionErrorFinish() {
                getUIUpdateInstance(getView()).updateCameraConnectStates(false);
                getUIUpdateInstance(getView()).hideLoadingDialog();
            }
        });

        mRemoteWifiTask.startWatcWifiSates(listenerObserve);


    }

    @Override
    public void detachView(boolean retainInstance) {
        super.detachView(retainInstance);

        mRemoteWifiTask.stopWatcWifiSates(listenerObserve);
        mRemoteWifiTask.stopWatcWifiSates(listenerConnect);
    }



    private static class MainStateUIUpdate
            extends ActivityUIUpdate<ViewMainState>
    {
        final static public int UI_UPDATE_CAM_CONNECT_STATES = 1;
        final static public int UI_UPDATE_DEVICE_CONNECT_STATES = 2;
        final static public int UI_UPDATE_NAVIGATE_HOME = 3;

        public MainStateUIUpdate(ViewMainState view) {
            super(view);
        }

        boolean bConnSuccess;

        void updateCameraConnectStates(boolean bSuccess){

            setUpdateId(UI_UPDATE_CAM_CONNECT_STATES);

            bConnSuccess = bSuccess;

            getView().updateViewInMainThread(this);
        }

        void updateDeviceConnectStates(boolean bSuccess){

            setUpdateId(UI_UPDATE_DEVICE_CONNECT_STATES);

            bConnSuccess = bSuccess;

            getView().updateViewInMainThread(this);
        }

        void navigateToHomePage(){

            setUpdateId(UI_UPDATE_NAVIGATE_HOME);



            getView().updateViewInMainThread(this);
        }

        @Override protected void onUpdateEventChild(BaseMvpView view, int event) {

            ViewMainState stateView = (ViewMainState) view;

            switch (event) {
                case UI_UPDATE_CAM_CONNECT_STATES:
                    stateView.setCameraState(bConnSuccess);
                     break;
                case UI_UPDATE_DEVICE_CONNECT_STATES:
                    stateView.setDeviceState(bConnSuccess);
                    break;
                case UI_UPDATE_NAVIGATE_HOME:
                    stateView.navigateToAppHomeActivity();
                    break;



            }
        }

    }

}
