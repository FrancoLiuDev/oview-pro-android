package com.leedian.oviewremote.base.baseView;
import com.hannesdorfmann.mosby.mvp.MvpView;


/**
 * MvpView for Mvc View in this App
 *
 * This MvpView is used as Base MvpView .
 *
 * @author Franco
 */
public interface BaseMvpView
        extends MvpView
{
    void updateViewInMainThread(ActivityUIUpdate viewUpdate);

    void navigateToAppHomeActivityCauseReAuth();

    void navigateToAppHomeActivity();

    void displayErrorResponse(String msg);

    void displayTipMassage(String msg);

    void reactiveAsInvalidView();

    void showLoadingDialog();

    void showLoadingDialogMsg(String msg);

    void hideLoadingDialog();

    void showLoadingDialogError();

    void showLoadingDialogSuccess();
}
