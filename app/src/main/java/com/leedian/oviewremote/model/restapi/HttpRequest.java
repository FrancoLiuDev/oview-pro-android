package com.leedian.oviewremote.model.restapi;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.List;

import android.content.Context;
import android.util.Log;

import com.blankj.utilcode.utils.EncryptUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.StringBody;

import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import com.leedian.oviewremote.model.dataIn.AuthCredentials;
import com.leedian.oviewremote.model.dataIn.Camera;
import com.leedian.oviewremote.model.dataIn.CameraMetaModel;
import com.leedian.oviewremote.model.dataIn.MetaJson;
import com.leedian.oviewremote.model.net.ApiConnection;
import com.leedian.oviewremote.utils.EncryptionHelper;
import com.leedian.oviewremote.utils.ProgressRequestBody;
import com.leedian.oviewremote.utils.UploadListener;

/**
 * a Http Request creator
 *
 * @author Franco
 */
class HttpRequest {
    private static final MediaType JSON_TYPE;
    private static final MediaType MULTIPART;

    static {
        JSON_TYPE = MediaType.parse("application/json; charset=utf-8");
        MULTIPART = MediaType.parse(" multipart/form-data");

    }

    private ApiConnection conn = null;

    HttpRequest() {

    }

    /**
     * Http Login Request
     *
     * @param credentials AuthCredentials model
     **/
    HttpRequest loginRequest(AuthCredentials credentials) throws Exception {

        String url = UrlMessagesConstants.strHttpServiceRoot() +
                UrlMessagesConstants.getRequestRestUrl(false, UrlMessagesConstants.IDENTIFIER_OVIEW_LOGIN);

        final JsonNodeFactory factory = JsonNodeFactory.instance;
        ObjectNode root = factory.objectNode();
        String key = UrlMessagesConstants.StrHttpServiceTripleDesKey;
        byte[] bytes = credentials.getPassword().getBytes("UTF-8");

        String mdString = EncryptUtils.encryptMD5ToString(bytes)
                .toLowerCase();
        String encryptedPassword = EncryptionHelper
                .des3EncodeECBtoBase64(key, mdString);

        root.put("user", credentials.getUsername());
        root.put("pwd", encryptedPassword);
        root.put("brand", UrlMessagesConstants.StrHttpServiceBrandName);
        RequestBody body = RequestBody.create(JSON_TYPE, getJson(root));
        conn = ApiConnection.instance(url, "Post", body);
        return this;
    }

    HttpRequest logoutRequest(String rsession) throws Exception {

        String url = UrlMessagesConstants.strHttpServiceRoot() +
                UrlMessagesConstants.getRequestRestUrl(false, UrlMessagesConstants.IDENTIFIER_OVIEW_LOGOUT);

        final JsonNodeFactory factory = JsonNodeFactory.instance;
        ObjectNode root = factory.objectNode();
        String key = UrlMessagesConstants.StrHttpServiceTripleDesKey;

        root.put("rsession", rsession);

        RequestBody body = RequestBody.create(JSON_TYPE, getJson(root));
        conn = ApiConnection.instance(url, "Post", body);
        return this;
    }



    /**
     * Get Oview List
     *
     * @param limit the limit
     * @param offset the offset
     **/
    HttpRequest getOviewPackageList(int limit, int offset) throws Exception {

        String url = UrlMessagesConstants.strHttpServiceRoot() +
                UrlMessagesConstants.getRequestRestUrl(true, UrlMessagesConstants.IDENTIFIER_CONTENT_PACK_LIST) +
                getLimitOffsetString(limit, offset);
        conn = ApiConnection.instance(url, "Get", null);
        return this;
    }

    /**
     * Delete Oview item in cloud
     *
     * @param zipKey the zipKey item
     * @param status the status
     **/
    HttpRequest deleteOviewItem(String zipKey, String status) throws Exception {

        String url = UrlMessagesConstants.strHttpServiceRoot() +
                UrlMessagesConstants
                        .getRequestRestUrl(false, UrlMessagesConstants.IDENTIFIER_CONTENT_ITEM) + "/" +
                zipKey;

        final JsonNodeFactory factory = JsonNodeFactory.instance;
        ObjectNode root = factory.objectNode();
        root.put("status", status);
        String jBody = getJson(root);
        RequestBody body = RequestBody.create(JSON_TYPE, jBody);
        conn = ApiConnection.instance(url, "Delete", body);
        return this;
    }

    /**
     * Download Content Images
     *
     * @param zipKey the zipKey item
     **/
    HttpRequest downloadContentImages(String zipKey) throws Exception {

        String url = UrlMessagesConstants.getFileDownloadRestUrl(zipKey);
        conn = ApiConnection.instance(url, "Download", null);
        return this;
    }

    /**
     * Request Oview Item Detail
     *
     * @param zipKey the zipKey item
     **/
    HttpRequest requestOviewItemDetail(String zipKey) throws Exception {

        String url = UrlMessagesConstants.strHttpServiceRoot() +
                UrlMessagesConstants
                        .getRequestRestUrl(false, UrlMessagesConstants.IDENTIFIER_CONTENT_ITEM) + "/" +
                zipKey;
        conn = ApiConnection.instance(url, "Get", null);
        return this;
    }

    /**
     * Update Oview item name
     *
     * @param zipKey the zipKey item
     * @param name the zipKey name
     **/
    HttpRequest updateOviewItemName(String zipKey, String name) throws Exception {

        String url = UrlMessagesConstants.strHttpServiceRoot() +
                UrlMessagesConstants.getRequestRestUrl(false, UrlMessagesConstants.IDENTIFIER_CONTENT_ITEM) +
                "/" + zipKey;

        final JsonNodeFactory factory = JsonNodeFactory.instance;
        ObjectNode root = factory.objectNode();
        root.put("name", name);
        String jBody = getJson(root);
        RequestBody body = RequestBody.create(JSON_TYPE, jBody);
        conn = ApiConnection.instance(url, "Patch", body);
        return this;
    }

    /**
     * Update  Oview item description
     *
     * @param zipKey the zipKey item
     * @param description the zipKey description
     **/
    HttpRequest updateObieItemDescription(String zipKey, String description) throws Exception {

        String url = UrlMessagesConstants.strHttpServiceRoot() +
                UrlMessagesConstants.getRequestRestUrl(false, UrlMessagesConstants.IDENTIFIER_CONTENT_ITEM) +
                "/" + zipKey;

        final JsonNodeFactory factory = JsonNodeFactory.instance;
        ObjectNode root = factory.objectNode();
        root.put("about", description);
        String jBody = getJson(root);
        RequestBody body = RequestBody.create(JSON_TYPE, jBody);
        conn = ApiConnection.instance(url, "Patch", body);
        return this;
    }

    HttpRequest exchangeToken(String session,String r_session) throws Exception {

        String url = UrlMessagesConstants.strHttpServiceRoot() +
                UrlMessagesConstants.getRequestRestUrl(false, UrlMessagesConstants.IDENTIFIER_OVIEW_RELOG);

        final JsonNodeFactory factory = JsonNodeFactory.instance;
        ObjectNode root = factory.objectNode();
        root.put("session", session);
        root.put("rsession", r_session);
        root.put("token", "abc123");

        String jBody = getJson(root);
        RequestBody body = RequestBody.create(JSON_TYPE, jBody);
        conn = ApiConnection.instance(url, "Post", body);
        return this;
    }
    HttpRequest checkAuth() throws Exception {

        String url = UrlMessagesConstants.strHttpServiceRoot() +
                UrlMessagesConstants.getRequestRestUrl(false, UrlMessagesConstants.IDENTIFIER_OVIEW_AUTH);

        final JsonNodeFactory factory = JsonNodeFactory.instance;
        ObjectNode root = factory.objectNode();

        root.put("token", "abc123");

        String jBody = getJson(root);
        RequestBody body = RequestBody.create(JSON_TYPE, jBody);

        conn = ApiConnection.instance(url, "Post", body);
        return this;
    }


    private String readFromFile(String file, Context context) {

        String ret = "";

        try {
            InputStream inputStream = context.openFileInput(file);

            if ( inputStream != null ) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();

                while ( (receiveString = bufferedReader.readLine()) != null ) {
                    stringBuilder.append(receiveString);
                }

                inputStream.close();
                ret = stringBuilder.toString();
            }
        }
        catch (FileNotFoundException e) {
            Log.e("login activity", "File not found: " + e.toString());
        } catch (IOException e) {
            Log.e("login activity", "Can not read file: " + e.toString());
        }

        return ret;
    }




    HttpRequest createROviewInstance(String cover, String file, CameraMetaModel model, final UploadListener uploadListener) throws Exception {

        String url = UrlMessagesConstants.strHttpServiceRoot() +
                UrlMessagesConstants.getRequestRestUrl(false, UrlMessagesConstants.IDENTIFIER_CONTENT_R360) +
                UrlMessagesConstants.IDENTIFIER_OVIEW_360FILE ;

        MultipartBody.Builder builder = new MultipartBody.Builder();

        final JsonNodeFactory factory = JsonNodeFactory.instance;

        ObjectMapper mapper = new ObjectMapper();

        String jsonInString = mapper.writeValueAsString(model);

       /* root.put("name", "R360");
        root.put("size", "243510");
        root.put("json", jsonInString);*/
        //RequestBody body = RequestBody.create(JSON_TYPE, getJson(root));
        RequestBody requestBody  =       RequestBody.create(MediaType.parse("image/jpeg"), new File(cover));
        RequestBody requestBodyZip  =       RequestBody.create(MediaType.parse("application/zip"), new File(file));
        RequestBody requestBodyJson  =       RequestBody.create(JSON_TYPE, jsonInString);

        MultipartBody body = builder
                .setType(MultipartBody.FORM)
                //.addPart(RequestBody.create(JSON_TYPE, jsonInString))
                .addPart(
                        Headers.of("Content-Disposition", "form-data; name=\"json\""),
                        RequestBody.create(JSON_TYPE, jsonInString))
                .addFormDataPart("zip","r360", requestBodyZip)
                .addFormDataPart("thumb","thumb", requestBody)

                .build();


        ProgressRequestBody progressRequestBody = new ProgressRequestBody(body,new ProgressRequestBody.Listener(){

            @Override
            public void onProgress(int progress) {
                int y= progress;
                Log.d("progress",String.valueOf(y));
                uploadListener.onProgress(y);
            }
        });

        conn = ApiConnection.instance(url, "Post", progressRequestBody);

        return this;
    }
//MultipartEntity multipartEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);k
    /*HttpRequest createROviewInstance(String cover, String file, CameraMetaModel model) throws Exception {

        String url = UrlMessagesConstants.strHttpServiceRoot() +
                UrlMessagesConstants.getRequestRestUrl(false, UrlMessagesConstants.IDENTIFIER_CONTENT_R360) +
                UrlMessagesConstants.IDENTIFIER_OVIEW_360FILE ;

        MultipartBody.Builder builder = new MultipartBody.Builder();

        final JsonNodeFactory factory = JsonNodeFactory.instance;

        ObjectMapper mapper = new ObjectMapper();



        model.setCamera(new Camera());
        model.setJson(new MetaJson());


        ObjectNode root = factory.objectNode();

        //String jsonInString = mapper.writeValueAsString(new MetaJson());



        String jsonInString = "\"{\"name\":\"Skinny Jeans\",\"about\":\"大腿部分經重度褪色和抓鬚處理，鈕扣褲檔\",\"size\":243510}\"";

        MultipartBody body = builder
                .setType(MultipartBody.FORM)
                .addPart(
                        Headers.of("Content-Disposition", "form-data; name=\"json\""),
                        RequestBody.create(JSON_TYPE, jsonInString))
                .addPart(
                        Headers.of("Content-Disposition", "form-data; name=\"zip\""),
                        RequestBody.create(MediaType.parse("application/x-zip-compressed"), new File(file)))
               .addPart(
                        Headers.of("Content-Disposition", "form-data; name=\"thumb\""),
                        RequestBody.create(MediaType.parse("image/jpeg"), new File(cover)))
                .build();



        ProgressRequestBody progressRequestBody = new ProgressRequestBody(body,new ProgressRequestBody.Listener(){

            @Override
            public void onProgress(int progress) {
                int y= progress;
                Log.d("progress",String.valueOf(progress));
            }
        });


        conn = ApiConnection.instance(url, "Post", progressRequestBody);

        return this;
    }*/


    /**
     * Get response
     *
     **/
    Response getResponse() throws Exception {

        return conn.GetResponse();
    }

    /**
     * Get response content length
     *
     **/
    Long getResponseContentLength() throws Exception {

        return this.getResponse().body().contentLength();
    }

    /**
     * Get response content stream
     *
     **/
    InputStream getResponseStream() throws Exception {

        return this.getResponse().body().byteStream();
    }

    /**
     * Connect Sync
     *
     **/
    boolean flag =false;

    public void setTestRelog()
    {
        flag =true;

    }

    boolean connectSync() throws Exception {

        if (flag) conn.setTestRelog();
        return conn.connectToApi();
    }

    /**
     * Get Limit & Offset string
     *
     * @param limit the limit
     * @param offset the offset
     **/
    private String getLimitOffsetString(int limit, int offset) {

        return "limit=" + Integer.toString(limit) + "&" + "offset=" + Integer.toString(offset);
    }

    /**
     * convert  ObjectNode to Json string
     *
     * @param dic the dic
     *            @return String
     **/
    private String getJson(ObjectNode dic) {

        String json = "";
        try {
            json = new ObjectMapper().writeValueAsString(dic);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return json;
    }

    /**
     * convert  List to Json string
     *
     * @param list the list
     * @return String
     **/
    private String getJsonList(List list) {

        final OutputStream out = new ByteArrayOutputStream();
        final ObjectMapper mapper = new ObjectMapper();
        String json = "";
        try {
            mapper.writeValue(out, list);
            json = String.valueOf(out);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return json;
    }
}
