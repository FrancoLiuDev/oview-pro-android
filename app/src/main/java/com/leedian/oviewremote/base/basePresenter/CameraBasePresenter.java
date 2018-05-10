package com.leedian.oviewremote.base.basePresenter;

import com.hannesdorfmann.mosby.mvp.MvpBasePresenter;

import com.leedian.oviewremote.OviewCameraApp;
import com.leedian.oviewremote.base.baseView.ActivityUIUpdate;
import com.leedian.oviewremote.base.baseView.BaseMvpView;
import com.leedian.oviewremote.presenter.camera.CameraManager;
import com.leedian.oviewremote.presenter.device.OviewDeviceManager;
import com.leedian.oviewremote.presenter.presenterInterface.BasePresenter;
import com.leedian.oviewremote.presenter.task.taskImp.UserManagerImp;
import com.leedian.oviewremote.presenter.task.taskInterface.UserManager;
import com.leedian.oviewremote.presenter.wifi.RemoteWifiTask;
import com.leedian.oviewremote.presenter.wifi.SSIDPreferencesManager;
import com.leedian.oviewremote.utils.exception.AuthException;
import com.leedian.oviewremote.utils.exception.DomainException;
import com.leedian.oviewremote.utils.exception.DomainExceptionHandler;

/**
 * BasePresenter for Mvc BasePresenter in this App
 * <p>
 * This BasePresenter is used as Base Presenter .
 *
 * @author Franco
 */
public class CameraBasePresenter<V extends BaseMvpView>
        extends MvpBasePresenter<V>
        implements BasePresenter<V> {
    //protected UserManager userManager = new UserManagerImp();

    static protected String wifiSSID;

    static protected CameraManager mCameraManager = new CameraManager();
    static protected RemoteWifiTask mRemoteWifiTask = new RemoteWifiTask(OviewCameraApp.getAppContext());

    protected static OviewDeviceManager mDeviceManager = new OviewDeviceManager(OviewCameraApp.getAppContext());
    protected UserManager userManageTask = new UserManagerImp();

    public void onClickConfirmLogout() {

        BaseMvpView view = getView();

        if (view == null) {
            return;
        }

        //userManager.setUserLogout();
        view.navigateToAppHomeActivity();
    }

    @Override
    public void onDisconnectWifi() {

        mRemoteWifiTask.disConnectCameraServer(wifiSSID);
    }


    protected void startUpdateUiThread(ActivityUIUpdate update) {

        BaseMvpView view = getView();

        if (view == null) {
            return;
        }

        view.updateViewInMainThread(update);
    }

    public void showLoadingDialog() {

        BaseMvpView view = getView();

        if (view == null) {
            return;
        }

        ActivityUIUpdate<BaseMvpView> uiUpdate = new ActivityUIUpdate<>(ActivityUIUpdate.UI_UPDATE_SHOW_LOADING,
                view);
        view.updateViewInMainThread(uiUpdate);
    }

    public void showLoadingDialogError() {

        BaseMvpView view = getView();

        if (view == null) {
            return;
        }

        ActivityUIUpdate<BaseMvpView> uiUpdate = new ActivityUIUpdate<>(ActivityUIUpdate.UI_UPDATE_SHOW_LOADING_ERROR,
                view);
        view.updateViewInMainThread(uiUpdate);
    }

    public void showLoadingDialogSuccess() {

        BaseMvpView view = getView();

        if (view == null) {
            return;
        }

        ActivityUIUpdate<BaseMvpView> uiUpdate = new ActivityUIUpdate<>(ActivityUIUpdate.UI_UPDATE_SHOW_LOADING_SUCEESS,
                view);
        view.updateViewInMainThread(uiUpdate);
    }

    public void hideLoadingDialog() {

        BaseMvpView view = getView();

        if (view == null) {
            return;
        }

        ActivityUIUpdate<BaseMvpView> uiUpdate = new ActivityUIUpdate<>(ActivityUIUpdate.UI_UPDATE_HIDE_LOADING,
                view);
        view.updateViewInMainThread(uiUpdate);
    }


    public void setWifiSettingInfo(String id, String pwd) {

        SSIDPreferencesManager preferencesManager = new SSIDPreferencesManager(OviewCameraApp.getAppContext());

        preferencesManager.setServerUrl(id, pwd);
    }

    public String getWifiSettingInfoSSID() {

        SSIDPreferencesManager preferencesManager = new SSIDPreferencesManager(OviewCameraApp.getAppContext());

        return preferencesManager.getServerInfomationSSID();
    }

    public String getWifiSettingInfoPassword() {

        SSIDPreferencesManager preferencesManager = new SSIDPreferencesManager(OviewCameraApp.getAppContext());

        return preferencesManager.getServerInfomationPassword();
    }

    /**
     * show massage to view
     *
     * @param msg show massage
     **/
    protected void showUiMessage(String msg) {

        BaseMvpView view = getView();

        if (view == null) {
            return;
        }

        ActivityUIUpdate uiUpdate = new ActivityUIUpdate<>(ActivityUIUpdate.UI_UPDATE_SHOW_ERROR_STRING,
                view).setShowMsg(msg);
        view.updateViewInMainThread(uiUpdate);
    }

    public void showTipMassage(String msg) {

        BaseMvpView view = getView();

        if (view == null) {
            return;
        }

        ActivityUIUpdate uiUpdate = new ActivityUIUpdate<>(ActivityUIUpdate.UI_SHOW_TIP_MASSAGE,
                view).setShowMsg(msg);
        view.updateViewInMainThread(uiUpdate);
    }

    protected void HandleOnException(Throwable e) {

        if (e instanceof DomainException) {
            onDomainException(e);
        }
    }

    protected void HandleOnException(Throwable e, DomainExceptionHandler handler) {

        if (e instanceof DomainException) {
            onDomainException(e,handler);
        }
    }

    protected void onDomainException(Throwable e) {

        int action;
        BaseMvpView view = getView();

        if (view == null) {
            return;
        }

        if (e instanceof AuthException) {
            AuthException domainException = (AuthException) e;
            action = domainException.getExceptionActionCode();
            if (action == DomainException.EXCEPTION_ACTION_RESTART_APP) {
                //userManager.setUserLogout();
                view.navigateToAppHomeActivityCauseReAuth();
            }
        }
    }

    @Override
    public boolean getIsUserLogin() {
        return false;
    }








    private void onDomainException(Throwable e, DomainExceptionHandler handler) {

        int action;
        BaseMvpView view = getView();

        if (view == null) {
            return;
        }

        if (e instanceof AuthException) {
            AuthException domainException = (AuthException) e;
            action = domainException.getExceptionActionCode();
            if (action == DomainException.EXCEPTION_ACTION_RESTART_APP) {

                view.navigateToAppHomeActivityCauseReAuth();
            }
        }
        handler.handleEvent((Exception) e);
    }
}
