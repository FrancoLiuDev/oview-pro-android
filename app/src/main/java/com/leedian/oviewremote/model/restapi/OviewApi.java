package com.leedian.oviewremote.model.restapi;

import java.io.InputStream;
import java.util.List;

import okhttp3.Response;
import com.leedian.oviewremote.model.dataIn.CameraMetaModel;
import com.leedian.oviewremote.model.dataout.OviewListModel;
import com.leedian.oviewremote.utils.JacksonConverter;
import com.leedian.oviewremote.utils.UploadListener;

/**
 * Oview Api
 *
 * @author Franco
 */
public class OviewApi
        extends HttpApiBase
{
    private List<OviewListModel> OviewList;

    private long                 responseContentLength;
    private InputStream inputStream;


    /**
     * Get response content length
     *
     * @return long
     **/
    public long getResponseContentLength() {

        return responseContentLength;
    }

    /**
     * Get Stream data Input
     *
     * @return InputStream
     **/
    public InputStream getInputStream() {

        return inputStream;
    }

    /**
     * Get Oview List data
     *
     * @return List
     **/
    public List<OviewListModel> getOviewList() {

        return OviewList;
    }



    /**
     * Retrieve Oview List data
     *
     * @param limit the limit
     * @param Offset the Offset
     * @return boolean
     **/
    public boolean retrieveOviewList(int limit, int Offset) throws Exception {

        boolean isSuccessCall;
        String nodeJson;
        String arrayNodeJson;

        try {
            JacksonConverter converter = new JacksonConverter<OviewListModel>(OviewListModel.class);
            isSuccessCall = getApiRequest().getOviewPackageList(limit, Offset).connectSync();
            Response response = this.getHttpResponse();

            if (isSuccessCall) {
                String Body = response.body().string();
                nodeJson = converter.getJsonStringFromJsonNodeName("data", Body);
                arrayNodeJson = converter.getJsonStringFromJsonNodeName("rows", nodeJson);
                OviewList = converter.getJsonObjectListObject(arrayNodeJson);
            } else {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }

        return true;
    }

    /**
     * Delete Item
     *
     * @param zipKey
     * @return boolean
     * @throws Exception
     */
    public boolean deleteItem(String zipKey) throws Exception {

        boolean isSuccessCall;
        try {
            isSuccessCall = getApiRequest().deleteOviewItem(zipKey, "1").connectSync();

            if (!isSuccessCall) {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }

        return true;
    }


    /**
     * Update Item Name
     *
     * @param zipKey
     * @param name
     * @return boolean
     * @throws Exception
     */
    public boolean updateItemName(String zipKey, String name) throws Exception {

        boolean isSuccessCall;
        try {
            isSuccessCall = getApiRequest().updateOviewItemName(zipKey, name).connectSync();
            Response response = this.getHttpResponse();

            if (!isSuccessCall) {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }

        return true;
    }

    /**
     * Download Item Content zipKey
     *
     * @param zipKey the zipKey
     * @return boolean
     * @throws Exception
     */
    public boolean downloadItemContent(String zipKey) throws Exception {

        boolean isSuccessCall;

        try {
            isSuccessCall = getApiRequest().downloadContentImages(zipKey).connectSync();
            Response response = this.getHttpResponse();

            if (!isSuccessCall) {
                return false;
            }

            inputStream = getApiRequest().getResponseStream();
            responseContentLength = getApiRequest().getResponseContentLength();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }

        return true;
    }

    /**
     * Update Item Description
     *
     * @param zipKey the zipKey
     * @param description item description
     * @return boolean
     * @throws Exception
     */
    public boolean updateItemDescription(String zipKey, String description) throws Exception {

        boolean isSuccessCall;

        try {
            isSuccessCall = getApiRequest().updateObieItemDescription(zipKey, description)
                                           .connectSync();
            Response response = this.getHttpResponse();
            if (!isSuccessCall) {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }

        return true;
    }

    public boolean createROviewInstance(String cover, String file, CameraMetaModel model, UploadListener uploadListener) throws Exception {

        boolean isSuccessCall;

        try {
            //getApiRequest().checkAuthToken();
            isSuccessCall = getApiRequest().createROviewInstance(cover,file, model,uploadListener)
                    .connectSync();

            Response response = this.getHttpResponse();


            if (!isSuccessCall) {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }

        return true;
    }

}

