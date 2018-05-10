package com.leedian.oviewremote.presenter.presenterInterface;

import com.leedian.oviewremote.view.viewInterface.ViewUserLoginMvp;

/**
 * UserLoginPresenter
 *
 * @author Franco
 */
public interface UserLoginPresenter extends BasePresenter<ViewUserLoginMvp> {

    void doUserLogin(String name, String password);
}
