/*
 * Copyright 2014 Sony Corporation
 */

package com.leedian.oviewremote.presenter.camera;

import android.util.Log;

import com.leedian.oviewremote.utils.JsonConvertUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Simple Camera Remote API wrapper class. (JSON based API <--> Java API)
 */
public class CameraRemoteApi {

    private static final String TAG = CameraRemoteApi.class.getSimpleName();

    // If you'd like to suppress detailed log output, change this value into
    // false.
    private static final boolean FULL_LOG = true;

    // API server device you want to send requests.
    private ServerDevice mTargetServer;

    // Request ID of API calling. This will be counted up by each API calling.
    private int mRequestId;

    /**
     * Constructor.
     *
     * @param target server device of Remote API
     */
    public CameraRemoteApi(ServerDevice target) {
        mTargetServer = target;
        mRequestId = 1;
    }

    /**
     * Parse JSON and return whether it has error or not.
     *
     * @param replyJson JSON object to check
     * @return return true if JSON has error. otherwise return false.
     */
    public static boolean isErrorReply(JSONObject replyJson) {
        boolean hasError = (replyJson != null && replyJson.has("error"));
        return hasError;
    }

    /**
     * Retrieves Action List URL from Server information.
     *
     * @param service
     * @return
     * @throws IOException
     */
    private String findActionListUrl(String service) throws IOException {
        List<ServerDevice.ApiService> services = mTargetServer.getApiServices();
        for (ServerDevice.ApiService apiService : services) {
            if (apiService.getName().equals(service)) {
                return apiService.getActionListUrl();
            }
        }
        throw new IOException("actionUrl not found. service : " + service);
    }

    /**
     * Request ID. Counted up after calling.
     *
     * @return
     */
    private int id() {
        return mRequestId++;
    }

    // Camera Service APIs

    // Output a log line.
    private void log(String msg) {
        if (FULL_LOG) {
            Log.d(TAG, msg);
        }
    }

    /**
     * Calls getAvailableApiList API to the target server. Request JSON data is
     * such like as below.
     * <p>
     * <pre>
     * {
     *   "method": "getAvailableApiList",
     *   "params": [""],
     *   "id": 2
     *   "version": "1.0"
     * }
     * </pre>
     *
     * @return JSON data of response
     * @throws IOException all errors and exception are wrapped by this
     *                     Exception.
     */
    public JSONObject getAvailableApiList() throws IOException {
        String service = "camera";
        try {
            JSONObject requestJson =
                    new JSONObject().put("method", "getAvailableApiList")
                            .put("params", new JSONArray()).put("id", id())
                            .put("version", "1.0");
            String url = findActionListUrl(service) + "/" + service;

            log("Request:  " + requestJson.toString());
            String responseJson = CameraHttpClient.httpPost(url, requestJson.toString());
            log("Response: " + responseJson);
            return new JSONObject(responseJson);
        } catch (JSONException e) {
            throw new IOException(e);
        }
    }

    public JSONObject getSupportedExposureMode() throws IOException {
        String service = "camera";
        try {
            JSONObject requestJson =
                    new JSONObject().put("method", "getSupportedExposureMode")
                            .put("params", new JSONArray()).put("id", id())
                            .put("version", "1.0");
            String url = findActionListUrl(service) + "/" + service;

            log("Request:  " + requestJson.toString());
            String responseJson = CameraHttpClient.httpPost(url, requestJson.toString());
            log("Response: " + responseJson);
            return new JSONObject(responseJson);
        } catch (JSONException e) {
            throw new IOException(e);
        }
    }

    /**
     * Calls getApplicationInfo API to the target server. Request JSON data is
     * such like as below.
     * <p>
     * <pre>
     * {
     *   "method": "getApplicationInfo",
     *   "params": [""],
     *   "id": 2,
     *   "version": "1.0"
     * }
     * </pre>
     *
     * @return JSON data of response
     * @throws IOException all errors and exception are wrapped by this
     *                     Exception.
     */
    public JSONObject getApplicationInfo() throws IOException {
        String service = "camera";
        try {
            JSONObject requestJson =
                    new JSONObject().put("method", "getApplicationInfo") //
                            .put("params", new JSONArray()).put("id", id()) //
                            .put("version", "1.0");
            String url = findActionListUrl(service) + "/" + service;

            log("Request:  " + requestJson.toString());
            String responseJson = CameraHttpClient.httpPost(url, requestJson.toString());
            log("Response: " + responseJson);
            return new JSONObject(responseJson);
        } catch (JSONException e) {
            throw new IOException(e);
        }
    }

    public ApiResult getFNumber() throws IOException {
        return getCameraSettingParm("getFNumber");
       /* String service = "camera";
        try {
            JSONObject requestJson =
                    new JSONObject().put("method", "getFNumber") //
                            .put("params", new JSONArray()).put("id", id()) //
                            .put("version", "1.0");
            String url = findActionListUrl(service) + "/" + service;

            log("Request:  " + requestJson.toString());
            String responseJson = CameraHttpClient.httpPost(url, requestJson.toString());
            log("Response: " + responseJson);

            ApiResult result = new ApiResult(responseJson);

            return result;
        } catch (JSONException e) {
            throw new IOException(e);
        }*/
    }

    public ApiResult getIsoSpeedRate() throws IOException {

        return getCameraSettingParm("getIsoSpeedRate");
    }

    public ApiResult getAvailableIsoSpeedRate() throws IOException {

        return getCameraSettingParm("getAvailableIsoSpeedRate");
        /*String service = "camera";
        try {
            JSONObject requestJson =
                    new JSONObject().put("method", "getAvailableIsoSpeedRate") //
                            .put("params", new JSONArray()).put("id", id()) //
                            .put("version", "1.0");
            String url = findActionListUrl(service) + "/" + service;

            log("Request:  " + requestJson.toString());
            String responseJson = CameraHttpClient.httpPost(url, requestJson.toString());
            log("Response: " + responseJson);

            ApiResult result = new ApiResult(responseJson);

            return result;
        } catch (JSONException e) {
            throw new IOException(e);
        }*/
    }

    public ApiResult getExposureCompensation() throws IOException {

        return getCameraSettingParm("getExposureCompensation");
        /*String service = "camera";
        try {
            JSONObject requestJson =
                    new JSONObject().put("method", "getAvailableIsoSpeedRate") //
                            .put("params", new JSONArray()).put("id", id()) //
                            .put("version", "1.0");
            String url = findActionListUrl(service) + "/" + service;

            log("Request:  " + requestJson.toString());
            String responseJson = CameraHttpClient.httpPost(url, requestJson.toString());
            log("Response: " + responseJson);

            ApiResult result = new ApiResult(responseJson);

            return result;
        } catch (JSONException e) {
            throw new IOException(e);
        }*/
    }

    public ApiResult setIsoSpeedRate(String iso) throws IOException {

        return setCameraSettingParm("setIsoSpeedRate", iso);
    }

    public ApiResult getShutterSpeed() throws IOException {

        return getCameraSettingParm("getShutterSpeed");
    }

    public ApiResult setCameraSettingParm(String parm, String value) throws IOException {
        String service = "camera";
        try {
            JSONObject requestJson =
                    new JSONObject().put("method", parm) //
                            .put("params", new JSONArray().put(value))
                            .put("id", id())
                            .put("version", "1.0");
            String url = findActionListUrl(service) + "/" + service;

            log("Request:  " + requestJson.toString());
            String responseJson = CameraHttpClient.httpPost(url, requestJson.toString());
            log("Response: " + responseJson);

            ApiResult result = new ApiResult(responseJson);

            return result;
        } catch (JSONException e) {
            throw new IOException(e);
        }
    }

    public ApiResult getCameraSettingParm(String parm) throws IOException {
        String service = "camera";
        try {
            JSONObject requestJson =
                    new JSONObject().put("method", parm) //
                            .put("params", new JSONArray()).put("id", id()) //
                            .put("version", "1.0");
            String url = findActionListUrl(service) + "/" + service;

            log("Request:  " + requestJson.toString());
            String responseJson = CameraHttpClient.httpPost(url, requestJson.toString());
            log("Response: " + responseJson);

            ApiResult result = new ApiResult(responseJson);

            return result;
        } catch (JSONException e) {
            throw new IOException(e);
        }
    }

    /**
     * Calls getShootMode API to the target server. Request JSON data is such
     * like as below.
     * <p>
     * <pre>
     * {
     *   "method": "getShootMode",
     *   "params": [],
     *   "id": 2,
     *   "version": "1.0"
     * }
     * </pre>
     *
     * @return JSON data of response
     * @throws IOException all errors and exception are wrapped by this
     *                     Exception.
     */
    public JSONObject getShootMode() throws IOException {
        String service = "camera";
        try {
            JSONObject requestJson =
                    new JSONObject().put("method", "getShootMode").put("params", new JSONArray()) //
                            .put("id", id()).put("version", "1.0");
            String url = findActionListUrl(service) + "/" + service;

            log("Request:  " + requestJson.toString());
            String responseJson = CameraHttpClient.httpPost(url, requestJson.toString());
            log("Response: " + responseJson);
            return new JSONObject(responseJson);
        } catch (JSONException e) {
            throw new IOException(e);
        }
    }

    /**
     * Calls setShootMode API to the target server. Request JSON data is such
     * like as below.
     * <p>
     * <pre>
     * {
     *   "method": "setShootMode",
     *   "params": ["still"],
     *   "id": 2,
     *   "version": "1.0"
     * }
     * </pre>
     *
     * @param shootMode shoot mode (ex. "still")
     * @return JSON data of response
     * @throws IOException all errors and exception are wrapped by this
     *                     Exception.
     */
    public JSONObject setShootMode(String shootMode) throws IOException {
        String service = "camera";
        try {
            JSONObject requestJson =
                    new JSONObject().put("method", "setShootMode") //
                            .put("params", new JSONArray().put(shootMode)) //
                            .put("id", id()).put("version", "1.0");
            String url = findActionListUrl(service) + "/" + service;

            log("Request:  " + requestJson.toString());
            String responseJson = CameraHttpClient.httpPost(url, requestJson.toString());
            log("Response: " + responseJson);
            return new JSONObject(responseJson);
        } catch (JSONException e) {
            throw new IOException(e);
        }
    }

    /**
     * Calls getAvailableShootMode API to the target server. Request JSON data
     * is such like as below.
     * <p>
     * <pre>
     * {
     *   "method": "getAvailableShootMode",
     *   "params": [],
     *   "id": 2,
     *   "version": "1.0"
     * }
     * </pre>
     *
     * @return JSON data of response
     * @throws
     */
    public JSONObject getAvailableShootMode() throws IOException {
        String service = "camera";
        try {
            JSONObject requestJson =
                    new JSONObject().put("method", "getAvailableShootMode") //
                            .put("params", new JSONArray()).put("id", id()) //
                            .put("version", "1.0");
            String url = findActionListUrl(service) + "/" + service;

            log("Request:  " + requestJson.toString());
            String responseJson = CameraHttpClient.httpPost(url, requestJson.toString());
            log("Response: " + responseJson);
            return new JSONObject(responseJson);
        } catch (JSONException e) {
            throw new IOException(e);
        }
    }

    /**
     * Calls getSupportedShootMode API to the target server. Request JSON data
     * is such like as below.
     * <p>
     * <pre>
     * {
     *   "method": "getSupportedShootMode",
     *   "params": [],
     *   "id": 2,
     *   "version": "1.0"
     * }
     * </pre>
     *
     * @return JSON data of response
     * @throws IOException all errors and exception are wrapped by this
     *                     Exception.
     */
    public JSONObject getSupportedShootMode() throws IOException {
        String service = "camera";
        try {
            JSONObject requestJson =
                    new JSONObject().put("method", "getSupportedShootMode") //
                            .put("params", new JSONArray()).put("id", id()) //
                            .put("version", "1.0");
            String url = findActionListUrl(service) + "/" + service;

            log("Request:  " + requestJson.toString());
            String responseJson = CameraHttpClient.httpPost(url, requestJson.toString());
            log("Response: " + responseJson);
            return new JSONObject(responseJson);
        } catch (JSONException e) {
            throw new IOException(e);
        }
    }

    /**
     * Calls startLiveview API to the target server. Request JSON data is such
     * like as below.
     * <p>
     * <pre>
     * {
     *   "method": "startLiveview",
     *   "params": [],
     *   "id": 2,
     *   "version": "1.0"
     * }
     * </pre>
     *
     * @return JSON data of response
     * @throws IOException all errors and exception are wrapped by this
     *                     Exception.
     */
    public JSONObject startLiveview() throws IOException {
        String service = "camera";
        try {
            JSONObject requestJson =
                    new JSONObject().put("method", "startLiveview").put("params", new JSONArray()) //
                            .put("id", id()).put("version", "1.0");
            String url = findActionListUrl(service) + "/" + service;

            log("Request:  " + requestJson.toString());
            String responseJson = CameraHttpClient.httpPost(url, requestJson.toString());
            log("Response: " + responseJson);
            return new JSONObject(responseJson);
        } catch (JSONException e) {
            throw new IOException(e);
        }
    }

    /**
     * Calls stopLiveview API to the target server. Request JSON data is such
     * like as below.
     * <p>
     * <pre>
     * {
     *   "method": "stopLiveview",
     *   "params": [],
     *   "id": 2,
     *   "version": "1.0"
     * }
     * </pre>
     *
     * @return JSON data of response
     * @throws IOException all errors and exception are wrapped by this
     *                     Exception.
     */
    public JSONObject stopLiveview() throws IOException {
        String service = "camera";
        try {
            JSONObject requestJson =
                    new JSONObject().put("method", "stopLiveview").put("params", new JSONArray()) //
                            .put("id", id()).put("version", "1.0");
            String url = findActionListUrl(service) + "/" + service;

            log("Request:  " + requestJson.toString());
            String responseJson = CameraHttpClient.httpPost(url, requestJson.toString());
            log("Response: " + responseJson);
            return new JSONObject(responseJson);
        } catch (JSONException e) {
            throw new IOException(e);
        }
    }

    /**
     * Calls startRecMode API to the target server. Request JSON data is such
     * like as below.
     * <p>
     * <pre>
     * {
     *   "method": "startRecMode",
     *   "params": [],
     *   "id": 2,
     *   "version": "1.0"
     * }
     * </pre>
     *
     * @return JSON data of response
     * @throws IOException all errors and exception are wrapped by this
     *                     Exception.
     */
    public JSONObject startRecMode() throws IOException {
        String service = "camera";
        try {
            JSONObject requestJson =
                    new JSONObject().put("method", "startRecMode").put("params", new JSONArray()) //
                            .put("id", id()).put("version", "1.0");
            String url = findActionListUrl(service) + "/" + service;

            log("Request:  " + requestJson.toString());
            String responseJson = CameraHttpClient.httpPost(url, requestJson.toString());
            log("Response: " + responseJson);
            return new JSONObject(responseJson);
        } catch (JSONException e) {
            throw new IOException(e);
        }
    }

    public ApiResult getExposureMode() throws IOException {

        return getCameraSettingParm("getExposureMode");
       /* String service = "camera";
        try {
            JSONObject requestJson =
                    new JSONObject().put("method", "getExposureMode").put("params", new JSONArray()) //
                            .put("id", id()).put("version", "1.0");
            String url = findActionListUrl(service) + "/" + service;

            log("Request:  " + requestJson.toString());
            String responseJson = CameraHttpClient.httpPost(url, requestJson.toString());
            log("Response: " + responseJson);

            ApiResult result = new ApiResult(responseJson);

            return result;
        } catch (JSONException e) {
            throw new IOException(e);
        }*/
    }

    public JSONObject setExposureMode(String mode) throws IOException {
        String service = "camera";
        try {
            JSONObject requestJson = new JSONObject().put("method", "setExposureMode").put("params", new JSONArray().put(mode)) //
                    .put("id", id()).put("version", "1.0");
            String url = findActionListUrl(service) + "/" + service;

            log("Request:  " + requestJson.toString());
            String responseJson = CameraHttpClient.httpPost(url, requestJson.toString());
            log("Response: " + responseJson);
            return new JSONObject(responseJson);
        } catch (JSONException e) {
            throw new IOException(e);
        }
    }

    /**
     * Calls actTakePicture API to the target server. Request JSON data is such
     * like as below.
     * <p>
     * <pre>
     * {
     *   "method": "actTakePicture",
     *   "params": [],
     *   "id": 2,
     *   "version": "1.0"
     * }
     * </pre>
     *
     * @return JSON data of response
     * @throws IOException
     */
    public ApiResult actTakePicture() throws IOException {
        String service = "camera";
        try {
            JSONObject requestJson =
                    new JSONObject().put("method", "actTakePicture").put("params", new JSONArray()) //
                            .put("id", id()).put("version", "1.0");
            String url = findActionListUrl(service) + "/" + service;

            log("Request:  " + requestJson.toString());
            String responseJson = CameraHttpClient.httpPost(url, requestJson.toString());
            log("Response: " + responseJson);

            ApiResult result = new ApiResult(responseJson);

            return result;
        } catch (JSONException e) {
            throw new IOException(e);
        }


    }

    /**
     * Calls startMovieRec API to the target server. Request JSON data is such
     * like as below.
     * <p>
     * <pre>
     * {
     *   "method": "startMovieRec",
     *   "params": [],
     *   "id": 2,
     *   "version": "1.0"
     * }
     * </pre>
     *
     * @return JSON data of response
     * @throws IOException all errors and exception are wrapped by this
     *                     Exception.
     */
    public JSONObject startMovieRec() throws IOException {
        String service = "camera";
        try {
            JSONObject requestJson =
                    new JSONObject().put("method", "startMovieRec").put("params", new JSONArray()) //
                            .put("id", id()).put("version", "1.0");
            String url = findActionListUrl(service) + "/" + service;

            log("Request:  " + requestJson.toString());
            String responseJson = CameraHttpClient.httpPost(url, requestJson.toString());
            log("Response: " + responseJson);
            return new JSONObject(responseJson);
        } catch (JSONException e) {
            throw new IOException(e);
        }
    }

    /**
     * Calls stopMovieRec API to the target server. Request JSON data is such
     * like as below.
     * <p>
     * <pre>
     * {
     *   "method": "stopMovieRec",
     *   "params": [],
     *   "id": 2,
     *   "version": "1.0"
     * }
     * </pre>
     *
     * @return JSON data of response
     * @throws IOException all errors and exception are wrapped by this
     *                     Exception.
     */
    public ApiResult stopMovieRec() throws IOException {
        String service = "camera";
        try {
            JSONObject requestJson =
                    new JSONObject().put("method", "stopMovieRec").put("params", new JSONArray()) //
                            .put("id", id()).put("version", "1.0");
            String url = findActionListUrl(service) + "/" + service;

            log("Request:  " + requestJson.toString());
            String responseJson = CameraHttpClient.httpPost(url, requestJson.toString());
            log("Response: " + responseJson);
            return new ApiResult(responseJson);
        } catch (JSONException e) {
            throw new IOException(e);
        }
    }

    /**
     * Calls actZoom API to the target server. Request JSON data is such like as
     * below.
     * <p>
     * <pre>
     * {
     *   "method": "actZoom",
     *   "params": ["in","stop"],
     *   "id": 2,
     *   "version": "1.0"
     * }
     * </pre>
     *
     * @param direction direction of zoom ("in" or "out")
     * @param movement  zoom movement ("start", "stop", or "1shot")
     * @return JSON data of response
     * @throws IOException all errors and exception are wrapped by this
     *                     Exception.
     */
    public ApiResult actZoom(String direction, String movement) throws IOException {
        String service = "camera";
        try {
            JSONObject requestJson =
                    new JSONObject().put("method", "actZoom") //
                            .put("params", new JSONArray().put(direction).put(movement)) //
                            .put("id", id()).put("version", "1.0");
            String url = findActionListUrl(service) + "/" + service;

            log("Request:  " + requestJson.toString());
            String responseJson = CameraHttpClient.httpPost(url, requestJson.toString());
            log("Response: " + responseJson);
            return new ApiResult(responseJson);
        } catch (JSONException e) {
            throw new IOException(e);
        }
    }

    public ApiResult setTouchAFPosition(float x, float y) throws IOException {

        String service = "camera";
        try {
            JSONObject requestJson =
                    new JSONObject().put("method", "setTouchAFPosition") //
                            .put("params", new JSONArray().put(x).put(y)) //
                            .put("id", id()).put("version", "1.0");
            String url = findActionListUrl(service) + "/" + service;

            log("Request:  " + requestJson.toString());
            String responseJson = CameraHttpClient.httpPost(url, requestJson.toString());
            log("Response: " + responseJson);
            return new ApiResult(responseJson);
        } catch (JSONException e) {
            throw new IOException(e);
        }
    }

    public ApiResult actHalfPressShutter() throws IOException {

        String service = "camera";
        try {
            JSONObject requestJson =
                    new JSONObject().put("method", "actHalfPressShutter") //
                            .put("params", new JSONArray()) //
                            .put("id", id()).put("version", "1.0");
            String url = findActionListUrl(service) + "/" + service;

            log("Request:  " + requestJson.toString());
            String responseJson = CameraHttpClient.httpPost(url, requestJson.toString());
            log("Response: " + responseJson);
            return new ApiResult(responseJson);
        } catch (JSONException e) {
            throw new IOException(e);
        }
    }


    public ApiResult cancelHalfPressShutter() throws IOException {

        String service = "camera";
        try {
            JSONObject requestJson =
                    new JSONObject().put("method", "cancelHalfPressShutter") //
                            .put("params", new JSONArray()) //
                            .put("id", id()).put("version", "1.0");
            String url = findActionListUrl(service) + "/" + service;

            log("Request:  " + requestJson.toString());
            String responseJson = CameraHttpClient.httpPost(url, requestJson.toString());
            log("Response: " + responseJson);
            return new ApiResult(responseJson);
        } catch (JSONException e) {
            throw new IOException(e);
        }
    }
    /**
     * Calls getEvent API to the target server. Request JSON data is such like
     * as below.
     * <p>
     * <pre>
     * {
     *   "method": "getEvent",
     *   "params": [true],
     *   "id": 2,
     *   "version": "1.0"
     * }
     * </pre>
     *
     * @param longPollingFlag true means long polling request.
     * @return JSON data of response
     * @throws IOException all errors and exception are wrapped by this
     *                     Exception.
     */
    public JSONObject getEvent(boolean longPollingFlag) throws IOException {
        String service = "camera";
        try {
            JSONObject requestJson =
                    new JSONObject().put("method", "getEvent") //
                            .put("params", new JSONArray().put(longPollingFlag)) //
                            .put("id", id()).put("version", "1.0");
            String url = findActionListUrl(service) + "/" + service;
            int longPollingTimeout = (longPollingFlag) ? 20000 : 8000; // msec

            log("Request:  " + requestJson.toString());
            String responseJson = CameraHttpClient.httpPost(url, requestJson.toString(),
                    longPollingTimeout);
            log("Response: " + responseJson);
            return new JSONObject(responseJson);
        } catch (JSONException e) {
            throw new IOException(e);
        }
    }

    /**
     * Calls setCameraFunction API to the target server. Request JSON data is
     * such like as below.
     * <p>
     * <pre>
     * {
     *   "method": "setCameraFunction",
     *   "params": ["Remote Shooting"],
     *   "id": 2,
     *   "version": "1.0"
     * }
     * </pre>
     *
     * @param cameraFunction camera function to set
     * @return JSON data of response
     * @throws IOException all errors and exception are wrapped by this
     *                     Exception.
     */
    public ApiResult setCameraFunction(String cameraFunction) throws IOException {
        String service = "camera";
        try {
            JSONObject requestJson =
                    new JSONObject().put("method", "setCameraFunction") //
                            .put("params", new JSONArray().put(cameraFunction)) //
                            .put("id", id()).put("version", "1.0");
            String url = findActionListUrl(service) + "/" + service;

            log("Request:  " + requestJson.toString());
            String responseJson = CameraHttpClient.httpPost(url, requestJson.toString());
            log("Response: " + responseJson);

            ApiResult result = new ApiResult(responseJson);
            return result;
        } catch (JSONException e) {
            throw new IOException(e);
        }
    }

    public ApiResult getCameraFunction() throws IOException {
        String service = "camera";
        try {
            JSONObject requestJson =
                    new JSONObject().put("method", "getCameraFunction") //
                            .put("params", new JSONArray()) //
                            .put("id", id()).put("version", "1.0");
            String url = findActionListUrl(service) + "/" + service;

            log("Request:  " + requestJson.toString());
            String responseJson = CameraHttpClient.httpPost(url, requestJson.toString());
            log("Response: " + responseJson);

            ApiResult result = new ApiResult(responseJson);
            return result;
        } catch (JSONException e) {
            throw new IOException(e);
        }
    }

    public ApiResult getStorageInformation() throws IOException {
        String service = "camera";
        try {
            JSONObject requestJson =
                    new JSONObject().put("method", "getStorageInformation") //
                            .put("params", new JSONArray()) //
                            .put("id", id()).put("version", "1.0");
            String url = findActionListUrl(service) + "/" + service;

            log("Request:  " + requestJson.toString());
            String responseJson = CameraHttpClient.httpPost(url, requestJson.toString());
            log("Response: " + responseJson);

            ApiResult result = new ApiResult(responseJson);
            return result;
        } catch (JSONException e) {
            throw new IOException(e);
        }
    }



    public ApiResult getPostviewImageSize() throws IOException {
        String service = "camera";
        try {
            JSONObject requestJson =
                    new JSONObject().put("method", "getPostviewImageSize") //
                            .put("params", new JSONArray()) //
                            .put("id", id()).put("version", "1.0");
            String url = findActionListUrl(service) + "/" + service;

            log("Request:  " + requestJson.toString());
            String responseJson = CameraHttpClient.httpPost(url, requestJson.toString());
            log("Response: " + responseJson);

            ApiResult result = new ApiResult(responseJson);
            return result;
        } catch (JSONException e) {
            throw new IOException(e);
        }
    }
    public ApiResult getSupportedPostviewImageSize() throws IOException {
        String service = "camera";
        try {
            JSONObject requestJson =
                    new JSONObject().put("method", "getSupportedPostviewImageSize") //
                            .put("params", new JSONArray()) //
                            .put("id", id()).put("version", "1.0");
            String url = findActionListUrl(service) + "/" + service;

            log("Request:  " + requestJson.toString());
            String responseJson = CameraHttpClient.httpPost(url, requestJson.toString());
            log("Response: " + responseJson);

            ApiResult result = new ApiResult(responseJson);
            return result;
        } catch (JSONException e) {
            throw new IOException(e);
        }
    }

    // Avcontent APIs

    /**
     * Calls getMethodTypes API of Camera service to the target server. Request
     * JSON data is such like as below.
     * <p>
     * <pre>
     * {
     *   "method": "getMethodTypes",
     *   "params": ["1.0"],
     *   "id": 2,
     *   "version": "1.0"
     * }
     * </pre>
     *
     * @return JSON data of response
     * @throws IOException all errors and exception are wrapped by this
     *                     Exception.
     */
    public JSONObject getCameraMethodTypes() throws IOException {
        String service = "camera";
        try {
            JSONObject requestJson =
                    new JSONObject().put("method", "getMethodTypes") //
                            .put("params", new JSONArray().put("")) //
                            .put("id", id()).put("version", "1.0");
            String url = findActionListUrl(service) + "/" + service;

            log("Request:  " + requestJson.toString());
            String responseJson = CameraHttpClient.httpPost(url, requestJson.toString());
            log("Response: " + responseJson);
            return new JSONObject(responseJson);
        } catch (JSONException e) {
            throw new IOException(e);
        }
    }

    /**
     * Calls getMethodTypes API of AvContent service to the target server.
     * Request JSON data is such like as below.
     * <p>
     * <pre>
     * {
     *   "method": "getMethodTypes",
     *   "params": ["1.0"],
     *   "id": 2,
     *   "version": "1.0"
     * }
     * </pre>
     *
     * @return JSON data of response
     * @throws IOException all errors and exception are wrapped by this
     *                     Exception.
     */
    public JSONObject getAvcontentMethodTypes() throws IOException {
        String service = "avContent";
        try {
            String url = findActionListUrl(service) + "/" + service;
            JSONObject requestJson =
                    new JSONObject().put("method", "getMethodTypes") //
                            .put("params", new JSONArray().put("")) //
                            .put("id", id()).put("version", "1.0"); //

            log("Request:  " + requestJson.toString());
            String responseJson = CameraHttpClient.httpPost(url, requestJson.toString());
            log("Response: " + responseJson);
            return new JSONObject(responseJson);
        } catch (JSONException e) {
            throw new IOException(e);
        }
    }

    /**
     * Calls getSchemeList API to the target server. Request JSON data is such
     * like as below.
     * <p>
     * <pre>
     * {
     *   "method": "getSchemeList",
     *   "params": [],
     *   "id": 2,
     *   "version": "1.0"
     * }
     * </pre>
     *
     * @return JSON data of response
     * @throws IOException all errors and exception are wrapped by this
     *                     Exception.
     */

    public ApiResult getSchemeList() throws IOException {
        String service = "avContent";
        try {
            JSONObject requestJson =
                    new JSONObject().put("method", "getSchemeList").put("params", new JSONArray()) //
                            .put("id", id()).put("version", "1.0");
            String url = findActionListUrl(service) + "/" + service;

            log("Request:  " + requestJson.toString());
            String responseJson = CameraHttpClient.httpPost(url, requestJson.toString());
            log("Response: " + responseJson);

            ApiResult result = new ApiResult(responseJson);
            return result;
        } catch (JSONException e) {
            throw new IOException(e);
        }
    }

    public ApiResult getAvailableStillSize() throws IOException {
        String service = "camera";
        try {
            JSONObject requestJson =
                    new JSONObject().put("method", "getAvailableStillSize") //
                            .put("params", new JSONArray()) //
                            .put("id", id()).put("version", "1.0");
            String url = findActionListUrl(service) + "/" + service;

            log("Request:  " + requestJson.toString());
            String responseJson = CameraHttpClient.httpPost(url, requestJson.toString());
            log("Response: " + responseJson);
            return new ApiResult(responseJson);
        } catch (JSONException e) {
            throw new IOException(e);
        }
    }

    public ApiResult getSupportedStillQuality() throws IOException {
        String service = "camera";
        try {
            JSONObject requestJson =
                    new JSONObject().put("method", "getSupportedStillQuality") //
                            .put("params", new JSONArray()) //
                            .put("id", id()).put("version", "1.0");
            String url = findActionListUrl(service) + "/" + service;

            log("Request:  " + requestJson.toString());
            String responseJson = CameraHttpClient.httpPost(url, requestJson.toString());
            log("Response: " + responseJson);
            return new ApiResult(responseJson);
        } catch (JSONException e) {
            throw new IOException(e);
        }
    }


    /**
     * Calls getSourceList API to the target server. Request JSON data is such
     * like as below.
     * <p>
     * <pre>
     * {
     *   "method": "getSourceList",
     *   "params": [{
     *      "scheme": "storage"
     *      }],
     *   "id": 2,
     *   "version": "1.0"
     * }
     * </pre>
     *
     * @param scheme target scheme to get source
     * @return JSON data of response
     * @throws IOException all errors and exception are wrapped by this
     *                     Exception.
     */

    public JSONObject getSourceList(String scheme) throws IOException {
        String service = "avContent";
        try {

            JSONObject params = new JSONObject().put("scheme", scheme);
            JSONObject requestJson =
                    new JSONObject().put("method", "getSourceList") //
                            .put("params", new JSONArray().put(0, params)) //
                            .put("version", "1.0").put("id", id());

            String url = findActionListUrl(service) + "/" + service;

            log("Request:  " + requestJson.toString());
            String responseJson = CameraHttpClient.httpPost(url, requestJson.toString());
            log("Response: " + responseJson);
            return new JSONObject(responseJson);
        } catch (JSONException e) {
            throw new IOException(e);
        }
    }

    /**
     * Calls getContentList API to the target server. Request JSON data is such
     * like as below.
     * <p>
     * <pre>
     * {
     *   "method": "getContentList",
     *   "params": [{
     *      "sort" : "ascending"
     *      "view": "date"
     *      "uri": "storage:memoryCard1"
     *      }],
     *   "id": 2,
     *   "version": "1.3"
     * }
     * </pre>
     *
     * @param params request JSON parameter of "params" object.
     * @return JSON data of response
     * @throws IOException all errors and exception are wrapped by this
     *                     Exception.
     */

    public JSONObject getContentList(JSONArray params) throws IOException {
        String service = "avContent";
        try {

            JSONObject requestJson =
                    new JSONObject().put("method", "getContentList").put("params", params) //
                            .put("version", "1.3").put("id", id());

            String url = findActionListUrl(service) + "/" + service;

            log("Request:  " + requestJson.toString());
            String responseJson = CameraHttpClient.httpPost(url, requestJson.toString());
            log("Response: " + responseJson);
            return new JSONObject(responseJson);
        } catch (JSONException e) {
            throw new IOException(e);
        }
    }

    /**
     * Calls setStreamingContent API to the target server. Request JSON data is
     * such like as below.
     * <p>
     * <pre>
     * {
     *   "method": "setStreamingContent",
     *   "params": [
     *      "remotePlayType" : "simpleStreaming"
     *      "uri": "image:content?contentId=01006"
     *      ],
     *   "id": 2,
     *   "version": "1.0"
     * }
     * </pre>
     *
     * @param uri streaming contents uri
     * @return JSON data of response
     * @throws IOException all errors and exception are wrapped by this
     *                     Exception.
     */

    public JSONObject setStreamingContent(String uri) throws IOException {
        String service = "avContent";
        try {

            JSONObject params = new JSONObject().put("remotePlayType", "simpleStreaming").put(
                    "uri", uri);
            JSONObject requestJson =
                    new JSONObject().put("method", "setStreamingContent") //
                            .put("params", new JSONArray().put(0, params)) //
                            .put("version", "1.0").put("id", id());

            String url = findActionListUrl(service) + "/" + service;

            log("Request:  " + requestJson.toString());
            String responseJson = CameraHttpClient.httpPost(url, requestJson.toString());
            log("Response: " + responseJson);
            return new JSONObject(responseJson);
        } catch (JSONException e) {
            throw new IOException(e);
        }
    }

    /**
     * Calls startStreaming API to the target server. Request JSON data is such
     * like as below.
     * <p>
     * <pre>
     * {
     *   "method": "startStreaming",
     *   "params": [],
     *   "id": 2,
     *   "version": "1.0"
     * }
     * </pre>
     *
     * @return JSON data of response
     * @throws IOException all errors and exception are wrapped by this
     *                     Exception.
     */

    public JSONObject startStreaming() throws IOException {
        String service = "avContent";
        try {
            JSONObject requestJson =
                    new JSONObject().put("method", "startStreaming").put("params", new JSONArray()) //
                            .put("id", id()).put("version", "1.0").put("id", id());
            String url = findActionListUrl(service) + "/" + service;

            log("Request:  " + requestJson.toString());
            String responseJson = CameraHttpClient.httpPost(url, requestJson.toString());
            log("Response: " + responseJson);
            return new JSONObject(responseJson);
        } catch (JSONException e) {
            throw new IOException(e);
        }
    }

    // static method

    /**
     * Calls stopStreaming API to the target server. Request JSON data is such
     * like as below.
     * <p>
     * <pre>
     * {
     *   "method": "stopStreaming",
     *   "params": [],
     *   "id": 2,
     *   "version": "1.0"
     * }
     * </pre>
     *
     * @return JSON data of response
     * @throws IOException all errors and exception are wrapped by this
     *                     Exception.
     */

    public JSONObject stopStreaming() throws IOException {
        String service = "avContent";
        try {
            JSONObject requestJson =
                    new JSONObject().put("method", "stopStreaming").put("params", new JSONArray()) //
                            .put("id", id()).put("version", "1.0");
            String url = findActionListUrl(service) + "/" + service;

            log("Request:  " + requestJson.toString());
            String responseJson = CameraHttpClient.httpPost(url, requestJson.toString());
            log("Response: " + responseJson);
            return new JSONObject(responseJson);
        } catch (JSONException e) {
            throw new IOException(e);
        }
    }


    public ApiResult parseResult() {

        return null;
    }

    public ApiResult getAvailableFNumber() throws IOException {

        return getCameraSettingParm("getAvailableFNumber");
    }

    public ApiResult getAvailableShutterSpeed() throws IOException {
        return getCameraSettingParm("getAvailableShutterSpeed");
    }

    public ApiResult getAvailableExposureCompensation() throws IOException {

        return getCameraSettingParm("getAvailableExposureCompensation");
    }

    public ApiResult setFNumber(String value) throws IOException {
        return setCameraSettingParm("setFNumber", value);
    }

    public ApiResult setPostviewImageSize(String value) throws IOException {
        return setCameraSettingParm("setPostviewImageSize", value);
    }

    public ApiResult setShutterSpeed(String value) throws IOException {
        return setCameraSettingParm("setShutterSpeed", value);
    }

    public ApiResult getFocusMode()  throws IOException{
        return getCameraSettingParm("getFocusMode");
    }

    public static class CameraParameter {
        public static final String EXPOSURE_PROGRAM = "Program Auto";
        public static final String EXPOSURE_APERTURE_PRIORITY = "Aperture";
        public static final String EXPOSURE_SHUTTER_PRIORITY = "Shutter";

        public static final String EXPOSURE_MANUAL_EXPOSURE = "Manual";
        public static final String EXPOSURE_INTELLIGENT_AUTO = "Intelligent Auto";
        public static final String EXPOSURE_SUPERIOR_AUTO = "Superior Auto";
    }

    public class ApiResult {

        JSONObject resultJson;
        boolean isSuccess;

        Object error;
        Object result;

        public ApiResult(String json) {
            try {
                resultJson = new JSONObject(json);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            parseResponse();
        }

        public void parseResponse() {

            Iterator<String> keys = resultJson.keys();

            while (keys.hasNext()) {

                String key = (String) keys.next();

                try {

                    Object value = resultJson.get(key);

                    if (key.equals("result")) {
                        doOk(value);
                    }

                    if (key.equals("error")) {
                        doError(value);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        void doError(Object data) {
            isSuccess = false;
            error = data;

           /* if (data instanceof JSONArray){

                ArrayList<String> list = new ArrayList<String>();
                JSONArray jsonArray = (JSONArray )data;

                int len =jsonArray.length();

                for (int i=0;i<len;i++){
                    try {
                        list.add(jsonArray.get(i).toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                resultString = data.toString();
            } else if (data instanceof JSONObject){
                resultString = data.toString();
            } else {
                resultString = data.toString();
            }*/
        }

        void doOk(Object data) {
            isSuccess = true;
            result = data;
        }

        public String retrieveFNumber() {

            String fNum = getResultArrayFirstItem("0.0");
            getResultArrayFirstItem(fNum);
            return fNum;
        }

        public String retrieveExposure() {

            String mode = getResultArrayFirstItem("X");
            getResultArrayFirstItem(mode);
            return mode;
        }

        public String retrieveISO() {

            String iso = getResultArrayFirstItem("0");
            getResultArrayFirstItem(iso);
            return iso;
        }

        public ArrayList retrieveAvailableResultValue() throws JSONException {

            ArrayList list = null;

            JSONArray avalibleISO = new JSONArray(getResultArrayAllItem().get(1).toString());


            if (avalibleISO instanceof JSONArray) {

                list = JsonConvertUtils.convertJsonToArray(avalibleISO);
            }


            return list;
        }

        public ArrayList retrieveAvailableResultValue2() throws JSONException {

            ArrayList list = getResultArrayAllItem();



            return list;
        }



        public String retrieveResultValue() {

            String shortcut = getResultArrayFirstItem("0");
            getResultArrayFirstItem(shortcut);
            return shortcut;
        }

        public String getResultArrayFirstItem(String defaultString) {

            String item = defaultString;
            if (result instanceof JSONArray) {
                item = convetResultToList().get(0);
            }

            return item;
        }

        public ArrayList getResultArrayAllItem() throws JSONException {

            ArrayList list = null;

            if (result instanceof JSONArray) {

                JSONArray array = (JSONArray) result;

                list = JsonConvertUtils.convertJsonToArray(array);
            }

            return list;
        }

        public ArrayList<String> convetResultToList() {

            ArrayList<String> list = new ArrayList<String>();
            JSONArray jsonArray = (JSONArray) result;

            int len = jsonArray.length();

            for (int i = 0; i < len; i++) {
                try {
                    list.add(jsonArray.get(i).toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            return list;
        }
    }
}
