package com.leedian.oviewremote.view.viewInterface;

import com.leedian.oviewremote.base.baseView.BaseMvpView;

/**
 * ViewUserLoginMvp
 *
 * @author Franco
 */
public interface ViewUserLoginMvp
        extends BaseMvpView
{
    void showLoginForm();

    void showFailedError(String type);

    void navigateToListView();
}
