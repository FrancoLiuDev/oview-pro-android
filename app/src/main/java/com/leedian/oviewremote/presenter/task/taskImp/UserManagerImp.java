package com.leedian.oviewremote.presenter.task.taskImp;

import rx.Observable;
import rx.Subscriber;
import com.leedian.oviewremote.model.cache.cacheImp.UserLoginCacheImp;
import com.leedian.oviewremote.model.cache.cacheInterface.UserLoginCache;
import com.leedian.oviewremote.model.dataIn.AuthCredentials;
import com.leedian.oviewremote.model.dataout.UserInfoModel;
import com.leedian.oviewremote.model.restapi.OviewUserApi;
import com.leedian.oviewremote.presenter.task.taskInterface.UserManager;
import com.leedian.oviewremote.utils.exception.DomainException;
import com.leedian.oviewremote.utils.subscript.ObservableActionIdentifier;

/**
 * UserManagerImp
 *
 * @author Franco
 */
public class UserManagerImp
        implements UserManager
{
    private UserLoginCache userCache = new UserLoginCacheImp();

    @Override
    public boolean isUserLogin() {

        return userCache.isCached();
    }

    @Override
    public void setUserLogin() {

    }

    @Override
    public void setUserLogout() {

        userCache.cleanCache();
    }

    @Override
    public UserInfoModel getUserLoginInfoData() {

        return userCache.getCache();
    }

    @Override
    public void setUserLoginInfoData(UserInfoModel info) {

        userCache.writeCache(info);
    }

    @Override
    public Observable<ObservableActionIdentifier> executeRequestUserLogin(final AuthCredentials Auth) {

        return Observable.create(
                new Observable.OnSubscribe<ObservableActionIdentifier>() {
                    @Override
                    public void call(Subscriber<? super ObservableActionIdentifier> subscriber) {

                        boolean      isSuccess;
                        OviewUserApi HttpApi = new OviewUserApi();

                        try {
                            isSuccess = HttpApi.requestSignIn(Auth);

                            if (!isSuccess) {
                                Exception exception = DomainException
                                        .buildHttpException(HttpApi.getMajor_code(), HttpApi
                                                .getMinor_code());
                                subscriber.onError(exception);
                                return;
                            }

                            UserInfoModel model = HttpApi.getUserModel();
                            UserManagerImp.this.setUserLoginInfoData(model);

                            subscriber.onCompleted();
                        } catch (Exception e) {
                            subscriber.onError(DomainException.getDomainException(e));
                        }
                    }
                });
    }

    @Override
    public Observable<ObservableActionIdentifier> executeRequestUserLogout() {
        return Observable.create(
                new Observable.OnSubscribe<ObservableActionIdentifier>() {
                    @Override
                    public void call(Subscriber<? super ObservableActionIdentifier> subscriber) {

                        UserInfoModel model =  UserManagerImp.this.getUserLoginInfoData();
                        boolean      isSuccess;
                        OviewUserApi HttpApi = new OviewUserApi();

                        try {
                            isSuccess = HttpApi.requestSignOut(model.getRSession());

                            if (!isSuccess) {
                                Exception exception = DomainException
                                        .buildHttpException(HttpApi.getMajor_code(), HttpApi
                                                .getMinor_code());
                                subscriber.onError(exception);
                                return;
                            }

                            setUserLogout();

                            subscriber.onCompleted();
                        } catch (Exception e) {
                            subscriber.onError(DomainException.getDomainException(e));
                        }
                    }
                });
    }
}
