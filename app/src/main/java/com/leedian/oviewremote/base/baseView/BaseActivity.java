package com.leedian.oviewremote.base.baseView;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;

import com.hannesdorfmann.mosby.mvp.MvpView;

import com.leedian.oviewremote.AppResource;
import com.leedian.oviewremote.R;
import com.leedian.oviewremote.navigator.AppNavigator;
import com.leedian.oviewremote.presenter.presenterInterface.BasePresenter;
import com.leedian.oviewremote.utils.ToastUtil;

/**
 * Activity for Mvc Activity in this App
 * <p>
 * This activity is used as Base Activity .
 *
 * @author Franco
 */
public abstract class BaseActivity<V extends MvpView, P extends BasePresenter<V>>
        extends BaseViewStateActivity<V, P>
        implements BaseMvpView

{
    private boolean keyboardShown             = false;
    private boolean keyboardListenersAttached = false;
    private ViewGroup rootLayout;
    private ProgressDialog progress;
    private ViewTreeObserver.OnGlobalLayoutListener keyboardLayoutListener = new ViewTreeObserver.OnGlobalLayoutListener() {
        @Override
        public void onGlobalLayout() {

            int heightDiff = rootLayout.getRootView()
                                       .getHeight() - rootLayout.getHeight();
            int contentViewTop = getWindow().findViewById(Window.ID_ANDROID_CONTENT)
                                            .getTop();
            LocalBroadcastManager broadcastManager = LocalBroadcastManager
                    .getInstance(BaseActivity.this);

            if (heightDiff < 300) {
                if (!keyboardShown) return;
                onKeyBoardHide();
                keyboardShown = false;
                Intent intent = new Intent("KeyboardWillHide");
                broadcastManager.sendBroadcast(intent);
            } else {
                if (keyboardShown) return;
                int keyboardHeight = heightDiff - contentViewTop;
                onKeyBoardShown();
                keyboardShown = true;
                Intent intent = new Intent("KeyboardWillShow");
                intent.putExtra("KeyboardHeight",
                                keyboardHeight);
                broadcastManager.sendBroadcast(intent);
            }
        }
    };

    protected abstract int getContentResourceId();

    protected void initKeyboardConfig(int Id) {

        if (keyboardListenersAttached) {
            return;
        }
        rootLayout = (ViewGroup) findViewById(Id);
        rootLayout.getViewTreeObserver()
                  .addOnGlobalLayoutListener(keyboardLayoutListener);
        keyboardListenersAttached = true;
    }

    protected void registerKeyboardCallBack() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(getContentResourceId());
    }

    @Override
    public void navigateToAppHomeActivity() {

        AppNavigator viewNavigator = new AppNavigator(BaseActivity.this);
        viewNavigator.navigateHome();
    }

    @Override
    public void reactiveAsInvalidView() {

        onInvalidView();
    }

    @Override
    public void navigateToAppHomeActivityCauseReAuth() {

        /*DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        AppNavigator viewNavigator = new AppNavigator(BaseActivity.this);
                        viewNavigator.navigateHome();
                        break;
                }
            }
        };
        DialogUtil.showConfirmOnlyConfirm(this,
                                          AppResource.getString(R.string.confirm_logout_confirm),
                                          AppResource.getString(R.string.confirm_ok),
                                          dialogClickListener
                                         );*/
    }

    @Override
    public void showLoadingDialog() {

        showActivityLoading();
    }

    @Override
    public void showLoadingDialogError() {

        showActivityLoadingError();
    }

    @Override
    public void showLoadingDialogSuccess() {

        showActivityLoadingSuccess();
    }

    @Override
    public void showLoadingDialogMsg(String msg) {

        showActivityLoadingMsg(msg);
    }

    @Override
    public void hideLoadingDialog() {

        hideActivityLoading();
    }

    protected void showActivityLoading() {

        if (progress == null) {
            progress = new ProgressDialog(this);
        }

        progress.setTitle(AppResource.getString(R.string.proceesing));
        progress.setMessage(AppResource.getString(R.string.please_wait));
        progress.setCancelable(false);

        progress.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        if (!progress.isShowing()) {
            progress.show();
        }
    }

    protected void showActivityLoadingError() {


    }
    protected void showActivityLoadingSuccess() {


    }


    protected void showActivityLoadingMsg(String msg) {

        if (progress == null) {
            progress = new ProgressDialog(this);
        }

        progress.setTitle(msg);
        progress.setMessage(AppResource.getString(R.string.please_wait));
        progress.setCancelable(false);

        progress.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        if (!progress.isShowing()) {
            progress.show();
        }
    }

    protected void hideActivityLoading() {


        if (progress != null) {
            progress.dismiss();
        }


    }

    @Override
    public void displayErrorResponse(String msg) {

        showActivityToast(msg);
    }

    @Override
    public void displayTipMassage(String msg) {

        showActivityToastTips(msg);
    }

    protected void showActivityToast(String msg) {

       ToastUtil.ShowToastWithDismissIcon(this,
                                           msg,
                                           ToastUtil.ToastStyle.TYPE_WARNING,
                                           getToastActivityContainer());
    }

    protected void showActivityToastTips(String msg) {

        /*ToastUtil.ShowToastWithDismissIcon(this,
                                           msg,
                                           ToastUtil.ToastStyle.TYPE_TIP,
                                           getToastActivityContainer());*/
    }

    protected int getToastActivityContainer() {

        return 0;
    }

    @Override
    protected void onDestroy() {


        try {
            if (progress != null ) {
                progress.dismiss();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        super.onDestroy();
        if (keyboardListenersAttached) {
           // Utils.removeOnGlobalLayoutListener(rootLayout, keyboardLayoutListener);
        }
    }

    public void updateViewInMainThread(ActivityUIUpdate viewUpdate) {

        runOnUiThread(viewUpdate);
    }

    protected void initVal() {


    }

    protected void startFetch() {

    }


    protected void initView() {

    }

    protected void initData() {

    }

    protected void onKeyBoardShown() {

    }

    protected void onKeyBoardHide() {

    }

    protected void onInvalidView() {

    }
}
