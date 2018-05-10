package com.leedian.oviewremote.model.restapi;

import okhttp3.Response;
import com.leedian.oviewremote.AppManager;
import com.leedian.oviewremote.model.cache.cacheImp.UserLoginCacheImp;
import com.leedian.oviewremote.model.cache.cacheInterface.UserLoginCache;
import com.leedian.oviewremote.model.dataIn.AuthCredentials;
import com.leedian.oviewremote.model.dataout.UserInfoModel;

/**
 * Oview User Api
 *
 * @author Franco
 */
public class OviewUserApi
        extends HttpApiBase
{
    private UserInfoModel userModel;

    /**
     * Get user information
     *
     * @return UserInfoModel
     */
    public UserInfoModel getUserModel() {

        return userModel;
    }

    /**
     * requestSignIn
     *
     * @param credentials
     * @return boolean
     * @throws Exception
     */
    public boolean requestSignIn(AuthCredentials credentials) throws Exception {

        boolean  isSuccessCall;
        Response response;
        String body;

        try {
            isSuccessCall = getApiRequest().loginRequest(credentials).connectSync();
            response = getApiRequest().getResponse();

            if (!isSuccessCall) {
                this.ParseResponseCode(response.header(RESPONSE_CODE, UNKNOWN_ERROR_CODE));
                return false;
            } else {
                body = response.body().string();
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }

        debugAssert(body != null);

        try {
            String jsonDataNode = "data";
            userModel = UserInfoModel.getModelFromJsonNode(body, jsonDataNode);
            userModel.setUser(credentials.getUsername());
            userModel.setBrand(UrlMessagesConstants.StrHttpServiceBrandName);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }

        return true;
    }

    public boolean requestSignOut(String session) throws Exception {

        boolean  isSuccessCall;
        Response response;
        String body;

        try {
            isSuccessCall = getApiRequest().logoutRequest(session).connectSync();
            response = getApiRequest().getResponse();

            if (!isSuccessCall) {
                this.ParseResponseCode(response.header(RESPONSE_CODE, UNKNOWN_ERROR_CODE));
                return false;
            } else {
                body = response.body().string();
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }

        return true;
    }




    public boolean checkAuthToken() throws Exception {


        boolean  isSuccessCall;
        Response response;
        String body;
        UserInfoModel user = AppManager.getUserLoginInfoData();

        try {
            isSuccessCall = getApiRequest().checkAuth().connectSync();
            response = getApiRequest().getResponse();

            if (!isSuccessCall) {
                this.ParseResponseCode(response.header(RESPONSE_CODE, UNKNOWN_ERROR_CODE));
                return false;
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }

        return true;
    }
    public boolean requestExchangeToken() throws Exception {


        boolean  isSuccessCall;
        Response response;
        String body;
        UserInfoModel user = AppManager.getUserLoginInfoData();

        try {
            isSuccessCall = getApiRequest().exchangeToken(user.getSession(),user.getRSession()).connectSync();
            response = getApiRequest().getResponse();

            if (!isSuccessCall) {
                this.ParseResponseCode(response.header(RESPONSE_CODE, UNKNOWN_ERROR_CODE));
                return false;
            } else {
                body = response.body().string();
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }

        debugAssert(body != null);

        try {

            UserLoginCache userCache = new UserLoginCacheImp();

            String jsonDataNode = "data";
            userModel = UserInfoModel.getModelFromJsonNode(body, jsonDataNode);
            userModel.setUser(user.getUser());
            userModel.setBrand(UrlMessagesConstants.StrHttpServiceBrandName);

            userCache.writeCache(userModel);

        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }

        return true;
    }
}
