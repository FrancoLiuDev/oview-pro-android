package com.leedian.oviewremote.presenter.presenterInterface;

import com.hannesdorfmann.mosby.mvp.MvpPresenter;
import com.hannesdorfmann.mosby.mvp.MvpView;

/**
 * BasePresenter
 *
 * @author Franco
 */
public interface BasePresenter<V extends MvpView> extends MvpPresenter<V> {

    boolean getIsUserLogin();

    void onDisconnectWifi();


}


