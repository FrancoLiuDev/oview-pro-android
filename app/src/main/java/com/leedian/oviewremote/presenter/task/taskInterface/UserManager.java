package com.leedian.oviewremote.presenter.task.taskInterface;

import rx.Observable;
import com.leedian.oviewremote.model.dataIn.AuthCredentials;
import com.leedian.oviewremote.model.dataout.UserInfoModel;
import com.leedian.oviewremote.utils.subscript.ObservableActionIdentifier;

/**
 * Created by franco on 19/11/2016.
 */
public interface UserManager {

    boolean isUserLogin();

    void setUserLogin();

    void setUserLogout();

    UserInfoModel getUserLoginInfoData();

    void setUserLoginInfoData(UserInfoModel Info);

    Observable<ObservableActionIdentifier> executeRequestUserLogin(AuthCredentials Auth);

    Observable<ObservableActionIdentifier> executeRequestUserLogout();
}
