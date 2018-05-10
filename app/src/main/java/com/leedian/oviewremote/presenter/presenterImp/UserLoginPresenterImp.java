package com.leedian.oviewremote.presenter.presenterImp;


import rx.Subscriber;
import com.leedian.oviewremote.AppResource;
import com.leedian.oviewremote.R;
import com.leedian.oviewremote.base.basePresenter.CameraBasePresenter;
import com.leedian.oviewremote.base.baseView.ActivityUIUpdate;
import com.leedian.oviewremote.base.baseView.BaseMvpView;
import com.leedian.oviewremote.model.dataIn.AuthCredentials;
import com.leedian.oviewremote.presenter.presenterInterface.UserLoginPresenter;

import com.leedian.oviewremote.presenter.task.taskImp.UserManagerImp;
import com.leedian.oviewremote.presenter.task.taskInterface.UserManager;
import com.leedian.oviewremote.utils.exception.AuthException;
import com.leedian.oviewremote.utils.exception.DomainException;
import com.leedian.oviewremote.utils.subscript.ObservableActionIdentifier;
import com.leedian.oviewremote.utils.thread.JobExecutor;
import com.leedian.oviewremote.utils.thread.TaskExecutor;
import com.leedian.oviewremote.utils.thread.UIThread;
import com.leedian.oviewremote.view.viewInterface.ViewUserLoginMvp;

/**
 * UserLoginPresenter
 *
 * @author Franco
 */
public class UserLoginPresenterImp
        extends CameraBasePresenter<ViewUserLoginMvp>
        implements UserLoginPresenter
{


    /**
     * process user login
     *
     * @param name     Somebody's name.
     * @param password Somebody's password.
     **/
    @Override
    public void doUserLogin(String name, String password) {

        final AuthCredentials credentials = new AuthCredentials(name, password);
        if (name.length() < 1) {
            showUiMessage(AppResource.getString(R.string.please_input_user_name));
            uiViewShowError("name");
            return;
        }
        if (password.length() < 1) {
            showUiMessage(AppResource.getString(R.string.please_input_user_password));
            uiViewShowError("password");
            return;
        }
        Subscriber<ObservableActionIdentifier> subscriber = new Subscriber<ObservableActionIdentifier>() {
            @Override
            public void onStart() {

                showLoadingDialog();
                request(1);
            }

            @Override
            public void onCompleted() {

                setLoginSuccess();
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
                .executeRequestUserLogin(credentials));
        taskCase.execute(subscriber);
    }

    /**
     * handle on domain exception
     *
     * @param e domain Throwable
     **/
    @Override
    protected void onDomainException(Throwable e) {

        DomainException exception = (DomainException) e;

        if (DomainException.isExceptionAuth(exception)) {
            AuthException exceptionAuth = (AuthException) exception;

            if (exceptionAuth.isAuthFailName()) {
                uiViewShowError("name");
            }

            if (exceptionAuth.isAuthFailPassword()) {
                uiViewShowError("password");
            }
        }

        showUiMessage(exception.getMessage());
        super.onDomainException(e);
    }

    /**
     * fire event when login in success
     **/
    private void setLoginSuccess() {

        ViewUserLoginMvp view = this.getView();
        if (view == null) {
            return;
        }

        OVLoginActivityUIUpdate uiUpdate = new OVLoginActivityUIUpdate(OVLoginActivityUIUpdate.UI_UPDATE_NAVIGATE_OVIEW_LIST, view);
        view.updateViewInMainThread(uiUpdate);
    }

    private void uiViewShowError(String type) {

        ViewUserLoginMvp view = this.getView();
        if (view == null) {
            return;
        }

        OVLoginActivityUIUpdate uiUpdate = new OVLoginActivityUIUpdate(OVLoginActivityUIUpdate.UI_UPDATE_SHOW_ERROR, view);
        uiUpdate.setFieldType(type);
        view.updateViewInMainThread(uiUpdate);
    }

    public class OVLoginActivityUIUpdate
            extends ActivityUIUpdate<ViewUserLoginMvp>
    {
        final static int UI_UPDATE_NAVIGATE_OVIEW_LIST = 0;

        final static int UI_UPDATE_SHOW_ERROR = 1;

        String fieldType = "";

        OVLoginActivityUIUpdate(int UpdateId, ViewUserLoginMvp view) {

            super(UpdateId, view);
        }

        void setFieldType(String fieldType) {

            this.fieldType = fieldType;
        }

        @Override
        protected void onUpdateEventChild(BaseMvpView view, int event) {

            ViewUserLoginMvp loginView = (ViewUserLoginMvp) view;

            switch (event) {
                case UI_UPDATE_NAVIGATE_OVIEW_LIST:
                    loginView.navigateToListView();
                    break;
                case UI_UPDATE_SHOW_ERROR:
                    loginView.showFailedError(fieldType);
                    break;
            }
        }
    }
}
