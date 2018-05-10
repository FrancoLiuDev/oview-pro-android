package com.leedian.oviewremote.model.net;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import com.leedian.oviewremote.AppManager;
import com.leedian.oviewremote.model.dataout.UserInfoModel;
import com.leedian.oviewremote.model.restapi.OviewApi;
import com.leedian.oviewremote.model.restapi.OviewUserApi;
import com.leedian.oviewremote.presenter.task.taskImp.UserManagerImp;
import com.leedian.oviewremote.utils.exception.DomainException;

/**
 * Api Connection class used to retrieve data from the cloud.
 * Implements {@link Callable} so when executed asynchronously can
 * return a value.
 *
 * @author Franco
 */
public class ApiConnection {
    private static final String CONTENT_TYPE_LABEL = "Content-Type";
    private static final String CONTENT_TYPE_VALUE_JSON = "application/json; charset=utf-8";
    private static final String KEY_K_TOKEN = "k-token";
    private static final String DEFAULT_TOKEN = "abc123";
    private Request request;
    private URL url;
    private Response httpResponse;

    /**
     * Constructor
     *
     * @param url    the url
     * @param method the method
     * @param body   the RequestBody
     **/
    private ApiConnection(String url, String method, RequestBody body) throws MalformedURLException {

        this.url = new URL(url);
        this.request = buildRequest(method, body);
    }

    /**
     * Get ApiConnection instance
     *
     * @param url    the url
     * @param method the method
     * @param body   the RequestBody
     **/
    public static ApiConnection instance(String url, String method, RequestBody body) throws MalformedURLException {

        return new ApiConnection(url, method, body);
    }

    /**
     * Get X token
     **/
    private String getXToken() {

        return DEFAULT_TOKEN;
    }

    /**
     * Get Application Request Header
     *
     * @param builder the builder
     **/
    private Request.Builder getApplicationRequestToken(Request.Builder builder) {

        Request.Builder NewBuilder;
        if (AppManager.isUserLogin()) {
            UserInfoModel user = AppManager.getUserLoginInfoData();
            NewBuilder = builder.addHeader(CONTENT_TYPE_LABEL, CONTENT_TYPE_VALUE_JSON)
                    .addHeader("k-brand", user.getBrand())
                    .addHeader("k-user", user.getUser())
                    .addHeader("k-session", user.getSession());
                   // .addHeader("k-session", " eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1aWQiOjEsImJpZCI6MSwidW5hbWUiOiJsZWVkaWFuIiwiYm5hbWUiOiJsZWVkaWFuIiwiY2RhdGUiOiIyMDE3LTA0LTE3VDA4OjQ3OjEzLjcxOVoiLCJpYXQiOjE0OTI0MTg4MzN9.ZvSyzUoma615eWGszeS2G9OIGCxDfR30lsIVK-ziXLA");
             } else {
            NewBuilder = builder.addHeader(CONTENT_TYPE_LABEL, CONTENT_TYPE_VALUE_JSON)
                    .addHeader(CONTENT_TYPE_LABEL, CONTENT_TYPE_VALUE_JSON)
                    .addHeader(KEY_K_TOKEN, getXToken());
        }
        return NewBuilder;
    }

    /**
     * Build Request
     *
     * @param method the method
     * @param body   the body
     **/
    private Request buildRequest(String method, RequestBody body) throws MalformedURLException {

        Request.Builder builder = new Request.Builder();
        builder = getApplicationRequestToken(builder).url(this.url);
        request = null;
        if (method.equals("Get")) {
            request = builder
                    .get()
                    .build();
        }
        if (method.equals("Post")) {
            request = builder
                    .post(body)
                    .build();
        }
        if (method.equals("Put")) {
            request = builder
                    .put(body)
                    .build();
        }
        if (method.equals("Patch")) {
            request = builder
                    .patch(body)
                    .build();
        }
        if (method.equals("Delete")) {
            if (body != null) {
                request = builder
                        .delete(body)
                        .build();
            } else {
                request = builder
                        .delete()
                        .build();
            }
        }
        if (method.equals("Download")) {
            request = builder
                    .build();
        }
        return GetRequest();
    }

    /**
     * Get Request
     **/
    private Request GetRequest() throws MalformedURLException {

        return request;
    }

    /**
     * Get Response
     **/
    public Response GetResponse() throws MalformedURLException {

        return httpResponse;
    }

    /**
     * Async Connect To Server
     *
     * @param callback the callback
     **/
    public void connectToApiAsync(Callback callback) throws IOException {

        OkHttpClient okHttpClient = this.createClient();
        okHttpClient.newCall(request).enqueue(callback);
    }

    /**
     * Connect To Server
     **/
    public void setTestRelog()
    {
        test = true;
    }
    static boolean test = false;
    public boolean connectToApi() throws IOException {

        OviewApi HttpApi = new OviewApi();

        OkHttpClient okHttpClient = this.createClient();
        this.httpResponse = okHttpClient.newCall(request).execute();

        String head = this.httpResponse.header("code", "99");

        if (head.equals("01.003")|| test){
            test = false;
            if (reSignIn()){
                this.httpResponse = okHttpClient.newCall(request).execute();
            }
        }

        return this.httpResponse.isSuccessful();
    }

    private boolean reSignIn(){

        boolean      isSuccess =false;
        OviewUserApi HttpApi = new OviewUserApi();

        try {
            isSuccess = HttpApi.requestExchangeToken();

            if (!isSuccess) {
                return false;
            }

            UserInfoModel model = HttpApi.getUserModel();


        } catch (Exception e) {

        }

        return isSuccess;
    }
    /**
     * Create Client
     **/
    private OkHttpClient createClient() {

        return new OkHttpClient.Builder()
                .connectTimeout(25000, TimeUnit.MILLISECONDS)
                .retryOnConnectionFailure(true)
                .readTimeout(100, TimeUnit.SECONDS)
                .build();
    }
}